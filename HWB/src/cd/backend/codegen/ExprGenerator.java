package cd.backend.codegen;

import static cd.backend.codegen.AssemblyEmitter.constant;
import static cd.backend.codegen.AssemblyEmitter.labelAddress;
import static cd.backend.codegen.RegisterManager.BASE_REG;
import static cd.backend.codegen.RegisterManager.STACK_REG;
import static cd.ir.Ast.BinaryOp.BOp.B_MINUS;
import static cd.ir.Ast.BinaryOp.BOp.B_MOD;

import java.util.Arrays;
import java.util.List;

import cd.Config;
import cd.backend.codegen.RegisterManager.Register;
import cd.ir.Ast;
import cd.ir.Ast.BinaryOp;
import cd.ir.Ast.BinaryOp.BOp;
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
import cd.ir.Ast.UnaryOp.UOp;
import cd.ir.Ast.Var;
import cd.ir.ExprVisitor;
import cd.ir.Symbol;
import cd.ir.Symbol.ArrayTypeSymbol;
import cd.ir.Symbol.ClassSymbol;
import cd.ir.Symbol.PrimitiveTypeSymbol;
import cd.ir.Symbol.TypeSymbol;
import cd.util.Pair;
import cd.util.debug.AstOneLine;

/**
 * Generates code to evaluate expressions. After emitting the code, returns a
 * String which indicates the register where the result can be found.
 */
class ExprGenerator extends ExprVisitor<Register, CurrentContext> {
    protected final AstCodeGenerator cg;

    ExprGenerator(AstCodeGenerator astCodeGenerator) {
        cg = astCodeGenerator;
    }

    public Register gen(Expr ast, CurrentContext context) {
        return visit(ast, context);
    }

    @Override
    public Register visit(Expr ast, CurrentContext arg) {
        try {
            cg.emit.increaseIndent("Emitting " + AstOneLine.toString(ast));
            return super.visit(ast, arg);
        } finally {
            cg.emit.decreaseIndent();
        }

    }

    @Override
    public Register intConst(IntConst ast, CurrentContext arg) {
        {
            Register reg = cg.rm.getRegister();
            cg.emit.emit("movl", "$" + ast.value, reg);
            return reg;
        }
    }

