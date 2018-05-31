package cd.backend.codegen;

import static cd.Config.MAIN;
import static cd.Config.SCANF;
import static cd.Config.SIZEOF_PTR;
import static cd.backend.codegen.AssemblyEmitter.constant;
import static cd.backend.codegen.AssemblyEmitter.registerOffset;
import static cd.backend.codegen.RegisterManager.STACK_REG;
import static cd.backend.codegen.RegisterManager.BASE_REG;

import java.io.Writer;
import java.util.Collections;
import java.util.List;

import cd.Config;
import cd.Main;
import cd.backend.ExitCode;
import cd.backend.codegen.RegisterManager.Register;
import cd.ir.Ast;
import cd.ir.Ast.ClassDecl;
import cd.ir.Ast.Expr;
import cd.ir.Ast.MethodDecl;
import cd.ir.Symbol.ArrayTypeSymbol;
import cd.ir.Symbol.ClassSymbol;
import cd.ir.Symbol.MethodSymbol;
import cd.ir.Symbol.TypeSymbol;
import cd.ir.Symbol.VariableSymbol;

public class AstCodeGenerator {

    protected RegsNeededVisitor rnv;

    protected ExprGenerator eg;
    protected StmtGenerator sg;

    protected final Main main;

    protected final AssemblyEmitter emit;
    protected final RegisterManager rm = new RegisterManager();

    protected ExprGeneratorRef egRef;
    protected StmtGeneratorRef sgRef;

    AstCodeGenerator(Main main, Writer out) {
        initMethodData();

        this.emit = new AssemblyEmitter(out);
        this.main = main;
        this.rnv = new RegsNeededVisitor();

        this.eg = new ExprGenerator(this);
        this.sg = new StmtGenerator(this);
    }

    protected void debug(String format, Object... args) {
        this.main.debug(format, args);
    }

    public static AstCodeGenerator createCodeGenerator(Main main, Writer out) {
        return new AstCodeGeneratorRef(main, out);
    }


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
        for (ClassDecl ast : astRoots) {
            sg.gen(ast, null);
        }
    }


    protected void initMethodData() {
        rm.initRegisters();
    }


    protected void emitMethodSuffix(boolean returnNull) {
        if (returnNull)
            emit.emit("movl", "$0", Register.EAX);
        emit.emitMove(BASE_REG, STACK_REG);
        emit.emit("pop", BASE_REG);
        emit.emitRaw("ret");
    }
}

class AstCodeGeneratorOpt extends AstCodeGeneratorRef {

    public AstCodeGeneratorOpt(Main main, Writer out) {
        super(main, out);
        this.sg = new StmtGeneratorOpt(this);
        this.eg = new ExprGeneratorOpt(this);
    }

    @Override
    protected void emitNullCheck(Register toCheck, Expr exprToCheck, CurrentContext context) {
        // Does not work if object is checked in one if branch but not in other and is in fact null but the branch
        // without check is taken. The check of the variable has been done in the other branch and so the check will not
        // be done after the if statement. But when the variable is null the check would be necessary...

        if (exprToCheck instanceof Ast.ThisRef) {
            return;
        } else if (exprToCheck instanceof Ast.Var) {
            String varName = ((Ast.Var) exprToCheck).name;
            if (context.isKnownObjectAccess(varName)) {
                return;
            }
            context.addObjectAccess(varName);
        }

        //you emit the check with this statement:
        super.emitNullCheck(toCheck, exprToCheck, context);
        // check the superclass, there you find the implementation.
    }

    @Override
    protected void emitArrayBoundsCheck(Register reference, Register index, Ast.Index exprToCheck, CurrentContext context) {
        //TODO: Do not emit if not necessary.

        if (exprToCheck.left() instanceof Ast.Var && exprToCheck.right() instanceof Ast.IntConst) {
            Ast.Var arrVar = (Ast.Var) exprToCheck.left();
            Integer indexAccess = ((Ast.IntConst) exprToCheck.right()).value;
            if (context.isKnownArrayAccess(arrVar.name, indexAccess)) {
                context.addArrayAccess(arrVar.name, indexAccess);
                return;
            }
        }

        if (exprToCheck.left() instanceof Ast.Var && exprToCheck.right() instanceof Ast.Var) {
            Ast.Var arrVar = (Ast.Var) exprToCheck.left();
            Ast.Var var = ((Ast.Var) exprToCheck.right());
            if (context.isKnownArrayAccess(arrVar.name, var.name)) {
                context.addArrayAccess(arrVar.name, var.name);
                return;
            }
        }

        //you emit the check with this statement:
        super.emitArrayBoundsCheck(reference, index, exprToCheck, context);
        // check the superclass, there you find the implementation.
    }
}

