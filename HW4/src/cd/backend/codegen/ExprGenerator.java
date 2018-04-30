package cd.backend.codegen;

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

    public Register gen(Expr ast) {
        return visit(ast, null);
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
    public Register binaryOp(BinaryOp ast, CurrentContext arg) {
        // Simplistic HW1 implementation that does
        // not care if it runs out of registers, and
        // supports only a limited range of operations:

        int leftRN = cg.rnv.calc(ast.left());
        int rightRN = cg.rnv.calc(ast.right());
        String if_label = Config.LOCALLABEL + cg.emit.uniqueLabel();
        String end_label = Config.LOCALLABEL + cg.emit.uniqueLabel();
        List<Register> dontBother;
        Register[] affected = {Register.EAX, Register.EBX, Register.EDX};

        Register leftReg, rightReg;
        if (leftRN > rightRN) {
            leftReg = visit(ast.left(), arg);
            cg.emit.emit("pushl", leftReg);
            cg.rm.releaseRegister(leftReg);

            rightReg = visit(ast.right(), arg);
            leftReg = cg.rm.getRegister();
            cg.emit.emit("popl", leftReg);
        } else {
            rightReg = visit(ast.right(), arg);
            cg.emit.emit("pushl", rightReg);
            cg.rm.releaseRegister(rightReg);

            leftReg = visit(ast.left(), arg);
            rightReg = cg.rm.getRegister();
            cg.emit.emit("popl", rightReg);
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
                // Save EAX, EBX, and EDX to the stack if they are not used
                // in this subtree (but are used elsewhere). We will be
                // changing them.
                dontBother = Arrays.asList(rightReg, leftReg);

                for (Register s : affected)
                    if (!dontBother.contains(s) && cg.rm.isInUse(s))
                        cg.emit.emit("pushl", s);

                cg.emit.emit("cmpl", AssemblyEmitter.constant(0), rightReg);
                cg.emit.emit("je", ExitCode.DIVISION_BY_ZERO.name());

                // Move the LHS (numerator) into eax
                // Move the RHS (denominator) into ebx
                cg.emit.emit("pushl", rightReg);
                cg.emit.emit("pushl", leftReg);
                cg.emit.emit("popl", Register.EAX);
                cg.emit.emit("popl", Register.EBX);
                cg.emit.emitRaw("cltd"); // sign-extend %eax into %edx
                cg.emit.emit("idivl", Register.EBX); // division, result into edx:eax

                // Move the result into the LHS, and pop off anything we saved
                cg.emit.emit("movl", Register.EAX, leftReg);
                for (int i = affected.length - 1; i >= 0; i--) {
                    Register s = affected[i];
                    if (!dontBother.contains(s) && cg.rm.isInUse(s))
                        cg.emit.emit("popl", s);
                }
                break;

            case B_MOD:
                // Save EAX, EBX, and EDX to the stack if they are not used
                // in this subtree (but are used elsewhere). We will be
                // changing them.
                dontBother = Arrays.asList(rightReg, leftReg);

                for (Register s : affected)
                    if (!dontBother.contains(s) && cg.rm.isInUse(s))
                        cg.emit.emit("pushl", s);

                cg.emit.emit("cmpl", AssemblyEmitter.constant(0), rightReg);
                cg.emit.emit("je", ExitCode.DIVISION_BY_ZERO.name());

                // Move the LHS (numerator) into eax
                // Move the RHS (denominator) into ebx
                cg.emit.emit("pushl", rightReg);
                cg.emit.emit("pushl", leftReg);
                cg.emit.emit("popl", Register.EAX);
                cg.emit.emit("popl", Register.EBX);
                cg.emit.emitRaw("cltd"); // sign-extend %eax into %edx
                cg.emit.emit("idivl", Register.EBX); // division, result into edx:eax

                // Move the result into the LHS, and pop off anything we saved
                cg.emit.emit("movl", Register.EDX, leftReg);
                for (int i = affected.length - 1; i >= 0; i--) {
                    Register s = affected[i];
                    if (!dontBother.contains(s) && cg.rm.isInUse(s))
                        cg.emit.emit("popl", s);
                }
                break;

            case B_OR:
                cg.emit.emit("orl", rightReg, leftReg);
                break;

            case B_AND:
                cg.emit.emit("andl", rightReg, leftReg);
                break;

            case B_EQUAL:
                cg.emit.emit("cmpl", rightReg, leftReg);
                cg.emit.emit("je", if_label);
                cg.emit.emitMove(AssemblyEmitter.constant(0), leftReg);
                cg.emit.emit("jmp", end_label);
                cg.emit.emitLabel(if_label);
                cg.emit.emitMove(AssemblyEmitter.constant(1), leftReg);
                cg.emit.emitLabel(end_label);
                break;

            case B_NOT_EQUAL:
                cg.emit.emit("cmpl", rightReg, leftReg);
                cg.emit.emit("jne", if_label);
                cg.emit.emitMove(AssemblyEmitter.constant(0), leftReg);
                cg.emit.emit("jmp", end_label);
                cg.emit.emitLabel(if_label);
                cg.emit.emitMove(AssemblyEmitter.constant(1), leftReg);
                cg.emit.emitLabel(end_label);
                break;

            case B_LESS_THAN:
                cg.emit.emit("cmpl", rightReg, leftReg);
                cg.emit.emit("jl", if_label);
                cg.emit.emitMove(AssemblyEmitter.constant(0), leftReg);
                cg.emit.emit("jmp", end_label);
                cg.emit.emitLabel(if_label);
                cg.emit.emitMove(AssemblyEmitter.constant(1), leftReg);
                cg.emit.emitLabel(end_label);
                break;

            case B_GREATER_THAN:
                cg.emit.emit("cmpl", rightReg, leftReg);
                cg.emit.emit("jg", if_label);
                cg.emit.emitMove(AssemblyEmitter.constant(0), leftReg);
                cg.emit.emit("jmp", end_label);
                cg.emit.emitLabel(if_label);
                cg.emit.emitMove(AssemblyEmitter.constant(1), leftReg);
                cg.emit.emitLabel(end_label);
                break;

            case B_LESS_OR_EQUAL:
                cg.emit.emit("cmpl", rightReg, leftReg);
                cg.emit.emit("jle", if_label);
                cg.emit.emitMove(AssemblyEmitter.constant(0), leftReg);
                cg.emit.emit("jmp", end_label);
                cg.emit.emitLabel(if_label);
                cg.emit.emitMove(AssemblyEmitter.constant(1), leftReg);
                cg.emit.emitLabel(end_label);
                break;

            case B_GREATER_OR_EQUAL:
                cg.emit.emit("cmpl", rightReg, leftReg);
                cg.emit.emit("jge", if_label);
                cg.emit.emitMove(AssemblyEmitter.constant(0), leftReg);
                cg.emit.emit("jmp", end_label);
                cg.emit.emitLabel(if_label);
                cg.emit.emitMove(AssemblyEmitter.constant(1), leftReg);
                cg.emit.emitLabel(end_label);
                break;

            default: {
                throw new RuntimeException("BinaryOperator does not exist. This should never happen!");
            }
        }

        cg.rm.releaseRegister(rightReg);

        return leftReg;
    }

    @Override
    public Register booleanConst(BooleanConst ast, CurrentContext arg) {
        Register reg = cg.rm.getRegister();
        if (ast.value) {
            cg.emit.emitMove(AssemblyEmitter.constant(1), reg);
        } else {
            cg.emit.emitMove(AssemblyEmitter.constant(0), reg);
        }
        return reg;
    }

    @Override
    public Register builtInRead(BuiltInRead ast, CurrentContext arg) {
        Register reg = cg.rm.getRegister();
        cg.emit.emit("sub", AssemblyEmitter.constant(16), STACK_REG);
        cg.emit.emit("leal", AssemblyEmitter.registerOffset(8, STACK_REG), reg);
        cg.emit.emitStore(reg, 4, STACK_REG);
        cg.emit.emitStore(AssemblyEmitter.labelAddress(AstCodeGenerator.DECIMAL_FORMAT_LABEL), 0, STACK_REG);
        cg.emit.emit("call", Config.SCANF);
        cg.emit.emitLoad(8, STACK_REG, reg);
        cg.emit.emit("add", AssemblyEmitter.constant(16), STACK_REG);
        return reg;
    }

    @Override
    public Register cast(Cast ast, CurrentContext arg) { // todo
        Register reg = visit(ast.arg(), arg);

        cg.emit.emit("pushl", reg);

        if (ast.type instanceof Symbol.ArrayTypeSymbol) {
            Symbol.ArrayTypeSymbol arrayTypeSymbol = (Symbol.ArrayTypeSymbol) ast.type;
            cg.emit.emit("pushl", AssemblyEmitter.labelAddress(LabelUtil.generateArrayLabelName(arrayTypeSymbol.elementType.name)));
        } else {
            cg.emit.emit("pushl", AssemblyEmitter.labelAddress(LabelUtil.generateMethodTableLabelName(ast.typeName)));
        }

        cg.emit.emit("call", "cast");
        cg.emit.emit("addl", AssemblyEmitter.constant(Config.SIZEOF_PTR * 2), RegisterManager.STACK_REG);

        return reg;
    }

    @Override
    public Register index(Index ast, CurrentContext arg) { // todo: jcheck
        Register index = visit(ast.right(), arg);
        cg.emit.emit("pushl", index);
        cg.rm.releaseRegister(index);

        Register array = visit(ast.left(), arg);
        cg.emit.emit("null_ptr_check", array);
        index = cg.rm.getRegister();
        cg.emit.emit("popl", index);

        cg.emit.emit("cmpl", AssemblyEmitter.constant(0), index);
        cg.emit.emit("jl", ExitCode.INVALID_ARRAY_BOUNDS.name());
        cg.emit.emit("cmpl", AssemblyEmitter.registerOffset(Config.SIZEOF_PTR, array), index);
        cg.emit.emit("jge", ExitCode.INVALID_ARRAY_BOUNDS.name());
        cg.emit.emitMove(AssemblyEmitter.arrayAddress(array, index), array);
        cg.rm.releaseRegister(index);
        return array;
    }

    @Override
    public Register intConst(IntConst ast, CurrentContext arg) {
        Register reg = cg.rm.getRegister();
        cg.emit.emitMove(AssemblyEmitter.constant(ast.value), reg);
        return reg;
    }

    @Override
    public Register field(Field ast, CurrentContext arg) {
        Register reg = visit(ast.arg(), arg);
        cg.emit.emit("null_ptr_check", reg);

        Integer offset = cg.vTables.get(ast.arg().type.name).getFieldOffset(ast.fieldName);
        cg.emit.emitMove(AssemblyEmitter.registerOffset(offset, reg), reg);
        return reg;
    }

    @Override
    public Register newArray(NewArray ast, CurrentContext arg) {
        /*
            Array Layout
                -------@ Memory 0xYYYYYYYY ------
                | METHOD_VTABLE_PTR             |
                | lenght                        |
                | Array contents                |
                ---------------------------------

            If the LSB of the METHOD_VTABLE_PTR is one we know it is an array. We align the METHOD_VTABLE to be on an
            address dividable by four so the LSB should always be 0 for a normal address.

            If it is an int array METHOD_VTABLE_PTR is 3 (... 0011)
            if it is a boolean array METHOD_VTABLE_PTR is 1 ( ... 0001)
         */
        Register array_size = visit(ast.arg(), arg);

        cg.emit.emit("cmpl", AssemblyEmitter.constant(0), array_size);
        cg.emit.emit("jl", "INVALID_ARRAY_SIZE");


        Register array = cg.rm.getRegister();

        cg.emit.emit("xchg", array, Register.EAX);
        cg.emit.emit("pushl", AssemblyEmitter.constant(Config.SIZEOF_PTR));

        cg.emit.emit("addl", 2, array_size);
        cg.emit.emit("pushl", array_size);

        cg.emit.emit("call", Config.CALLOC);
        cg.emit.emit("addl", AssemblyEmitter.constant(Config.SIZEOF_PTR*2), RegisterManager.STACK_REG);
        cg.emit.emit("xchg", Register.EAX, array);

        Symbol.TypeSymbol arrayType = ((Symbol.ArrayTypeSymbol) ast.type).elementType;

        String addressOrType = AssemblyEmitter.labelAddress(LabelUtil.generateArrayLabelName(arrayType.name));

        cg.emit.emit("movl", addressOrType, AssemblyEmitter.registerOffset(0, array));
        cg.emit.emit("subl", AssemblyEmitter.constant(2), array_size);
        cg.emit.emit("movl", array_size, AssemblyEmitter.registerOffset(4, array));
        cg.rm.releaseRegister(array_size);

        return array;
    }

    @Override
    public Register newObject(NewObject ast, CurrentContext arg) {
        /*
            Object Layout
                -------@ Memory 0xYYYYYYYY ------
                | METHOD_VTABLE_PTR             |
                | [Fields from Superclass(es)]  |
                | [Fields from this]            |
                ---------------------------------
         */
        Register objectPointer = cg.rm.getRegister();
        VTable table = cg.vTables.get(ast.typeName);
        cg.emit.emit("xchg", objectPointer, Register.EAX); // Backup EAX (even if not in use)
        cg.emit.emit("pushl", AssemblyEmitter.constant(Config.SIZEOF_PTR));
        cg.emit.emit("pushl", AssemblyEmitter.constant(table.getFieldCount()));
        cg.emit.emit("call", Config.CALLOC);
        cg.emit.emit("xchg", Register.EAX, objectPointer); // Restore EAX and put pointer in new register
        cg.emit.emit("addl", AssemblyEmitter.constant(Config.SIZEOF_PTR*2), RegisterManager.STACK_REG);
        cg.emit.emitMove(AssemblyEmitter.labelAddress(LabelUtil.generateMethodTableLabelName(ast.typeName)), AssemblyEmitter.registerOffset(0, objectPointer));
        return objectPointer;
    }

    @Override
    public Register nullConst(NullConst ast, CurrentContext arg) {
        Register ret = cg.rm.getRegister();
        cg.emit.emitMove(AssemblyEmitter.constant(0), ret);
        return ret;
    }

    @Override
    public Register thisRef(ThisRef ast, CurrentContext arg) {
        Register reg = cg.rm.getRegister();
        cg.emit.emitLoad(arg.getOffset("this"), RegisterManager.BASE_REG, reg);
        return reg;
    }

    @Override
    public Register methodCall(MethodCallExpr ast, CurrentContext arg) {
        Register reg;

        for (int i = ast.allArguments().size() - 1; i >= 0; i--) {
            reg = visit(ast.allArguments().get(i), arg);
            cg.emit.emit("pushl", reg);
            cg.rm.releaseRegister(reg);
        }

        reg = cg.rm.getRegister();

        Symbol.ClassSymbol currentClass = (Symbol.ClassSymbol) ast.receiver().type;
        Integer methodOffset = null;

        cg.emit.emitMove(AssemblyEmitter.registerOffset(0, RegisterManager.STACK_REG), reg);

        cg.emit.emit("null_ptr_check", reg);

        while (methodOffset == null) {
            cg.emit.emitLoad(0, reg, reg);
            VTable table = cg.vTables.get(currentClass.name);
            methodOffset = table.getMethodOffset(ast.methodName);
            currentClass = currentClass.superClass;
        }

        cg.emit.emit("call", "*" + AssemblyEmitter.registerOffset(methodOffset, reg));
        cg.emit.emit("addl", AssemblyEmitter.constant(ast.allArguments().size()*Config.SIZEOF_PTR), Register.ESP);
        cg.emit.emit("xchg", Register.EAX, reg);
        return reg;
    }

    @Override
    public Register unaryOp(UnaryOp ast, CurrentContext arg) {
        Register argReg = visit(ast.arg(), arg);
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

    @Override
    public Register var(Var ast, CurrentContext arg) {
        Register reg = cg.rm.getRegister();
        Integer offset;
        if (ast.sym.kind == Symbol.VariableSymbol.Kind.FIELD) {
            offset = cg.vTables.get(arg.getClassSymbol().name).getFieldOffset(ast.name);
            cg.emit.emitLoad(arg.getOffset("this"), Register.EBP, reg);

            cg.emit.emitLoad(offset, reg, reg);
        } else {
            offset = arg.getOffset(LabelUtil.generateLocalLabelName(ast.name, arg));
            cg.emit.emitLoad(offset, Register.EBP, reg);
        }
        return reg;
    }

}
