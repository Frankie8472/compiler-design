package cd.backend.codegen;

import static cd.backend.codegen.AstCodeGenerator.METHOD_PREFIX;
import static cd.backend.codegen.RegisterManager.STACK_REG;

import cd.Config;
import cd.backend.codegen.RegisterManager.Register;
import cd.ir.Ast;
import cd.ir.Ast.Assign;
import cd.ir.Ast.BuiltInWrite;
import cd.ir.Ast.BuiltInWriteln;
import cd.ir.Ast.ClassDecl;
import cd.ir.Ast.IfElse;
import cd.ir.Ast.MethodCall;
import cd.ir.Ast.MethodDecl;
import cd.ir.Ast.ReturnStmt;
import cd.ir.Ast.WhileLoop;
import cd.ir.AstVisitor;
import cd.util.Tuple;
import cd.util.debug.AstOneLine;

/**
 * Generates code to process statements and declarations.
 */
class StmtGenerator extends AstVisitor<Register, Tuple<ClassContext, MethodContext>> {
    protected final AstCodeGenerator cg;
    protected final RegsNeededVisitor rnv;

    StmtGenerator(AstCodeGenerator astCodeGenerator) {
        cg = astCodeGenerator;
        rnv = new RegsNeededVisitor();
    }

    @Override
    public Register visit(Ast ast, Tuple<ClassContext, MethodContext> arg) {
        try {
            cg.emit.increaseIndent("Emitting " + AstOneLine.toString(ast));
            return super.visit(ast, arg);
        } finally {
            cg.emit.decreaseIndent();
        }
    }

    @Override
    public Register methodCall(MethodCall ast, Tuple<ClassContext, MethodContext> arg) {
        this.cg.rm.releaseRegister(this.cg.eg.visit(ast.getMethodCallExpr(), arg));
        return null;
    }

    // Emit vtable for arrays of this class:
    @Override
    public Register classDecl(ClassDecl ast, Tuple<ClassContext, MethodContext> arg) {
        // Set ClassContext and visit children
        return visitChildren(ast, new Tuple<>(new ClassContext(ast, this.cg.vtables.get(ast.sym.name), this.cg.memLayouts.get(ast.sym.name)), null));
    }

    @Override
    public Register methodDecl(MethodDecl ast, Tuple<ClassContext, MethodContext> arg) {
        // Set MethodContext
        arg.b = new MethodContext(arg.a, ast);

        // Emit method label
        cg.emit.emitLabel(METHOD_PREFIX + arg.a.symbol.name + "_" + ast.name);

        // Emit method prefix, visit children and emit suffix
        cg.emitMethodPrefix(arg.b.stackSize, false);
        this.visit(ast.body(), arg);
        cg.emitMethodSuffix(false, arg.b.returnLabel);

        return null;
    }

    @Override
    public Register ifElse(IfElse ast, Tuple<ClassContext, MethodContext> arg) {
        String labelElse = this.cg.emit.uniqueLabel();
        String labelDone = this.cg.emit.uniqueLabel();

        // Evaluate condition
        Register reg = this.cg.eg.visit(ast.condition(), arg);

        // Check condition
        this.cg.emit.emit("test", reg, reg);

        // Release condition register
        this.cg.rm.releaseRegister(reg);

        // Jump to else if false
        this.cg.emit.emit("jz", labelElse);

        // Evaluate then and jump to done
        this.visitChildren(ast.then(), arg);
        this.cg.emit.emit("jmp", labelDone);

        // Evaluate else
        this.cg.emit.emitLabel(labelElse);
        this.visitChildren(ast.otherwise(), arg);
        this.cg.emit.emitLabel(labelDone);
        // TODO: No else statement

        return null;
    }

    @Override
    public Register whileLoop(WhileLoop ast, Tuple<ClassContext, MethodContext> arg) {
        String labelStart = this.cg.emit.uniqueLabel();
        String labelCondition = this.cg.emit.uniqueLabel();

        // Jump to condition
        this.cg.emit.emit("jmp", labelCondition);

        // Emit body label and visit
        this.cg.emit.emitLabel(labelStart);
        this.visit(ast.body(), arg);

        // Emit condition label
        this.cg.emit.emitLabel(labelCondition);

        // Evaluate condition
        Register reg = this.cg.eg.visit(ast.condition(), arg);

        // Jump to start if true
        this.cg.emit.emit("test", reg, reg);
        this.cg.emit.emit("jnz", labelStart);


        this.cg.rm.releaseRegister(reg);

        return null;
    }