class AstCodeGeneratorRef extends AstCodeGenerator {
    /**
     * The address of the this ptr relative to the BP. Note that the this ptr is
     * always the first argument. Default offset value is 8 but this can change
     * depending on the number of parameters pushed on the stack.
     */
    protected int THIS_OFFSET = 8;

    /**
     * Name of the internal Javali$CheckCast() helper function we generate.
     */
    static final String CHECK_CAST = "Javali$CheckCast";

    /**
     * Name of the internal Javali$CheckNull() helper function we generate.
     */
    static final String CHECK_NULL = "Javali$CheckNull";

    /**
     * Name of the internal Javali$CheckNonZero() helper function we generate.
     */
    static final String CHECK_NON_ZERO = "Javali$CheckNonZero";

    /**
     * Name of the internal Javali$CheckArraySize() helper function we generate.
     */
    static final String CHECK_ARRAY_SIZE = "Javali$CheckArraySize";

    /**
     * Name of the internal Javali$CheckArrayBounds() helper function we
     * generate.
     */
    static final String CHECK_ARRAY_BOUNDS = "Javali$CheckArrayBounds";

    /**
     * Name of the internal Javali$Alloc() helper function we generate.
     */
    static final String ALLOC = "Javali$Alloc";

    /**
     * Name of the internal Javali$PrintNewLine() helper function we generate.
     */
    static final String PRINT_NEW_LINE = "Javali$PrintNewLine";

    /**
     * Name of the internal Javali$PrintInteger() helper function we generate.
     */
    static final String PRINT_INTEGER = "Javali$PrintInteger";

    /**
     * Name of the internal Javali$ReadInteger() helper function we generate.
     */
    static final String READ_INTEGER = "Javali$ReadInteger";

    public AstCodeGeneratorRef(Main main, Writer out) {
        super(main, out);

        this.egRef = new ExprGeneratorRef(this);
        this.eg = this.egRef;
        this.sgRef = new StmtGeneratorRef(this);
        this.sg = this.sgRef;
    }


