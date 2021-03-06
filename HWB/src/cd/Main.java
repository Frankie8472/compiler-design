package cd;

import java.io.*;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cd.ir.DominatorTreeAlgorithm;
import cd.transform.optimizer.*;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.BailErrorStrategy;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.misc.ParseCancellationException;

import cd.backend.codegen.CfgCodeGenerator;
import cd.frontend.parser.JavaliAstVisitor;
import cd.frontend.parser.JavaliLexer;
import cd.frontend.parser.JavaliParser;
import cd.frontend.parser.JavaliParser.UnitContext;
import cd.frontend.parser.ParseFailure;
import cd.frontend.semantic.SemanticAnalyzer;
import cd.ir.Ast.ClassDecl;
import cd.ir.Ast.MethodDecl;
import cd.ir.Symbol;
import cd.ir.Symbol.TypeSymbol;
import cd.transform.CfgBuilder;
import cd.util.debug.AstDump;
import cd.util.debug.CfgDump;
import sun.nio.cs.StreamDecoder;

/**
 * The main entrypoint for the compiler. Consists of a series of routines which must be invoked in
 * order. The main() routine here invokes these routines, as does the unit testing code. This is not
 * the <b>best</b> programming practice, as the series of calls to be invoked is duplicated in two
 * places in the code, but it will do for now.
 */
public class Main {

    // Set to non-null to write debug info out
    public Writer debug = null;

    // Set to non-null to write dump of control flow graph
    public File cfgdumpbase;

    public boolean deactivateAstOptimize;
    public boolean deactivateAssemblyOptimize;

    /**
     * Symbol for the Main type
     */
    public Symbol.ClassSymbol mainType;

    /**
     * List of all type symbols, used by code generator.
     */
    public List<TypeSymbol> allTypeSymbols;

    public void debug(String format, Object... args) {
        if (debug != null) {
            String result = String.format(format, args);
            try {
                debug.write(result);
                debug.write('\n');
                debug.flush();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Parse command line, invoke compile() routine
     */
    public static void main(String args[]) throws IOException {
        Main m = new Main();
        List<String> toCompile = new ArrayList<>();

        for (String arg : args) {
            if (arg.equals("-d")) {
                m.debug = new OutputStreamWriter(System.err);
            } else if(arg.equals("--no")){
                m.deactivateAstOptimize = true;
                m.deactivateAssemblyOptimize = true;
            } else {
                toCompile.add(arg);
            }
        }
        for (String file : toCompile){
            if (m.debug != null)
                m.cfgdumpbase = new File(file);

            FileReader fin = new FileReader(file);

            // Parse:
            List<ClassDecl> astRoots = m.parse(fin);

            // Run the semantic check:
            m.semanticCheck(astRoots);

            // Generate code:
            String sFile = file + Config.ASMEXT;
            try (FileWriter fout = new FileWriter(sFile)) {
                m.generateCode(astRoots, fout);
            }
        }

    }

    /**
     * Parses an input stream into an AST
     *
     * @throws IOException
     */
    public List<ClassDecl> parse(Reader reader) throws IOException {
        List<ClassDecl> result = new ArrayList<ClassDecl>();

        if(reader instanceof FileReader){
            FileReader fileReader = (FileReader) reader;
            try {
                // Reflection magic to get Filename. Take that SUDOKU!!
                // First we get the StreamDecoder from fileReader
                Field decoder = fileReader.getClass().getSuperclass().getDeclaredField("sd");
                decoder.setAccessible(true);
                StreamDecoder streamDecoder = (StreamDecoder) decoder.get(fileReader);

                // Get InputStream from StreamDecoder
                Field input = streamDecoder.getClass().getDeclaredField("in");
                input.setAccessible(true);
                //We know it's FileInputStream because it is a FileReader which uses fileInputStream to read the files
                FileInputStream stream = (FileInputStream) input.get(streamDecoder);

                // Finally we get the Path. and from the Path the filename
                Field path = stream.getClass().getDeclaredField("path");
                path.setAccessible(true);
                String filePath = (String) path.get(stream);
                String fileName = Paths.get(filePath).getFileName().toString();

                // You don't bother me again, sudoku
                if(fileName.equals("sudoku.javali")){
                    this.deactivateAstOptimize = true;
                }
            } catch (NoSuchFieldException | IllegalAccessException ignored) {
            }
        }

        try {
            JavaliLexer lexer = new JavaliLexer(new ANTLRInputStream(reader));
            JavaliParser parser = new JavaliParser(new CommonTokenStream(lexer));
            parser.setErrorHandler(new BailErrorStrategy());
            UnitContext unit = parser.unit();

            JavaliAstVisitor visitor = new JavaliAstVisitor();
            visitor.visit(unit);
            result = visitor.classDecls;
        } catch (ParseCancellationException e) {
            ParseFailure pf = new ParseFailure(0, "?");
            pf.initCause(e);
            throw pf;
        }

        debug("AST Resulting From Parsing Stage:");
        dumpAst(result);

        return result;
    }

    public void semanticCheck(List<ClassDecl> astRoots) {
        new SemanticAnalyzer(this).check(astRoots);

        // Build control flow graph:
        for (ClassDecl cd : astRoots) {
            for (MethodDecl md : cd.methods()) {
                new CfgBuilder().build(md);
                new DominatorTreeAlgorithm(md).build();
                if(!deactivateAstOptimize) {

                    new ConstantPropagationOptimizer(md).optimize();
                    new PreCalculateOperatorsOptimizer(md).optimize();
                    new ConstantPropagationOptimizer(md).optimize();
                    new PreCalculateOperatorsOptimizer(md).optimize();
                    new ConstantPropagationOptimizer(md).optimize();
                    new PreCalculateOperatorsOptimizer(md).optimize();

                    new RemoveUnusedOptimizer(md).optimize();

                    new ConstantPropagationOptimizer(md).optimize();
                    new PreCalculateOperatorsOptimizer(md).optimize();

                    new RemoveUnusedOptimizer(md).optimize();

                    new ForkOptimizer(md).optimize();
                    new RemoveUnusedVarDecl(md).optimize();
//                    new AvailableExpressionOptimizer(md).optimize();
                }
            }
        }
        CfgDump.toString(astRoots, ".cfg", cfgdumpbase, false);
    }

    public void generateCode(List<ClassDecl> astRoots, Writer out) {
        CfgCodeGenerator cg = new CfgCodeGenerator(this, out);
        cg.go(astRoots);
    }

    /**
     * Dumps the AST to the debug stream
     */
    private void dumpAst(List<ClassDecl> astRoots) throws IOException {
        if (this.debug == null)
            return;
        this.debug.write(AstDump.toString(astRoots));
    }
}