    @Override
    public Register assign(Assign ast, Tuple<ClassContext, MethodContext> arg) {

        // Evaluate RHS
        Register rightReg = this.cg.eg.visit(ast.right(), arg);

        // Determine type of assignment
        if (ast.left() instanceof Ast.Var) {
            Ast.Var var = (Ast.Var) ast.left();

            switch (var.sym.kind) {
                // If param or local, store on stack
                case PARAM:
                case LOCAL:
                    this.cg.emit.emitStore(rightReg, arg.b.getOffset(var.name), Register.EBP);
                    break;
                // If field, load this pointer and store with appropriate offset
                case FIELD:
                    Register leftReg = this.cg.rm.getRegister();
                    this.cg.emit.emitLoad(8, Register.EBP, leftReg);
                    this.cg.emit.emitStore(rightReg, arg.a.memLayout.getOffset(var.name), leftReg);
                    this.cg.rm.releaseRegister(leftReg);
                    break;
                default:
                    throw new RuntimeException("Invalid variable");
            }

        } else if (ast.left() instanceof Ast.Field) {
            Ast.Field field = (Ast.Field) ast.left();

            // Load LHS as pointer and store with appropriate offset
            Register leftReg = this.cg.eg.visit(field.arg(), arg);
            this.cg.checkNullPointer(leftReg);
            this.cg.emit.emitStore(rightReg, this.cg.memLayouts.get(field.arg().type.name).getOffset(field.fieldName), leftReg);

            this.cg.rm.releaseRegister(leftReg);
        } else if (ast.left() instanceof Ast.Index) {
            Ast.Index index = (Ast.Index) ast.left();

            // Determine the size of one element
            int size = this.cg.getSize(index.left().type);

            // Get array pointer and index
            Register leftReg = this.cg.eg.visit(index.left(), arg);
            Register offset = this.cg.eg.visit(index.right(), arg);

            // Check for nullptr
            this.cg.checkNullPointer(leftReg);

            // Account for element size
            this.cg.emit.emit("imul", AssemblyEmitter.constant(size), offset);

            // Check for invalid index
            this.cg.checkArrayBounds(leftReg, offset);

            this.cg.emit.emit("add", offset, leftReg);
            this.cg.emit.emitStore(rightReg, 0, leftReg);

            this.cg.rm.releaseRegister(leftReg);
            this.cg.rm.releaseRegister(offset);
        } else {
            throw new RuntimeException("Invalid assignment");
        }

        this.cg.rm.releaseRegister(rightReg);
        return null;
    }

    @Override
    public Register builtInWrite(BuiltInWrite ast, Tuple<ClassContext, MethodContext> arg) {
        Register reg = this.cg.eg.visit(ast.arg(), arg);
        int offset = this.cg.emitCallerSave(Config.SIZEOF_PTR * 2, reg);
        cg.emit.emitStore(reg, 4, STACK_REG);
        cg.emit.emitStore("$STR_D", 0, STACK_REG);
        cg.emit.emit("call", Config.PRINTF);
        this.cg.emitCallerLoad(offset, reg);
        cg.rm.releaseRegister(reg);
        return null;
    }

    @Override
    public Register builtInWriteln(BuiltInWriteln ast, Tuple<ClassContext, MethodContext> arg) {
        int offset = this.cg.emitCallerSave(Config.SIZEOF_PTR, null);
        cg.emit.emitStore("$STR_NL", 0, STACK_REG);
        cg.emit.emit("call", Config.PRINTF);
        this.cg.emitCallerLoad(offset, null);
        return null;
    }

    @Override
    public Register returnStmt(ReturnStmt ast, Tuple<ClassContext, MethodContext> arg) {
        if (ast.arg() != null)
            this.cg.emit.emitMove(this.cg.eg.visit(ast.arg(), arg), Register.EAX);
        if (arg.b.returnLabel == null) {
            arg.b.returnLabel = this.cg.emit.uniqueLabel();
        }

        this.cg.emit.emit("jmp", arg.b.returnLabel);
        return null;
    }

}