    protected void emitPrefix(List<? extends ClassDecl> astRoots) {
        // compute method and field offsets
        for (ClassDecl ast : astRoots) {
            computeFieldOffsets(ast.sym);
            computeVtableOffsets(ast.sym);
        }

        // emit vtables
        for (TypeSymbol ts : main.allTypeSymbols)
            emitVtable(ts);

        // Emit some useful string constants and static data:
        emit.emitRaw(Config.DATA_STR_SECTION);
        emit.emitLabel("STR_NL");
        emit.emitRaw(Config.DOT_STRING + " \"\\n\"");
        emit.emitLabel("STR_D");
        emit.emitRaw(Config.DOT_STRING + " \"%d\"");
        emit.emitLabel("STR_F");
        emit.emitRaw(Config.DOT_STRING + " \"%.5f\"");
        emit.emitLabel("SCANF_STR_F");
        emit.emitRaw(Config.DOT_STRING + " \"%f\"");
        emit.emitRaw(Config.DATA_INT_SECTION);

        emit.emitRaw(Config.TEXT_SECTION);

        // Generate a helper method for checking casts:
        // It takes first a vtable and second an object ptr.
        {
            Register obj = RegisterManager.CALLER_SAVE[0];
            Register cls = RegisterManager.CALLER_SAVE[1];
            String looplbl = emit.uniqueLabel();
            String donelbl = emit.uniqueLabel();
            String faillbl = emit.uniqueLabel();
            emit.emitCommentSection(CHECK_CAST + " function");
            emit.emitLabel(CHECK_CAST);

            emit.emit("push", BASE_REG);
            emit.emitMove(STACK_REG, BASE_REG);
            emit.emit("sub", constant(8), STACK_REG);

            emit.emit("and", constant(-16), STACK_REG);
            emit.emit("sub", constant(16), STACK_REG);
            emit.emitLoad(SIZEOF_PTR * 2, BASE_REG, cls);
            emit.emitLoad(SIZEOF_PTR * 3, BASE_REG, obj);
            emit.emit("cmpl", constant(0), obj);
            emit.emit("je", donelbl); // allow null objects to pass
            emit.emitLoad(0, obj, obj); // load vtbl of object
            emit.emitLabel(looplbl);
            emit.emit("cmpl", obj, cls);
            emit.emit("je", donelbl);
            emit.emit("cmpl", constant(0), obj);
            emit.emit("je", faillbl);
            emit.emitLoad(0, obj, obj); // load parent vtable
            emit.emit("jmp", looplbl);
            emit.emitLabel(faillbl);
            emit.emitStore(constant(ExitCode.INVALID_DOWNCAST.value), 0, STACK_REG);
            emit.emit("call", Config.EXIT);
            emit.emitLabel(donelbl);
            emit.emitMove(BASE_REG, STACK_REG);
            emit.emit("pop", BASE_REG);
            emit.emitRaw("ret");
        }

        // Generate a helper method for checking for null ptrs:
        {
            String oknulllbl = emit.uniqueLabel();
            emit.emitCommentSection(CHECK_NULL + " function");
            emit.emitLabel(CHECK_NULL);

            emit.emit("push", BASE_REG);
            emit.emitMove(STACK_REG, BASE_REG);
            emit.emit("sub", constant(8), STACK_REG);

            emit.emit("and", constant(-16), STACK_REG);
            emit.emit("sub", constant(16), STACK_REG);
            emit.emit("cmpl", constant(0), registerOffset(SIZEOF_PTR * 2, BASE_REG));
            emit.emit("jne", oknulllbl);
            emit.emitStore(constant(ExitCode.NULL_POINTER.value), 0, STACK_REG);
            emit.emit("call", Config.EXIT);
            emit.emitLabel(oknulllbl);
            emit.emitMove(BASE_REG, STACK_REG);
            emit.emit("pop", BASE_REG);
            emit.emitRaw("ret");
        }

        // Generate a helper method for checking that we don't divide by zero:
        {
            String oknzlbl = emit.uniqueLabel();
            emit.emitCommentSection(CHECK_NON_ZERO + " function");
            emit.emitLabel(CHECK_NON_ZERO);

            emit.emit("push", BASE_REG);
            emit.emitMove(STACK_REG, BASE_REG);
            emit.emit("sub", constant(8), STACK_REG);

            emit.emit("and", constant(-16), STACK_REG);
            emit.emit("sub", constant(16), STACK_REG);
            emit.emit("cmpl", constant(0), registerOffset(SIZEOF_PTR * 2, BASE_REG));
            emit.emit("jne", oknzlbl);
            emit.emitStore(constant(ExitCode.DIVISION_BY_ZERO.value), 0, STACK_REG);
            emit.emit("call", Config.EXIT);
            emit.emitLabel(oknzlbl);
            emit.emitMove(BASE_REG, STACK_REG);
            emit.emit("pop", BASE_REG);
            emit.emitRaw("ret");
        }

        // Generate a helper method for checking array size:
        {
            String okunqlbl = emit.uniqueLabel();
            emit.emitCommentSection(CHECK_ARRAY_SIZE + " function");
            emit.emitLabel(CHECK_ARRAY_SIZE);

            emit.emit("push", BASE_REG);
            emit.emitMove(STACK_REG, BASE_REG);
            emit.emit("sub", constant(8), STACK_REG);

            emit.emit("and", constant(-16), STACK_REG);
            emit.emit("sub", constant(16), STACK_REG);
            emit.emit("cmpl", constant(0), registerOffset(SIZEOF_PTR * 2, BASE_REG));
            emit.emit("jge", okunqlbl);
            emit.emitStore(constant(ExitCode.INVALID_ARRAY_SIZE.value), 0, STACK_REG);
            emit.emit("call", Config.EXIT);
            emit.emitLabel(okunqlbl);
            emit.emitMove(BASE_REG, STACK_REG);
            emit.emit("pop", BASE_REG);
            emit.emitRaw("ret");
        }

        // Generate a helper method for checking array bounds:
        {
            Register arr = RegisterManager.CALLER_SAVE[0];
            Register idx = RegisterManager.CALLER_SAVE[1];
            String faillbl = emit.uniqueLabel();
            emit.emitCommentSection(CHECK_ARRAY_BOUNDS + " function");
            emit.emitLabel(CHECK_ARRAY_BOUNDS);

            emit.emit("push", BASE_REG);
            emit.emitMove(STACK_REG, BASE_REG);
            emit.emit("sub", constant(8), STACK_REG);

            emit.emit("and", constant(-16), STACK_REG);
            emit.emit("sub", constant(16), STACK_REG);
            emit.emitLoad(SIZEOF_PTR * 3, BASE_REG, idx);
            emit.emitLoad(SIZEOF_PTR * 2, BASE_REG, arr);
            emit.emit("cmpl", constant(0), idx); // idx < 0
            emit.emit("jl", faillbl);
            emit.emit("cmpl", registerOffset(Config.SIZEOF_PTR, arr), idx); // idx >= len
            emit.emit("jge", faillbl);
            // done
            emit.emitMove(BASE_REG, STACK_REG);
            emit.emit("pop", BASE_REG);
            emit.emitRaw("ret");
            // fail
            emit.emitLabel(faillbl);
            emit.emitStore(constant(ExitCode.INVALID_ARRAY_BOUNDS.value), 0, STACK_REG);
            emit.emit("call", Config.EXIT);

        }

        // Generate a helper method for allocating objects/arrays
        {
            Register size = RegisterManager.CALLER_SAVE[0];
            emit.emitCommentSection(ALLOC + " function");
            emit.emitLabel(ALLOC);

            emit.emit("push", BASE_REG);
            emit.emitMove(STACK_REG, BASE_REG);
            emit.emit("sub", constant(8), STACK_REG);

            emit.emit("and", constant(-16), STACK_REG);
            emit.emit("sub", constant(16), STACK_REG);
            emit.emitLoad(8, BASE_REG, size);
            emit.emitStore(size, 0, STACK_REG);
            emit.emitStore(constant(1), 4, STACK_REG);
            emit.emit("call", Config.CALLOC);
            emit.emitMove(BASE_REG, STACK_REG);
            emit.emit("pop", BASE_REG);
            emit.emitRaw("ret");
        }

        // Generate a helper method for printing a new line
        {
            emit.emitCommentSection(PRINT_NEW_LINE + " function");
            emit.emitLabel(PRINT_NEW_LINE);

            emit.emit("push", BASE_REG);
            emit.emitMove(STACK_REG, BASE_REG);
            emit.emit("sub", constant(8), STACK_REG);

            emit.emit("and", constant(-16), STACK_REG);
            emit.emit("sub", constant(16), STACK_REG);
            emit.emitStore("$STR_NL", 0, STACK_REG);
            emit.emit("call", Config.PRINTF);
            emit.emitMove(BASE_REG, STACK_REG);
            emit.emit("pop", BASE_REG);
            emit.emitRaw("ret");
        }

        // Generate a helper method for printing an integer
        {
            Register temp = RegisterManager.CALLER_SAVE[0];
            emit.emitCommentSection(PRINT_INTEGER + " function");
            emit.emitLabel(PRINT_INTEGER);

            emit.emit("push", BASE_REG);
            emit.emitMove(STACK_REG, BASE_REG);
            emit.emit("sub", constant(8), STACK_REG);

            emit.emit("and", constant(-16), STACK_REG);
            emit.emit("sub", constant(16), STACK_REG);
            emit.emitLoad(8, BASE_REG, temp);
            emit.emitStore(temp, 4, STACK_REG);
            emit.emitStore("$STR_D", 0, STACK_REG);
            emit.emit("call", Config.PRINTF);
            emit.emitMove(BASE_REG, STACK_REG);
            emit.emit("pop", BASE_REG);
            emit.emitRaw("ret");
        }

        // Generate a helper method for reading an integer
        {
            Register number = RegisterManager.CALLER_SAVE[0];
            emit.emitCommentSection(READ_INTEGER + " function");
            emit.emitLabel(READ_INTEGER);

            emit.emit("push", BASE_REG);
            emit.emitMove(STACK_REG, BASE_REG);
            emit.emit("sub", constant(8), STACK_REG);

            emit.emit("and", constant(-16), STACK_REG);
            emit.emit("sub", constant(16), STACK_REG);
            emit.emit("leal", registerOffset(8, STACK_REG), number);
            emit.emitStore(number, 4, STACK_REG);
            emit.emitStore("$STR_D", 0, STACK_REG);
            emit.emit("call", SCANF);
            emit.emitLoad(8, STACK_REG, Register.EAX);
            emit.emitMove(BASE_REG, STACK_REG);
            emit.emit("pop", BASE_REG);
            emit.emitRaw("ret");
        }

        // Generate AST for main() method:
        // new Main().main()
        Ast.NewObject newMain = new Ast.NewObject("Main");
        newMain.type = main.mainType;

        Ast.MethodCallExpr mce = new Ast.MethodCallExpr(newMain, "main", Collections.<Expr>emptyList());
        Ast.MethodCall callMain = new Ast.MethodCall(mce);
        mce.sym = main.mainType.getMethod("main");

        // Emit the main() method:
        // new Main().main();
        emit.emitCommentSection("main() function");
        emit.emitRaw(".globl " + MAIN);
        emit.emitLabel(MAIN);

        emit.emit("push", BASE_REG);
        emit.emitMove(STACK_REG, BASE_REG);
        emit.emit("sub", constant(8), STACK_REG);

        emit.emit("and", -16, STACK_REG);
        sg.gen(callMain, null);
        emit.emit("movl", constant(ExitCode.OK.value), Register.EAX); // normal termination:
        emit.emitMove(BASE_REG, STACK_REG);
        emit.emit("pop", BASE_REG);
        emit.emitRaw("ret");

    }

