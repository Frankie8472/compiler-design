package cd.backend.codegen;

import static cd.Config.SCANF;
import static cd.backend.codegen.AssemblyEmitter.constant;
import static cd.backend.codegen.AstCodeGenerator.ERROR_PREFIX;
import static cd.backend.codegen.AstCodeGenerator.VTABLE_PREFIX;
import static cd.backend.codegen.RegisterManager.STACK_REG;

import java.util.Arrays;
import java.util.List;

import cd.Config;
import cd.ToDoException;
import cd.backend.ExitCode;
import cd.backend.codegen.RegisterManager.Register;
import cd.ir.Ast.BinaryOp;
import cd.ir.Ast.BooleanConst;
import cd.ir.Ast.BuiltInRead;
import cd.ir.Ast.Cast;
import cd.ir.Ast.Expr;
import cd.ir.Ast.Field;
import cd.ir.Ast.Index;
import cd.ir.Ast.IntConst;
import cd.ir.Ast.MethodCallExpr;
import cd.ir.Ast.NewArray;
import cd.ir.Ast.NewObject;
import cd.ir.Ast.NullConst;
import cd.ir.Ast.ThisRef;
import cd.ir.Ast.UnaryOp;
import cd.ir.Ast.Var;
import cd.ir.ExprVisitor;
import cd.ir.Symbol;
import cd.util.Tuple;
import cd.util.debug.AstOneLine;

/**
 * Generates code to evaluate expressions. After emitting the code, returns a
 * String which indicates the register where the result can be found.
 */
class ExprGenerator extends ExprVisitor<Register, Tuple<ClassContext, MethodContext>> {
    protected final AstCodeGenerator cg;

    ExprGenerator(AstCodeGenerator astCodeGenerator) {
        cg = astCodeGenerator;
    }

    @Override
    public Register visit(Expr ast, Tuple<ClassContext, MethodContext> arg) {
        try {
            cg.emit.increaseIndent("Emitting " + AstOneLine.toString(ast));
            return super.visit(ast, arg);
        } finally {
            cg.emit.decreaseIndent();
        }

    }

    @Override
    public Register binaryOp(BinaryOp ast, Tuple<ClassContext, MethodContext> arg) {
        int leftRN = cg.rnv.calc(ast.left());
        int rightRN = cg.rnv.calc(ast.right());

        boolean save = Math.max(leftRN, rightRN) > this.cg.rm.availableRegisters();

        Register leftReg, rightReg;
        if (leftRN > rightRN) {
            leftReg = this.visit(ast.left(), arg);
            if (save) {
                this.cg.emit.emit("push", leftReg);
                this.cg.rm.releaseRegister(leftReg);
            }
            rightReg = this.visit(ast.right(), arg);
            if (save) {
                leftReg = this.cg.rm.getRegister();
                this.cg.emit.emit("pop", leftReg);
            }
        } else {
            rightReg = this.visit(ast.right(), arg);
            if (save) {
                this.cg.emit.emit("push", rightReg);
                this.cg.rm.releaseRegister(rightReg);
            }
            leftReg = this.visit(ast.left(), arg);
            if (save) {
                rightReg = this.cg.rm.getRegister();
                this.cg.emit.emit("pop", rightReg);
            }
        }

        cg.debug("Binary Op: %s (%s,%s)", ast, leftReg, rightReg);

        switch (ast.operator) {
            case B_TIMES:
                cg.emit.emit("imul", rightReg, leftReg);
                break;
            case B_PLUS:
                cg.emit.emit("add", rightReg, leftReg);
                break;
            case B_MINUS:
                cg.emit.emit("sub", rightReg, leftReg);
                break;
            case B_DIV:
            case B_MOD:
                this.cg.checkDivisionByZero(rightReg);
                // Save EAX, EBX, and EDX to the stack if they are not used
                // in this subtree (but are used elsewhere). We will be
                // changing them.
                List<Register> dontBother = Arrays.asList(rightReg, leftReg);
                Register[] affected = {Register.EAX, Register.EBX, Register.EDX};

                for (Register s : affected)
                    if (!dontBother.contains(s) && cg.rm.isInUse(s))
                        cg.emit.emit("pushl", s);

                // Move the LHS (numerator) into eax
                // Move the RHS (denominator) into ebx
                cg.emit.emit("pushl", rightReg);
                cg.emit.emit("pushl", leftReg);
                cg.emit.emit("popl", Register.EAX);
                cg.emit.emit("popl", "%ebx");
                cg.emit.emitRaw("cltd"); // sign-extend %eax into %edx
                cg.emit.emit("idivl", "%ebx"); // division, result into edx:eax

                // Move the result into the LHS, and pop off anything we saved
                switch (ast.operator) {
                    case B_DIV:
                        cg.emit.emit("movl", Register.EAX, leftReg);
                        break;
                    case B_MOD:
                        cg.emit.emit("movl", Register.EDX, leftReg);
                        break;
                }

                for (int i = affected.length - 1; i >= 0; i--) {
                    Register s = affected[i];
                    if (!dontBother.contains(s) && cg.rm.isInUse(s))
                        cg.emit.emit("popl", s);
                }
                break;
            case B_AND:
                this.cg.emit.emit("and", rightReg, leftReg);
                break;
            case B_OR:
                this.cg.emit.emit("or", rightReg, leftReg);
                break;
            case B_EQUAL:
            case B_NOT_EQUAL:
            case B_LESS_THAN:
            case B_LESS_OR_EQUAL:
            case B_GREATER_THAN:
            case B_GREATER_OR_EQUAL:

                this.cg.emit.emit("push", Register.EAX);
                this.cg.emit.emit("xor", Register.EAX, Register.EAX);
                this.cg.emit.emit("cmpl", rightReg, leftReg);

                switch (ast.operator) {
                    case B_EQUAL:
                        this.cg.emit.emit("sete", RegisterManager.ByteRegister.EAX.repr);
                        break;
                    case B_NOT_EQUAL:
                        this.cg.emit.emit("setne", RegisterManager.ByteRegister.EAX.repr);
                        break;
                    case B_LESS_THAN:
                        this.cg.emit.emit("setl", RegisterManager.ByteRegister.EAX.repr);
                        break;
                    case B_LESS_OR_EQUAL:
                        this.cg.emit.emit("setle", RegisterManager.ByteRegister.EAX.repr);
                        break;
                    case B_GREATER_THAN:
                        this.cg.emit.emit("setg", RegisterManager.ByteRegister.EAX.repr);
                        break;
                    case B_GREATER_OR_EQUAL:
                        this.cg.emit.emit("setge", RegisterManager.ByteRegister.EAX.repr);
                        break;
                }

                this.cg.emit.emitMove(Register.EAX, leftReg);
                this.cg.emit.emit("pop", Register.EAX);
        }

        this.cg.rm.releaseRegister(rightReg);

        return leftReg;
    }

