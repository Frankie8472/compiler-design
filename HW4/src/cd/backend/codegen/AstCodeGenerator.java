package cd.backend.codegen;

import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cd.Config;
import cd.Main;
import cd.backend.codegen.RegisterManager.Register;
import cd.ir.Ast.ClassDecl;

public class AstCodeGenerator {

    protected RegsNeededVisitor rnv;

    protected ExprGenerator eg;
    protected StmtGenerator sg;

    protected final Main main;

    protected final AssemblyEmitter emit;
    protected final RegisterManager rm = new RegisterManager();

    protected final Map<String, VTable> vTables;

    AstCodeGenerator(Main main, Writer out) {
        initMethodData();
        this.emit = new AssemblyEmitter(out);
        this.main = main;
        this.rnv = new RegsNeededVisitor();


        this.eg = new ExprGenerator(this);
        this.sg = new StmtGenerator(this);
        vTables = new HashMap<>();
    }

    protected void debug(String format, Object... args) {
        this.main.debug(format, args);
    }

    public static AstCodeGenerator createCodeGenerator(Main main, Writer out) {
        return new AstCodeGenerator(main, out);
    }

    protected static final String VAR_PREFIX = "var_";

    protected static final String NEW_LINE_LABEL = "STR_NL";
    protected static final String DECIMAL_FORMAT_LABEL = "STR_D";

    /**
     * Main method. Causes us to emit x86 assembly corresponding to {@code ast}
     * into {@code file}. Throws a {@link RuntimeException} should any I/O error
     * occur.
     * <p>
     * <p>
     * The generated file will be divided into two sections:
     * <ol>
     * <li>Prologue: Generated by {@link #emitPrefix()}. This contains any
     * introductory declarations and the like.
     * <li>Body: Generated by {@link ExprGenerator}. This contains the main
     * method definitions.
     * </ol>
     */
    public void go(List<? extends ClassDecl> astRoots) {
        // Needs to be emitted only one time
        emit.emitRaw(".globl " + Config.MAIN);
        emit.emitRaw(Config.DATA_STR_SECTION);
        emit.emitLabel(NEW_LINE_LABEL);
        emit.emitRaw(Config.DOT_STRING + " \"\\n\"");
        emit.emitLabel(DECIMAL_FORMAT_LABEL);
        emit.emitRaw(Config.DOT_STRING + " \"%d\"");

        //DEBUG
        emit.emitLabel("var_test");
        emit.emitConstantData("0");

        for (ClassDecl ast : astRoots) {
            VTable table = new VTable(ast.sym);
            vTables.put(ast.name, table);

        }

        for (ClassDecl ast : astRoots) {
            sg.gen(ast);
        }

        // Call Main function
        emit.emitLabel(Config.MAIN);
        emit.emit("call", VTableManager.generateMethodLabelName("Main", "main"));

    }


    protected void initMethodData() {
        rm.initRegisters();
    }


    protected void emitMethodSuffix(boolean returnNull) {
        if (returnNull)
            emit.emit("movl", "$0", Register.EAX);
        emit.emitRaw("leave");
        emit.emitRaw("ret");
    }
}