    @Override
    public void go(List<? extends ClassDecl> astRoots) {
        emitPrefix(astRoots);
        super.go(astRoots);
    }

    /**
     * Computes the vtable offset for each method defined in the class
     * {@code sym}.
     */

    protected int computeVtableOffsets(ClassSymbol sym) {

        if (sym == null)
            return 0;

        if (sym.totalMethods != -1)
            return sym.totalMethods;

        int index = computeVtableOffsets(sym.superClass);
        for (MethodSymbol ms : sym.methods.values()) {
            assert ms.vtableIndex == -1;
            if (ms.overrides != null)
                ms.vtableIndex = ms.overrides.vtableIndex;
            else
                ms.vtableIndex = index++;
        }
        sym.totalMethods = index;
        return index;
    }

    /**
     * Computes the offset for each field.
     */

    protected int computeFieldOffsets(ClassSymbol sym) {
        if (sym == null)
            return 0;

        if (sym.totalFields != -1)
            return sym.totalFields;

        int index = computeFieldOffsets(sym.superClass);
        for (VariableSymbol fs : sym.fields.values()) {
            assert fs.offset == -1;
            // compute offset in bytes; note that 0 is the vtable
            fs.offset = (index * SIZEOF_PTR) + SIZEOF_PTR;
            index++;
        }
        sym.totalFields = index;
        sym.sizeof = (sym.totalFields + 1) * Config.SIZEOF_PTR;
        return index;
    }