    @Override
    public Register booleanConst(BooleanConst ast, Tuple<ClassContext, MethodContext> arg) {
        Register reg = cg.rm.getRegister();
        cg.emit.emitMove(AssemblyEmitter.constant((ast.value) ? 1 : 0), reg);
        return reg;
    }

    @Override
    public Register builtInRead(BuiltInRead ast, Tuple<ClassContext, MethodContext> arg) {
        Register reg = cg.rm.getRegister();
        int offset = this.cg.emitCallerSave(Config.SIZEOF_PTR, reg);
        cg.emit.emit("sub", constant(16), STACK_REG);
        cg.emit.emit("leal", AssemblyEmitter.registerOffset(8, STACK_REG), reg);
        cg.emit.emitStore(reg, 4, STACK_REG);
        cg.emit.emitStore("$STR_D", 0, STACK_REG);
        cg.emit.emit("call", SCANF);
        cg.emit.emitLoad(8, STACK_REG, reg);
        cg.emit.emit("add", constant(16), STACK_REG);
        this.cg.emitCallerLoad(offset, reg);
        return reg;
    }

    @Override
    public Register cast(Cast ast, Tuple<ClassContext, MethodContext> arg) {
        Register reg = this.cg.eg.visit(ast.arg(), arg);


        boolean isClass = ast.type instanceof Symbol.ClassSymbol;
        boolean isArrayCast = ast.type == Symbol.ClassSymbol.objectType && ast.arg().type instanceof Symbol.ArrayTypeSymbol;
        if (isClass && !isArrayCast) {
            this.cg.checkDowncast((Symbol.ClassSymbol) ast.type, reg);
        }


        return reg;
    }

    @Override
    public Register index(Index ast, Tuple<ClassContext, MethodContext> arg) {
        Register reg = this.visit(ast.left(), arg);
        this.cg.checkNullPointer(reg);

        Register num = this.visit(ast.right(), arg);

        // Calculate offset
        this.cg.emit.emit("imul", AssemblyEmitter.constant(Config.SIZEOF_PTR), num);
        this.cg.checkArrayBounds(reg, num);
        this.cg.emit.emit("add", num, reg);

        // Get element
        this.cg.emit.emitLoad(0, reg, reg);

        this.cg.rm.releaseRegister(num);
        return reg;
    }

    @Override
    public Register intConst(IntConst ast, Tuple<ClassContext, MethodContext> arg) {
        Register reg = cg.rm.getRegister();
        cg.emit.emit("movl", "$" + ast.value, reg);
        return reg;
    }

    @Override
    public Register field(Field ast, Tuple<ClassContext, MethodContext> arg) {
        Register reg = this.visit(ast.arg(), arg);

        this.cg.checkNullPointer(reg);

        this.cg.emit.emitLoad(this.cg.memLayouts.get(ast.arg().type.name).getOffset(ast.fieldName), reg, reg);
        return reg;
    }

