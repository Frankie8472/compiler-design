package cd;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.*;

import cd.ir.BasicBlock;
import cd.ir.ControlFlowGraph;
import cd.transform.analysis.DataFlowAnalysis;
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

	/** Symbol for the Main type */
	public Symbol.ClassSymbol mainType;

	/** List of all type symbols, used by code generator. */
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

	/** Parse command line, invoke compile() routine */
	public static void main(String args[]) throws IOException {
		Main m = new Main();

		for (String arg : args) {
			if (arg.equals("-d"))
				m.debug = new OutputStreamWriter(System.err);
			else {
				if (m.debug != null)
					m.cfgdumpbase = new File(arg);

				FileReader fin = new FileReader(arg);

				// Parse:
				List<ClassDecl> astRoots = m.parse(fin);

				// Run the semantic check:
				m.semanticCheck(astRoots);

				// Generate code:
				String sFile = arg + Config.ASMEXT;
				try (FileWriter fout = new FileWriter(sFile)) {
					m.generateCode(astRoots, fout);
				}
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

				// Iterate through all blocks in a method
				for (BasicBlock block : md.cfg.allBlocks) {

					// Iterate through all definitions in a block
					for (String d : block.definition_set) {

						// Get variableName which is defined in d
						String var = md.cfg.definition_map.get(d);

						// Add d to gen set (as default)
						block.gen.add(d);

						// Iterate through all definition occurrences of var
						for (String defs : md.cfg.definition_set.get(var)) {
							if (!defs.equals(d)) {

								// Remove from gen set if gen contains defs and is not equals d
								if (block.gen.contains(defs)) {
									block.gen.remove(defs);
								}

								// Add to kill set if not equals d
								if (!block.kill.contains(defs)) {
									block.kill.add(defs);
								}
							}
						}
					}
				}


				/* DataFlowAnalysis for the current method
				 * State = Set<String> new HashSet<>() = {d_1,d_2, ...}
				 */

				new DataFlowAnalysis<Set<String>>(md.cfg) {

					/**
					 * OUT[Bi] = U (U is the set of all expressions that appear in the program
					 * @return all definitions this method contains
					 */
					@Override
					protected Set<String> initialState() {
						return cfg.definition_map.keySet();
					}

					/**
					 * Safe assumption OUT[ENTRY] = ∅
					 * @return empty hash_set
					 */
					@Override
					protected Set<String> startState() {
						return new HashSet<>();
					}

					/**
					 * OUT(B) = gen_B ∪ (IN(B) – kill_B )
					 * @param block containing it's kill and gen set
					 * @param inState as List of definitions (IN(B))
					 * @return OUT(B)
					 */
					@Override
					protected Set<String> transferFunction(BasicBlock block, Set<String> inState) {
						Set<String> ret = inState;
						ret.removeAll(block.kill);
						ret.addAll(block.gen);
						return ret;
					}

					/**
					 * Simple join function for HashSets <br>
					 * A Set does not contain duplicates by nature.
					 * @param sets List of definition sets
					 * @return concatenation of all definition sets
					 */
					@Override
					protected Set<String> join(Set<Set<String>> sets) {
						Set<String> ret = new HashSet<>();
						sets.forEach(ret::addAll);
						return ret;
					}
				};
			}
		}
		CfgDump.toString(astRoots, ".cfg", cfgdumpbase, false);
	}

	public void generateCode(List<ClassDecl> astRoots, Writer out) {
		CfgCodeGenerator cg = new CfgCodeGenerator(this, out);
		cg.go(astRoots);
	}

	/** Dumps the AST to the debug stream */
	private void dumpAst(List<ClassDecl> astRoots) throws IOException {
		if (this.debug == null)
			return;
		this.debug.write(AstDump.toString(astRoots));
	}
}