    private void collectVtable(MethodSymbol[] vtable, ClassSymbol sym) {
        if (sym.superClass != null)
            collectVtable(vtable, sym.superClass);
        for (MethodSymbol ms : sym.methods.values())
            vtable[ms.vtableIndex] = ms;
    }

    protected void emitVtable(TypeSymbol ts) {
        if (ts instanceof ClassSymbol) {
            ClassSymbol cs = (ClassSymbol) ts;

            // Collect the vtable:
            MethodSymbol[] vtable = new MethodSymbol[cs.totalMethods];
            collectVtable(vtable, cs);

            // Emit vtable for this class:
            emit.emitLabel(vtable(cs));
            if (cs.superClass != null)
                emit.emitConstantData(vtable(cs.superClass));
            else
                emit.emitConstantData("0");
            for (int i = 0; i < cs.totalMethods; i++)
                emit.emitConstantData(methodLabel(vtable[i]));
        } else if (ts instanceof ArrayTypeSymbol) {
            ArrayTypeSymbol as = (ArrayTypeSymbol) ts;
            emit.emitLabel(vtable(as));
            emit.emitConstantData(vtable(ClassSymbol.objectType));
        }
    }

    protected String vtable(TypeSymbol ts) {
        if (ts instanceof ClassSymbol) {
            return "vtable_" + ((ClassSymbol) ts).name;
        } else if (ts instanceof ArrayTypeSymbol) {
            return "vtablearr_" + ((ArrayTypeSymbol) ts).elementType.name;
        } else {
            throw new RuntimeException("No vtable for " + ts.name);
        }
    }