    @Override
    public Register newArray(NewArray ast, Tuple<ClassContext, MethodContext> arg) {
        Register reg = this.cg.rm.getRegister();
        Register num = this.visit(ast.arg(), arg);

        this.cg.checkArraySize(num);

        // Calculate array size, account for size
        this.cg.emit.emit("imul", AssemblyEmitter.constant(Config.SIZEOF_PTR), num);
        this.cg.emit.emit("addl", AssemblyEmitter.constant(Config.SIZEOF_PTR), num);

        // Allocate array
        this.cg.emit.emitStore(AssemblyEmitter.constant(1), Config.SIZEOF_PTR, Register.ESP);
        this.cg.emit.emitStore(num, 0, Register.ESP);
        this.cg.emit.emit("call", Config.CALLOC);

        // Save array size
        this.cg.emit.emit("addl", AssemblyEmitter.constant(-Config.SIZEOF_PTR), num);
        this.cg.emit.emitStore(num, 0, Register.EAX);

        // Shift array base pointer
        this.cg.emit.emit("add", AssemblyEmitter.constant(Config.SIZEOF_PTR), Register.EAX);
        this.cg.emit.emitMove(Register.EAX, reg);

        // Release unneeded register
        this.cg.rm.releaseRegister(num);

        return reg;
    }

    @Override
    public Register newObject(NewObject ast, Tuple<ClassContext, MethodContext> arg) {
        Register reg = this.cg.rm.getRegister();


        int size = this.cg.memLayouts.get(ast.type.name).size;
        String label = this.cg.vtables.get(ast.type.name).getLabel();

        int offset = this.cg.emitCallerSave(Config.SIZEOF_PTR, reg);

        this.cg.emit.emitStore(AssemblyEmitter.constant(size), Config.SIZEOF_PTR, Register.ESP);
        this.cg.emit.emitStore(AssemblyEmitter.constant(1), 0, Register.ESP);
        this.cg.emit.emit("call", Config.CALLOC);

        // Assign VTable pointer
        this.cg.emit.emitStore(AssemblyEmitter.labelAddress(label), 0, Register.EAX);

        // Move result into reg
        this.cg.emit.emitMove(Register.EAX, reg);

        // Restore registers
        this.cg.emitCallerLoad(offset, reg);
        return reg;
    }

    @Override
    public Register nullConst(NullConst ast, Tuple<ClassContext, MethodContext> arg) {
        Register reg = this.cg.rm.getRegister();
        this.cg.emit.emitMove(AssemblyEmitter.constant(0), reg);
        return reg;
    }

    @Override
    public Register thisRef(ThisRef ast, Tuple<ClassContext, MethodContext> arg) {
        Register reg = this.cg.rm.getRegister();
        this.cg.emit.emitLoad(8, Register.EBP, reg);
        return reg;
    }

    @Override
    public Register methodCall(MethodCallExpr ast, Tuple<ClassContext, MethodContext> arg) {
        Register reg = this.visit(ast.receiver(), arg);
        this.cg.checkNullPointer(reg);

        this.cg.emit.emitStore(reg, 0, Register.ESP);

        int offset = Config.SIZEOF_PTR;
        for (Expr expr : ast.argumentsWithoutReceiver()) {
            Register argReg = this.visit(expr, arg);
            this.cg.emit.emitStore(argReg, offset, Register.ESP);
            this.cg.rm.releaseRegister(argReg);

            offset += Config.SIZEOF_PTR;
        }

        offset = this.cg.emitCallerSave(offset, reg);

        int methodOffset = this.cg.vtables.get(ast.receiver().type.name).getOffset(ast.methodName);
        this.cg.emit.emitMove(AssemblyEmitter.registerOffset(0, reg), reg); // Dereference pointer
        this.cg.emit.emitMove(AssemblyEmitter.registerOffset(methodOffset, reg), reg); // Dereference main address

        this.cg.emit.emit("call", "*" + reg); // call method
        this.cg.emit.emitMove(Register.EAX, reg);

        this.cg.emitCallerLoad(offset, reg);

        return reg;
    }

    @Override
    public Register unaryOp(UnaryOp ast, Tuple<ClassContext, MethodContext> arg) {
        {
            Register argReg = this.visit(ast.arg(), arg);
            switch (ast.operator) {
                case U_PLUS:
                    break;

                case U_MINUS:
                    cg.emit.emit("negl", argReg);
                    break;

                case U_BOOL_NOT:
                    cg.emit.emit("negl", argReg);
                    cg.emit.emit("incl", argReg);
                    break;
            }
            return argReg;
        }
    }

    @Override
    public Register var(Var ast, Tuple<ClassContext, MethodContext> arg) {
        Register reg = cg.rm.getRegister();

        switch (ast.sym.kind) {
            case PARAM:
            case LOCAL:
                this.cg.emit.emitLoad(arg.b.getOffset(ast.name), Register.EBP, reg);
                break;
            case FIELD:
                this.cg.emit.emitLoad(8, Register.EBP, reg);
                this.cg.emit.emitLoad(arg.a.memLayout.getOffset(ast.name), reg, reg);
                break;
            default:
                throw new RuntimeException("Invalid variable");
        }
        return reg;
    }

}
