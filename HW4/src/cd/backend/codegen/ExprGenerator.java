package cd.backend.codegen;

import static cd.Config.SCANF;
import static cd.backend.codegen.AssemblyEmitter.constant;
import static cd.backend.codegen.AssemblyEmitter.arrayAddress;
import static cd.backend.codegen.AssemblyEmitter.labelAddress;
import static cd.backend.codegen.AssemblyEmitter.registerOffset;
import static cd.backend.codegen.RegisterManager.STACK_REG;

import java.util.Arrays;
import java.util.List;

import cd.Config;
import cd.ToDoException;
import cd.backend.codegen.RegisterManager.Register;
import cd.ir.Ast;
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
import cd.util.debug.AstOneLine;

/**
 * Generates code to evaluate expressions. After emitting the code, returns a
 * String which indicates the register where the result can be found.
 */
class ExprGenerator extends ExprVisitor<Register, Void> {
	protected final AstCodeGenerator cg;

	ExprGenerator(AstCodeGenerator astCodeGenerator) {
		cg = astCodeGenerator;
	}

	public Register gen(Expr ast) { //todo: what for?
		return visit(ast, null);
	}

	@Override
	public Register visit(Expr ast, Void arg) {
		try {
			cg.emit.increaseIndent("Emitting " + AstOneLine.toString(ast));
			return super.visit(ast, null);
		} finally {
			cg.emit.decreaseIndent();
		}

	}

	@Override
	public Register binaryOp(BinaryOp ast, Void arg) {
		{
			// Simplistic HW1 implementation that does
			// not care if it runs out of registers, and
			// supports only a limited range of operations:

			int leftRN = cg.rnv.calc(ast.left());
			int rightRN = cg.rnv.calc(ast.right());

			Register leftReg, rightReg;
			if (leftRN > rightRN) {
				leftReg = gen(ast.left());
				rightReg = gen(ast.right());
			} else {
				rightReg = gen(ast.right());
				leftReg = gen(ast.left());
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
				Register[] affected = { Register.EAX, Register.EBX, Register.EDX };

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
				cg.emit.emit("movl", Register.EAX, leftReg);
				for (int i = affected.length - 1; i >= 0; i--) {
					Register s = affected[i];
					if (!dontBother.contains(s) && cg.rm.isInUse(s))
						cg.emit.emit("popl", s);
				}
				break;
			default:
				{
					throw new ToDoException(); // todo: throw correct error code
				}
			}

			cg.rm.releaseRegister(rightReg);

			return leftReg;
		}
	}

	@Override
	public Register booleanConst(BooleanConst ast, Void arg) { //todo: jcheck
		Register reg = cg.rm.getRegister();
		if(ast.value){
			cg.emit.emitMove(constant(1), reg);
		} else{
			cg.emit.emitMove(constant(0), reg);
		}
		return reg;
	}


	@Override
	public Register builtInRead(BuiltInRead ast, Void arg) {
		{
			Register reg = cg.rm.getRegister();
			cg.emit.emit("sub", constant(16), STACK_REG);
			cg.emit.emit("leal", AssemblyEmitter.registerOffset(8, STACK_REG), reg);
			cg.emit.emitStore(reg, 4, STACK_REG);
			cg.emit.emitStore("$STR_D", 0, STACK_REG);
			cg.emit.emit("call", SCANF);
			cg.emit.emitLoad(8, STACK_REG, reg);
			cg.emit.emit("add", constant(16), STACK_REG);
			return reg;
		}
	}

	@Override
	public Register cast(Cast ast, Void arg) { // todo

		throw new ToDoException();
	}

	@Override
	// Giving value back, not address of value, remember to transform for assignments!
	public Register index(Index ast, Void arg) { // todo: jcheck
		Register index = visit(ast.left(), arg);
		Register array = visit(ast.right(), arg);

		cg.emit.emitMove(arrayAddress(array, index), array);
		cg.rm.releaseRegister(index);
		return array;
	}

	@Override
	public Register intConst(IntConst ast, Void arg) {
		{
			Register reg = cg.rm.getRegister();
			cg.emit.emitMove(constant(ast.value), reg);
			return reg;
		}
	}

	@Override
	public Register field(Field ast, Void arg) { // todo

		throw new ToDoException();
	}

	@Override
	public Register newArray(NewArray ast, Void arg) { // todo
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
	public Register newObject(NewObject ast, Void arg) { // todo

		throw new ToDoException();
	}

	@Override
	public Register nullConst(NullConst ast, Void arg) { // todo

		throw new ToDoException();
	}

	@Override
	public Register thisRef(ThisRef ast, Void arg) { // todo

		throw new ToDoException();
	}

	@Override
	public Register methodCall(MethodCallExpr ast, Void arg) { // todo: jcheck
		// put parameter in inverse queue on stack
		Register reg;
		for (int i = ast.allArguments().size()-1; i > 0; i--){
			reg = visit(ast.allArguments().get(i), arg);
			cg.emit.emit("pushl", reg);
			cg.rm.releaseRegister(reg);
		}
		// jump to methodlabel, inheritance not checked
		cg.emit.emit("call", labelAddress(ast.methodName));
		// return eax (return register? right)
		return Register.EAX;
	}

	@Override
	public Register unaryOp(UnaryOp ast, Void arg) {
			Register argReg = gen(ast.arg());
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
	public Register var(Var ast, Void arg) {
			Register reg = cg.rm.getRegister();
			cg.emit.emit("movl", AstCodeGenerator.VAR_PREFIX + ast.name, reg);
			return reg;
	}

}