    @Override
    protected void initMethodData() {
        THIS_OFFSET = 8;
        bytes = 0;
        super.initMethodData();
    }

    protected int padding(int numberOfParameters) {
        int padding = (bytes + numberOfParameters * Config.SIZEOF_PTR + 15) & 0xFFFFFFF0;
        return padding - bytes - numberOfParameters * Config.SIZEOF_PTR;
    }

    protected void push(int padding) {
        if (padding > 0) {
            emit.emit("sub", padding, STACK_REG);
            bytes += padding;
        }
    }

    protected void pop(int padding) {
        if (padding > 0) {
            emit.emit("add", padding, STACK_REG);
            bytes -= padding;
        }
        assert bytes >= 0;
    }

    protected void push(String reg) {
        emit.emit("push", reg);
        bytes += Config.SIZEOF_PTR;
    }

    protected void pop(String reg) {
        emit.emit("pop", reg);
        bytes -= Config.SIZEOF_PTR;
        assert bytes >= 0;
    }

    protected void restoreCalleeSaveRegs() {
        for (int reg = RegisterManager.CALLEE_SAVE.length - 1; reg >= 0; reg--) {
            emit.emit("pop", RegisterManager.CALLEE_SAVE[reg]);
        }
    }

    protected void storeCalleeSaveRegs() {
        bytes = 0;
        for (int reg = 0; reg < RegisterManager.CALLEE_SAVE.length; reg++) {
            emit.emit("push", RegisterManager.CALLEE_SAVE[reg]);
            bytes += Config.SIZEOF_PTR;
        }
    }

    protected void restoreCallerSaveRegs(Register res) {
        for (int reg = RegisterManager.CALLER_SAVE.length - 1; reg >= 0; reg--) {
            if (!rm.isInUse(RegisterManager.CALLER_SAVE[reg]))
                continue; // not in use
            if (RegisterManager.CALLER_SAVE[reg].equals(res))
                continue; // contains our result
            pop(RegisterManager.CALLER_SAVE[reg].repr);
        }
    }

    protected void storeCallerSaveRegs(Register res) {
        for (int reg = 0; reg < RegisterManager.CALLER_SAVE.length; reg++) {
            if (!rm.isInUse(RegisterManager.CALLER_SAVE[reg]))
                continue; // not in use
            if (RegisterManager.CALLER_SAVE[reg].equals(res))
                continue; // will contain our result
            push(RegisterManager.CALLER_SAVE[reg].repr);
        }
    }

    protected int emitCallPrefix(Register res, int numberOfParameters) {
        storeCallerSaveRegs(res);
        int padding = padding(numberOfParameters);
        push(padding);
        return padding;
    }

    protected void emitCallSuffix(Register res, int numberOfParameters,
                                  int padding) {
        pop(numberOfParameters * Config.SIZEOF_PTR + padding);
        if (res != null) {
            emit.emitMove(Register.EAX, res);
        }
        restoreCallerSaveRegs(res);
    }


    /**
     * Generates code which evaluates {@code ast} and branches to {@code lbl} if
     * the value generated for {@code ast} is false.
     */

    protected void genJumpIfFalse(Expr ast, String lbl, CurrentContext context) {
        // A better way to implement this would be with a separate
        // visitor.
        Register reg = eg.gen(ast, context);
        emit.emit("cmpl", "$0", reg);
        emit.emit("je", lbl);
        rm.releaseRegister(reg);
    }


    /**
     * Used to store the temporaries. We grow our stack dynamically, we allocate
     * "temporary" values on this stack during method execution. Values can be
     * stored and retrieved using {@link #push(String)} and {@link #pop(String)}
     * , which use the program stack.
     */

    protected int bytes = 0;

    protected String methodLabel(MethodSymbol msym) {
        return "meth_" + msym.owner.name + "_" + msym.name;
    }

