package cd.backend.codegen;

import java.io.*;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cd.Config;
import cd.Main;
import cd.backend.ExitCode;
import cd.backend.codegen.RegisterManager.Register;
import cd.ir.Ast.ClassDecl;
import cd.ir.Symbol;

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

    private static final String CASTING_ASSEMBLY_PATH = "src/cd/backend/codegen/check_casts.s";

    public static final Integer INT_ARRAY_IDENTIFIER = 0b11;
    public static final Integer BOOLEAN_ARRAY_IDENTIFIER = 0b01;

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

        emit.emitRaw(".macro null_ptr_check ptr");
        emit.emit("test", "\\ptr", "\\ptr");
        emit.emit("je", "NULL_POINTER");
        emit.emitRaw(".endm");


        emit.emitRaw(Config.DATA_INT_SECTION);
        emit.emitRaw(".align 4");
        emit.emitLabel(LabelUtil.generateArrayLabelName("int"));
        emit.emitConstantData(LabelUtil.generateMethodTableLabelName(Symbol.ClassSymbol.objectType.name));

        emit.emitRaw(Config.DATA_INT_SECTION);
        emit.emitRaw(".align 4");
        emit.emitLabel(LabelUtil.generateArrayLabelName("boolean"));
        emit.emitConstantData(LabelUtil.generateMethodTableLabelName(Symbol.ClassSymbol.objectType.name));


        VTable objectTable = new VTable(Symbol.ClassSymbol.objectType);
        vTables.put(Symbol.ClassSymbol.objectType.name, objectTable);
        objectTable.emitStaticMethodVTable(emit);

        for (ClassDecl ast : astRoots) {
            VTable table = new VTable(ast.sym);
            vTables.put(ast.name, table);

        }

        for (ClassDecl ast : astRoots) {
            sg.gen(ast);
        }

        // Call Main function
        emit.emitRaw(Config.TEXT_SECTION);
        emit.emitLabel(Config.MAIN);
        emit.increaseIndent("Call startpoint");
        // Allocate Memory on Heap
        emit.emit("pushl", AssemblyEmitter.constant(Config.SIZEOF_PTR));
        emit.emit("pushl", AssemblyEmitter.constant(vTables.get("Main").getFieldCount()));
        emit.emit("call", Config.CALLOC);
        emit.emit("addl", AssemblyEmitter.constant(Config.SIZEOF_PTR*2), Register.ESP);

        emit.emitStore(AssemblyEmitter.labelAddress(LabelUtil.generateMethodTableLabelName("Main")), 0, Register.EAX);

        emit.emit("pushl", Register.EAX);
        emit.emitLoad(0, Register.EAX, Register.EAX);

        emit.emit("call", "*" + AssemblyEmitter.registerOffset(vTables.get("Main").getMethodOffset("main"), Register.EAX));
        emit.emit("addl", AssemblyEmitter.constant(Config.SIZEOF_PTR), Register.ESP);
//        emit.emit("xorl", Register.EAX, Register.EAX);
        emit.emitRaw("ret");

        // error code label
        emit.emitLabel("INVALID_DOWNCAST");
        emit.emit("pushl", AssemblyEmitter.constant(ExitCode.INVALID_DOWNCAST.value));
        emit.emit("call", "exit");

        emit.emitLabel("INVALID_ARRAY_STORE");
        emit.emit("pushl", AssemblyEmitter.constant(ExitCode.INVALID_ARRAY_STORE.value));
        emit.emit("call", "exit");


        emit.emitLabel("INVALID_ARRAY_BOUNDS");
        emit.emit("pushl", AssemblyEmitter.constant(ExitCode.INVALID_ARRAY_BOUNDS.value));
        emit.emit("call", "exit");


        emit.emitLabel("INVALID_ARRAY_SIZE");
        emit.emit("pushl", AssemblyEmitter.constant(ExitCode.INVALID_ARRAY_SIZE.value));
        emit.emit("call", "exit");


        emit.emitLabel("INFINITE_LOOP");
        emit.emit("pushl", AssemblyEmitter.constant(ExitCode.INFINITE_LOOP.value));
        emit.emit("call", "exit");


        emit.emitLabel("DIVISION_BY_ZERO");
        emit.emit("pushl", AssemblyEmitter.constant(ExitCode.DIVISION_BY_ZERO.value));
        emit.emit("call", "exit");

        emit.emitLabel("NULL_POINTER");
        emit.emit("pushl", AssemblyEmitter.constant(ExitCode.NULL_POINTER.value));
        emit.emit("call", "exit");


        emit.emitLabel("INTERNAL_ERROR");
        emit.emit("pushl", AssemblyEmitter.constant(ExitCode.INTERNAL_ERROR.value));
        emit.emit("call", "exit");


        emit.decreaseIndent();

        emit.emitRaw(loadCastingAssembly(CASTING_ASSEMBLY_PATH));

    }

    protected String loadCastingAssembly(String filename){
        String assembly;
        try(BufferedReader br = new BufferedReader(new FileReader(filename))){
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while(line != null){
                sb.append(line);
                sb.append(System.lineSeparator());
                line = br.readLine();
            }
            assembly = sb.toString();
            return MessageFormat.format(assembly, LabelUtil.generateMethodTableLabelName(Symbol.ClassSymbol.objectType.name), Config.EXIT, AssemblyEmitter.constant(ExitCode.INVALID_DOWNCAST.value));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
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