    @Override
    public Register unaryOp(UnaryOp ast, CurrentContext arg) {
        {
            Register argReg = gen(ast.arg(), arg);
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

}

class ExprGeneratorOpt extends ExprGeneratorRef {

    ExprGeneratorOpt(AstCodeGeneratorRef astCodeGenerator) {
        super(astCodeGenerator);
    }

    @Override
    public Register var(Var ast, CurrentContext arg) {
        Register allReadyInRegister = cg.rm.getRegisterFromTag(ast.name);
        if (allReadyInRegister != null) {
//			cgRef.rm.setRegisterUsed(allReadyInRegister);
//			cg.emit.emitComment("REUSED REGISTER");
//			return allReadyInRegister;
        }
        return super.var(ast, arg);
    }

    @Override
    public Register binaryOp(BinaryOp ast, CurrentContext arg) {
        if (ast.operator != BOp.B_DIV && ast.operator != B_MOD && ast.operator != B_MINUS) {
            String value = null;
            Register exprResult = null;
            if (ast.left() instanceof IntConst) {
                value = AssemblyEmitter.constant(((IntConst) ast.left()).value);
                exprResult = gen(ast.right(), arg);
            } else if (ast.right() instanceof IntConst) {
                value = AssemblyEmitter.constant(((IntConst) ast.right()).value);
                exprResult = gen(ast.left(), arg);
            }
            if (exprResult != null) {
                BOp op = ast.operator;
                switch (op) {
                    case B_TIMES:
                        cgRef.emit.emit("imull", value, exprResult);
                        break;
                    case B_PLUS:
                        cgRef.emit.emit("addl", value, exprResult);
                        break;
                    case B_AND:
                        cgRef.emit.emit("andl", value, exprResult);
                        break;
                    case B_OR:
                        cgRef.emit.emit("orl", value, exprResult);
                        break;
                    case B_EQUAL:
                        emitCmp("sete", exprResult, value);
                        break;
                    case B_NOT_EQUAL:
                        emitCmp("setne", exprResult, value);
                        break;
                    case B_LESS_THAN:
                        emitCmp("setl", exprResult, value);
                        break;
                    case B_LESS_OR_EQUAL:
                        emitCmp("setle", exprResult, value);
                        break;
                    case B_GREATER_THAN:
                        emitCmp("setg", exprResult, value);
                        break;
                    case B_GREATER_OR_EQUAL:
                        emitCmp("setge", exprResult, value);
                        break;
                    default:
                        throw new AssemblyFailedException(
                                "Invalid binary operator for "
                                        + PrimitiveTypeSymbol.intType + " or "
                                        + PrimitiveTypeSymbol.booleanType);
                }

                return exprResult;

            }

        }

        Register result = super.binaryOp(ast, arg);
        cgRef.rm.removeRegisterTag(result);
        return result;
    }

    @Override
    public Register methodCall(MethodCallExpr ast, CurrentContext arg) {
        cgRef.rm.removeRegisterTag(Register.EAX);
        return super.methodCall(ast, arg);
    }

    @Override
    public Register builtInRead(BuiltInRead ast, CurrentContext arg) {
        cgRef.rm.flushTags();
        return super.builtInRead(ast, arg);
    }
}

/*
 * This is the subclass of ExprGenerator containing the reference solution
 */
class ExprGeneratorRef extends ExprGenerator {

    /* cg and cgRef are the same instance. cgRef simply
     * provides a wider interface */
    protected final AstCodeGeneratorRef cgRef;

    ExprGeneratorRef(AstCodeGeneratorRef astCodeGenerator) {
        super(astCodeGenerator);
        this.cgRef = astCodeGenerator;
    }

    /**
     * This routine handles register shortages. It generates a value for
     * {@code right}, while keeping the value in {@code leftReg} live. However,
     * if there are insufficient registers, it may temporarily store the value
     * in {@code leftReg} to the stack. In this case, it will be restored into
     * another register once {@code right} has been evaluated, but the register
     * may not be the same as {@code leftReg}. Therefore, this function returns
     * a pair of registers, the first of which stores the left value, and the
     * second of which stores the right value.
     */
    public Pair<Register> genPushing(Register leftReg, Expr right, CurrentContext context) {
        Register newLeftReg = leftReg;
        boolean pop = false;

        if (cgRef.rnv.calc(right) > cgRef.rm.availableRegisters()) {
            cgRef.push(newLeftReg.repr);
            cgRef.rm.releaseRegister(newLeftReg);
            pop = true;
        }

        Register rightReg = gen(right, context);

        if (pop) {
            newLeftReg = cgRef.rm.getRegister();
            cgRef.pop(newLeftReg.repr);
        }

        return new Pair<Register>(newLeftReg, rightReg);

    }

    @Override
    public Register binaryOp(BinaryOp ast, CurrentContext arg) {
        Register leftReg = null;
        Register rightReg = null;

        {

            leftReg = gen(ast.left(), arg);
            Pair<Register> regs = genPushing(leftReg, ast.right(), arg);
            leftReg = regs.a;
            rightReg = regs.b;

        }

        assert leftReg != null && rightReg != null;

        new OperandsDispatcher() {

            @Override
            public void booleanOp(Register leftReg, BOp op, Register rightReg) {
                integerOp(leftReg, op, rightReg);
            }

            @Override
            public void integerOp(Register leftReg, BOp op, Register rightReg) {

                switch (op) {
                    case B_TIMES:
                        cgRef.emit.emit("imull", rightReg, leftReg);
                        break;
                    case B_PLUS:
                        cgRef.emit.emit("addl", rightReg, leftReg);
                        break;
                    case B_MINUS:
                        cgRef.emit.emit("subl", rightReg, leftReg);
                        break;
                    case B_DIV:
                        emitDivMod(Register.EAX, leftReg, rightReg);
                        break;
                    case B_MOD:
                        emitDivMod(Register.EDX, leftReg, rightReg);
                        break;
                    case B_AND:
                        cgRef.emit.emit("andl", rightReg, leftReg);
                        break;
                    case B_OR:
                        cgRef.emit.emit("orl", rightReg, leftReg);
                        break;
                    case B_EQUAL:
                        emitCmp("sete", leftReg, rightReg.repr);
                        break;
                    case B_NOT_EQUAL:
                        emitCmp("setne", leftReg, rightReg.repr);
                        break;
                    case B_LESS_THAN:
                        emitCmp("setl", leftReg, rightReg.repr);
                        break;
                    case B_LESS_OR_EQUAL:
                        emitCmp("setle", leftReg, rightReg.repr);
                        break;
                    case B_GREATER_THAN:
                        emitCmp("setg", leftReg, rightReg.repr);
                        break;
                    case B_GREATER_OR_EQUAL:
                        emitCmp("setge", leftReg, rightReg.repr);
                        break;
                    default:
                        throw new AssemblyFailedException(
                                "Invalid binary operator for "
                                        + PrimitiveTypeSymbol.intType + " or "
                                        + PrimitiveTypeSymbol.booleanType);
                }

            }

        }.binaryOp(ast, leftReg, rightReg);

        cgRef.rm.releaseRegister(rightReg);

        return leftReg;
    }

    protected void emitCmp(String opname, Register leftReg, String rightReg) {

        cgRef.emit.emit("cmpl", rightReg, leftReg);

        if (leftReg.hasLowByteVersion()) {
            cgRef.emit.emit("movl", "$0", leftReg);
            cgRef.emit.emit(opname, leftReg.lowByteVersion().repr);
        } else {
            cgRef.push(Register.EAX.repr);
            cgRef.emit.emit("movl", "$0", Register.EAX);
            cgRef.emit.emit(opname, "%al");
            cgRef.emit.emit("movl", Register.EAX, leftReg);
            cgRef.pop(Register.EAX.repr);
        }

    }

    protected void emitDivMod(Register whichResultReg, Register leftReg,
                              Register rightReg) {

        // Compare right reg for 0
        int padding = cgRef.emitCallPrefix(null, 1);
        cgRef.push(rightReg.repr);
        cgRef.emit.emit("call", AstCodeGeneratorRef.CHECK_NON_ZERO);
        cgRef.emitCallSuffix(null, 1, padding);

        // Save EAX, EBX, and EDX to the stack if they are not used
        // in this subtree (but are used elsewhere). We will be
        // changing them.
        List<Register> dontBother = Arrays.asList(rightReg, leftReg);
        Register[] affected = {Register.EAX, Register.EBX, Register.EDX};
        for (Register s : affected)
            if (!dontBother.contains(s) && cgRef.rm.isInUse(s))
                cgRef.emit.emit("pushl", s);

        // Move the LHS (numerator) into eax
        // Move the RHS (denominator) into ebx
        cgRef.emit.emit("pushl", rightReg);
        cgRef.emit.emit("pushl", leftReg);
        cgRef.emit.emit("popl", Register.EAX);
        cgRef.emit.emit("popl", "%ebx");
        cgRef.emit.emitRaw("cltd"); // sign-extend %eax into %edx
        cgRef.emit.emit("idivl", "%ebx"); // division, result into edx:eax

        // Move the result into the LHS, and pop off anything we saved
        cgRef.emit.emit("movl", whichResultReg, leftReg);
        for (int i = affected.length - 1; i >= 0; i--) {
            Register s = affected[i];
            if (!dontBother.contains(s) && cgRef.rm.isInUse(s))
                cgRef.emit.emit("popl", s);
        }
    }

    @Override
    public Register booleanConst(BooleanConst ast, CurrentContext arg) {
        Register reg = cgRef.rm.getRegister();
        cgRef.emit.emit("movl", ast.value ? "$1" : "$0", reg);
        return reg;
    }

    @Override
    public Register builtInRead(BuiltInRead ast, CurrentContext arg) {
        Register reg = cgRef.rm.getRegister();
        int padding = cgRef.emitCallPrefix(reg, 0);
        cgRef.emit.emit("call", AstCodeGeneratorRef.READ_INTEGER);
        cgRef.emitCallSuffix(reg, 0, padding);
        return reg;
    }

    @Override
    public Register cast(Cast ast, CurrentContext arg) {
        // Invoke the helper function. If it does not exit,
        // the cast succeeded!
        Register objReg = gen(ast.arg(), arg);
        int padding = cgRef.emitCallPrefix(null, 2);
        cgRef.push(objReg.repr);
        cgRef.push(AssemblyEmitter.labelAddress(cgRef.vtable(ast.type)));
        cgRef.emit.emit("call", AstCodeGeneratorRef.CHECK_CAST);
        cgRef.emitCallSuffix(null, 2, padding);
        return objReg;
    }

    @Override
    public Register index(Index ast, CurrentContext arg) {
        Register arr = gen(ast.left(), arg);
        cgRef.emitNullCheck(arr, ast.left(), arg);
        Pair<Register> pair = genPushing(arr, ast.right(), arg);
        arr = pair.a;
        Register idx = pair.b;

        // Check array bounds
        cgRef.emitArrayBoundsCheck(arr, idx, ast, arg);

        cgRef.emit.emitMove(AssemblyEmitter.arrayAddress(arr, idx), idx);
        cgRef.rm.releaseRegister(arr);
        return idx;
    }

    @Override
    public Register field(Field ast, CurrentContext arg) {
        Register reg = gen(ast.arg(), arg);
        cgRef.emitNullCheck(reg, ast.arg(), arg);
        assert ast.sym.offset != -1;
        cgRef.emit.emitLoad(ast.sym.offset, reg, reg);
        return reg;
    }

    @Override
    public Register newArray(NewArray ast, CurrentContext arg) {
        // Size of the array = 4 + 4 + elemsize * num elem.
        // Compute that into reg, store it into the stack as
        // an argument to Javali$Alloc(), and then use it to store final
        // result.
        ArrayTypeSymbol arrsym = (ArrayTypeSymbol) ast.type;
        Register reg = gen(ast.arg(), arg);

        // Check for negative array sizes
        int padding = cgRef.emitCallPrefix(null, 1);
        cgRef.push(reg.repr);
        cgRef.emit.emit("call", AstCodeGeneratorRef.CHECK_ARRAY_SIZE);
        cgRef.emitCallSuffix(null, 1, padding);

        Register lenReg = cgRef.rm.getRegister();
        cgRef.emit.emit("movl", reg, lenReg); // save length

        cgRef.emit.emit("imul", Config.SIZEOF_PTR, reg);
        cgRef.emit.emit("addl", 2 * Config.SIZEOF_PTR, reg);

        int allocPadding = cgRef.emitCallPrefix(reg, 1);
        cgRef.push(reg.repr);
        cgRef.emit.emit("call", AstCodeGeneratorRef.ALLOC);
        cgRef.emitCallSuffix(reg, 1, allocPadding);

        // store vtable ptr and array length
        cgRef.emit.emitStore(AssemblyEmitter.labelAddress(cgRef.vtable(arrsym)), 0, reg);
        cgRef.emit.emitStore(lenReg, Config.SIZEOF_PTR, reg);
        cgRef.rm.releaseRegister(lenReg);

        return reg;
    }

    @Override
    public Register newObject(NewObject ast, CurrentContext arg) {
        ClassSymbol clssym = (ClassSymbol) ast.type;
        Register reg = cgRef.rm.getRegister();
        int allocPadding = cgRef.emitCallPrefix(reg, 1);
        cgRef.push(constant(clssym.sizeof));
        cgRef.emit.emit("call", AstCodeGeneratorRef.ALLOC);
        cgRef.emitCallSuffix(reg, 1, allocPadding);
        cgRef.emit.emitStore(labelAddress(cgRef.vtable(clssym)), 0, reg);
        return reg;
    }

    @Override
    public Register nullConst(NullConst ast, CurrentContext arg) {
        Register reg = cgRef.rm.getRegister();
        cgRef.emit.emit("movl", "$0", reg);
        return reg;
    }

    @Override
    public Register thisRef(ThisRef ast, CurrentContext arg) {
        Register reg = cgRef.rm.getRegister();
        cgRef.emit.emitLoad(cgRef.THIS_OFFSET, BASE_REG, reg);
        return reg;
    }

    @Override
    public Register methodCall(MethodCallExpr ast, CurrentContext arg) {
        List<Expr> allArgs = ast.allArguments();
        Symbol.MethodSymbol mthSymbol = ast.sym;

        int padding = cgRef.emitCallPrefix(null, allArgs.size());

        Register reg = null;
        for (int i = 0; i < allArgs.size(); i++) {
            if (reg != null) {
                cgRef.rm.releaseRegister(reg);
            }
            reg = cgRef.eg.gen(allArgs.get(i), arg);
            cgRef.push(reg.repr);
        }

        // Since "this" is the first parameter that push
        // we have to get it back to resolve the method call
        cgRef.emit.emitComment("Load \"this\" pointer");
        cgRef.emit.emitLoad((allArgs.size() - 1) * Config.SIZEOF_PTR, STACK_REG, reg);

        // Check for a null receiver
        cgRef.emitNullCheck(reg, ast.receiver(), arg);

        // Load the address of the method to call into "reg"
        // and call it indirectly.
        cgRef.emit.emitLoad(0, reg, reg);
        int mthdoffset = 4 + mthSymbol.vtableIndex * Config.SIZEOF_PTR;
        cgRef.emit.emitLoad(mthdoffset, reg, reg);
        cgRef.emit.emit("call", "*" + reg);

        cgRef.emitCallSuffix(reg, allArgs.size(), padding);

        if (mthSymbol.returnType == PrimitiveTypeSymbol.voidType) {
            cgRef.rm.releaseRegister(reg);
            return null;
        }
        return reg;
    }

    @Override
    public Register var(Var ast, CurrentContext arg) {
        Register reg = cgRef.rm.getRegister();
        switch (ast.sym.kind) {
            case LOCAL:
            case PARAM:
                assert ast.sym.offset != -1;
                cgRef.emit.emitLoad(ast.sym.offset, BASE_REG, reg);
                break;
            case FIELD:
                // These are removed by the ExprRewriter added to the
                // end of semantic analysis.
                throw new RuntimeException("Should not happen");
        }
        return reg;
    }

    @Override
    public Register unaryOp(UnaryOp ast, CurrentContext arg) {
        if (ast.operator == UOp.U_MINUS) {
            Register argReg = gen(ast.arg(), arg);
            cgRef.emit.emit("negl", argReg);
            return argReg;
        } else {
            return super.unaryOp(ast, arg);
        }
    }
}

/* Dispatches BinaryOp based on the types of the operands
 */
abstract class OperandsDispatcher {

    public abstract void integerOp(Register leftReg, BOp op,
                                   Register rightReg);

    public abstract void booleanOp(Register leftReg, BOp op,
                                   Register rightReg);

    public void binaryOp(BinaryOp ast, Register leftReg, Register rightReg) {

        assert ast.type != null;

        if (ast.type == PrimitiveTypeSymbol.intType) {
            integerOp(leftReg, ast.operator, rightReg);
        } else if (ast.type == PrimitiveTypeSymbol.booleanType) {

            final TypeSymbol opType = ast.left().type;

            if (opType == PrimitiveTypeSymbol.intType) {
                integerOp(leftReg, ast.operator, rightReg);
            } else if (opType == PrimitiveTypeSymbol.booleanType) {
                booleanOp(leftReg, ast.operator, rightReg);
            } else {
                integerOp(leftReg, ast.operator, rightReg);
            }

        }

    }

}