    protected void emitMethodPrefix(MethodDecl ast) {

        // Emit the label for the method:
        emit.emitRaw(Config.TEXT_SECTION);
        emit.emitCommentSection(String.format("Method %s.%s", ast.sym.owner.name,
                ast.name));
        emit.emitRaw(".globl " + methodLabel(ast.sym));
        emit.emitLabel(methodLabel(ast.sym));

        // Compute the size and layout of the stack frame. Our
        // frame looks like (the numbers are relative to our ebp):
        //
        // (caller's locals)
        // (padding)
        // arg 0 (this ptr)
        // ...
        // 12 arg N - 1
        // 8 arg N
        // 4 linkage ptr (return address)
        // 0 saved ebp
        // -4 locals
        // (callee's arguments + temporaries)
        //
        // We allocate on the stack during the course of
        // a function call using push(...) and pop(...) instructions.
        //
        // Stack slots fall into several
        // categories:
        // - "Linkage": overhead for function calls.
        // This includes the return address and saved ebp.
        // - locals: these store the value of user-declared local
        // variables.
        // - temporaries: these are stack slots used to store
        // values during expression evaluation when we run out
        // of registers, saving caller-saved registers, and
        // other miscellaneous purposes.
        // - padding: only there to ensure the stack size is a multiple
        // of 16.
        // - arguments: values we will pass to functions being
        // invoked.
        //
        // We calculate all address relative to the base pointer.

        // Initialize method-specific data
        initMethodData();

        // Assign parameter offsets:
        // As shown above, these start from 8.
        // Being able to evaluate parameters like in Java
        // with left-to-right evaluation order they result
        // on the stack in reversed order.
        // The "this" parameter is the first pushed on the stack
        // thus receiving higher offset.
        int paramOffset = Config.SIZEOF_PTR * 2;
        for (int i = ast.sym.parameters.size() - 1; i >= 0; i--) {
            final VariableSymbol param = ast.sym.parameters.get(i);
            assert param.offset == -1;
            param.offset = paramOffset;
            paramOffset += Config.SIZEOF_PTR;
        }
        THIS_OFFSET = paramOffset;
        paramOffset += Config.SIZEOF_PTR;

        // First few slots are reserved for caller save regs:
        int localSlot = RegisterManager.CALLER_SAVE.length * RegisterManager.SIZEOF_REG;

        // Assign local variable offsets:
        emit.emitComment(String.format("%-10s   Offset", "Variable"));
        for (VariableSymbol local : ast.sym.locals.values()) {
            assert local.offset == -1;
            local.offset = -localSlot;
            localSlot += Config.SIZEOF_PTR;
            emit.emitComment(String.format("%-10s   %d", local, local.offset));
        }

        // Round up stack size to make it a multiple of 16.
        // The actual amount passed to the enter instruction is 8
        // less, however, because it wants to know the amount
        // in addition to the linkage ptr and saved ebp.
        int implicit = Config.SIZEOF_PTR * 2;
        int stackSize = (implicit + localSlot + 15) & 0xFFFFFFF0;
        stackSize -= implicit;

        emit.emitComment(String.format("implicit=%d localSlot=%d sum=%d", implicit,
                localSlot, implicit + localSlot));


        // emit.emitRaw(String.format("enter $%d, $0", stackSize));
        emit.emit("push", BASE_REG);
        emit.emitMove(STACK_REG, BASE_REG);
        emit.emit("sub", constant(stackSize), STACK_REG);

        emit.emit("and", -16, STACK_REG);

        storeCalleeSaveRegs();

        // zero-initialize locals
        for (VariableSymbol local : ast.sym.locals.values()) {
            emit.emitMove(constant(0), registerOffset(local.offset, BASE_REG));
        }
    }

    @Override
    protected void emitMethodSuffix(boolean returnNull) {
        if (returnNull)
            emit.emit("movl", "$0", Register.EAX);
        restoreCalleeSaveRegs();
        emit.emitMove(BASE_REG, STACK_REG);
        emit.emit("pop", BASE_REG);
        emit.emitRaw("ret");
    }

    protected void emitNullCheck(Register toCheck, Expr exprToCheck, CurrentContext context) {
        int padding = emitCallPrefix(null, 1);
        push(toCheck.repr);
        emit.emit("call", AstCodeGeneratorRef.CHECK_NULL);
        emitCallSuffix(null, 1, padding);
    }

    protected void emitArrayBoundsCheck(Register reference, Register index, Ast.Index exprToCheck, CurrentContext context) {
        int padding = emitCallPrefix(null, 2);
        push(index.repr);
        push(reference.repr);
        emit.emit("call", AstCodeGeneratorRef.CHECK_ARRAY_BOUNDS);
        emitCallSuffix(null, 2, padding);
    }
}