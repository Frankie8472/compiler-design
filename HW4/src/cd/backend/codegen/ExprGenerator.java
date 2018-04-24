package cd.backend.codegen;

import static cd.Config.SCANF;
import static cd.backend.codegen.AssemblyEmitter.constant;
import static cd.backend.codegen.AssemblyEmitter.arrayAddress;
import static cd.backend.codegen.AssemblyEmitter.labelAddress;
import static cd.backend.codegen.RegisterManager.STACK_REG;

import java.util.Arrays;
import java.util.List;

import cd.Config;
import cd.ToDoException;
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

    public Register gen(Expr ast) { //todo: what for?
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

        Register leftReg, rightReg;
        if (leftRN > rightRN) {
            leftReg = visit(ast.left(), arg);
            rightReg = visit(ast.right(), arg);
        } else {
            rightReg = visit(ast.right(), arg);
            leftReg = visit(ast.left(), arg);
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
            default: {
                throw new ToDoException(); // todo: throw correct error code
            }
        }

        cg.rm.releaseRegister(rightReg);

        return leftReg;
    }

    @Override
    public Register booleanConst(BooleanConst ast, CurrentContext arg) { //todo: jcheck
        Register reg = cg.rm.getRegister();
        if (ast.value) {
            cg.emit.emitMove(constant(1), reg);
        } else {
            cg.emit.emitMove(constant(0), reg);
        }
        return reg;
    }


    @Override
    public Register builtInRead(BuiltInRead ast, CurrentContext arg) {
        Register reg = cg.rm.getRegister();
        cg.emit.emit("sub", constant(16), STACK_REG);
        cg.emit.emit("leal", AssemblyEmitter.registerOffset(8, STACK_REG), reg);
        cg.emit.emitStore(reg, 4, STACK_REG);
        cg.emit.emitStore(AssemblyEmitter.labelAddress(AstCodeGenerator.DECIMAL_FORMAT_LABEL), 0, STACK_REG);
        cg.emit.emit("call", SCANF);
        cg.emit.emitLoad(8, STACK_REG, reg);
        cg.emit.emit("add", constant(16), STACK_REG);
        return reg;
    }

    @Override
    public Register cast(Cast ast, CurrentContext arg) { // todo

        throw new ToDoException();
    }

    @Override
    // Giving value back, not address of value, remember to transform for assignments!
    public Register index(Index ast, CurrentContext arg) { // todo: jcheck
        Register index = visit(ast.left(), arg);
        Register array = visit(ast.right(), arg);

        cg.emit.emitMove(arrayAddress(array, index), array);
        cg.rm.releaseRegister(index);
        return array;
    }

    @Override
    public Register intConst(IntConst ast, CurrentContext arg) {
        Register reg = cg.rm.getRegister();
        cg.emit.emitMove(constant(ast.value), reg);
        return reg;
    }

    @Override
    public Register field(Field ast, CurrentContext arg) { // todo
        Register reg = visit(ast.arg(), arg);
        Integer offset = cg.vTables.get(ast.arg().type.name).getFieldOffset(ast.fieldName);
        cg.emit.emitMove(AssemblyEmitter.registerOffset(offset, reg), reg);
        return reg;
    }

    @Override
    public Register newArray(NewArray ast, CurrentContext arg) { // todo
        // is the array size a given?
        Register array_size = visit(ast.arg(), arg);
        Register array = cg.rm.getRegister();
        cg.emit.emit("imull", Config.SIZEOF_PTR, array_size);
        cg.emit.emit("subl", array_size, Register.ESP); // does this work? sceptic!
        cg.emit.emitMove(Register.ESP, array);

        cg.rm.releaseRegister(array_size);
        return array;

        // can stack release be neglected?
    }

    @Override
    public Register newObject(NewObject ast, CurrentContext arg) {
        // TODO: Allocate Heap in sizeof(ast.type as class), set Pointer to vtable of the corresponding class
        Register objectPointer = cg.rm.getRegister();
        VTable table = cg.vTables.get(ast.typeName);
        cg.emit.emit("xchg", objectPointer, Register.EAX); // Backup EAX (even if not in use)
        cg.emit.emit("pushl", AssemblyEmitter.constant(Config.SIZEOF_PTR));
        cg.emit.emit("pushl", AssemblyEmitter.constant(table.getFieldCount()));
        cg.emit.emit("call", Config.CALLOC);
        cg.emit.emit("xchg", Register.EAX, objectPointer); // Restore EAX and put pointer in new register
        cg.emit.emit("addl", AssemblyEmitter.constant(8), RegisterManager.STACK_REG);
        cg.emit.emitMove(AssemblyEmitter.labelAddress(LabelUtil.generateMethodTableLabelName(ast.typeName)), AssemblyEmitter.registerOffset(0, objectPointer));
        return objectPointer;
    }

    @Override
    public Register nullConst(NullConst ast, CurrentContext arg) { // todo

        throw new ToDoException();
    }

    @Override
    public Register thisRef(ThisRef ast, CurrentContext arg) { // todo
        Register reg = cg.rm.getRegister();
        cg.emit.emitLoad(arg.getOffset("this"), RegisterManager.BASE_REG, reg);
        return reg;
    }

    @Override
    public Register methodCall(MethodCallExpr ast, CurrentContext arg) { // todo: jcheck
        // put parameter in inverse queue on stack
        Register reg = null; //TODO Hack?

        for (int i = ast.allArguments().size() - 1; i > 0; i--) {
            reg = visit(ast.allArguments().get(i), arg);
            cg.emit.emit("pushl", reg);
            cg.rm.releaseRegister(reg);
        }
        reg = visit(ast.allArguments().get(0), arg);
        cg.emit.emit("pushl", reg);

//        reg = visit(ast.receiver(), arg); //TODO: Not nice!

        // jump to methodlabel, inheritance not checked
        cg.emit.emitLoad(0, reg, reg);
        // It needs a '*' because it's an indirect call
        Integer methodOffset = cg.vTables.get(arg.getClassSymbol().name).getMethodOffset(ast.methodName);

        //TODO if methodOffset == null check super class

        cg.emit.emit("call", "*" + AssemblyEmitter.registerOffset(methodOffset,reg));
        cg.emit.emit("xchg", Register.EAX, reg);
        // return eax (return register? right)
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
