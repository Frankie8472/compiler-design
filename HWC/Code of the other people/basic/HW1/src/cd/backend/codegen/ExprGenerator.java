package cd.backend.codegen;

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
import cd.ir.Ast.NewArray;
import cd.ir.Ast.NewObject;
import cd.ir.Ast.NullConst;
import cd.ir.Ast.ThisRef;
import cd.ir.Ast.UnaryOp;
import cd.ir.Ast.Var;
import cd.ir.ExprVisitor;
import cd.util.debug.AstOneLine;

import static cd.Config.SCANF;
import static cd.backend.codegen.RegisterManager.STACK_REG;

/**
 * Generates code to evaluate expressions. After emitting the code, returns a
 * String which indicates the register where the result can be found.
 */
class ExprGenerator extends ExprVisitor<Register, Void> {
	protected final AstCodeGenerator cg;

	ExprGenerator(AstCodeGenerator astCodeGenerator) {
		cg = astCodeGenerator;
	}

	public Register gen(Expr ast) {
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

	/**
	 * Return LHS = LHS Op RHS where Op = +,-,*,/ HACK SOLUTION (Other side placed
	 * on stack)
	 *
	 * @param ast
	 *            BinaryOp
	 * @param arg
	 *            Void
	 * @return Register
	 */
	@Override
	public Register binaryOp(BinaryOp ast, Void arg) {
		Register right = ast.right().accept(this, arg);
		cg.emit.emit("push", right);
		cg.rm.releaseRegister(right);

		Register left = ast.left().accept(this, arg);

		right = cg.rm.getRegister();
		cg.emit.emit("pop", right);

		switch (ast.operator.repr) {
			case ("+"):
				cg.emit.emit("addl", right, left);
				break;
			case ("-"):
				cg.emit.emit("subl", right, left);
				break;
			case ("*"):
				cg.emit.emit("imull", right, left);
				break;
			case ("/"):
				// Using the output of GCC on Cygwin 32-bit
				cg.emit.emitStore(left, 28, STACK_REG);
				cg.emit.emitStore(right, 24, STACK_REG);

				cg.emit.emitStore(Register.EAX, 16, STACK_REG);
				cg.emit.emitStore(Register.EDX, 12, STACK_REG);

				cg.emit.emitLoad(28, STACK_REG, Register.EAX);
				cg.emit.emitRaw("cltd");
				String str = AssemblyEmitter.registerOffset(24, STACK_REG);
				cg.emit.emit("idivl", str);
				cg.emit.emitStore(Register.EAX, 20, STACK_REG);

				cg.emit.emitLoad(16, STACK_REG, Register.EAX);
				cg.emit.emitLoad(12, STACK_REG, Register.EDX);

				cg.emit.emitLoad(20, STACK_REG, left);
				break;
		}

		cg.rm.releaseRegister(right);

		return left;
	}

	@Override
	public Register booleanConst(BooleanConst ast, Void arg) {
		{
			throw new RuntimeException("Not required");
		}
	}

	/**
	 * return i= read();
	 *
	 * @param ast
	 *            BuiltInRead
	 * @param arg
	 *            Void
	 * @return Register
	 */
	@Override
	public Register builtInRead(BuiltInRead ast, Void arg) {
		Register reg = cg.rm.getRegister();

		String offset = AssemblyEmitter.registerOffset(28, STACK_REG);
		cg.emit.emit("leal", offset, reg);
		cg.emit.emitStore(reg, 4, STACK_REG);

		// LC0 = String with the format "%d"
		String str = AssemblyEmitter.labelAddress("LC0");
		cg.emit.emitStore(str, 0, STACK_REG);

		cg.emit.emit("call", SCANF);
		// load from stdin to reg
		cg.emit.emitLoad(28, STACK_REG, reg);

		return reg;

	}

	@Override
	public Register cast(Cast ast, Void arg) {
		{
			throw new RuntimeException("Not required");
		}
	}

	@Override
	public Register index(Index ast, Void arg) {
		{
			throw new RuntimeException("Not required");
		}
	}

	/**
	 * Return a register holding an integer constant (ast.value)
	 *
	 * @param ast
	 *            IntConst
	 * @param arg
	 *            Void
	 * @return Register
	 */
	@Override
	public Register intConst(IntConst ast, Void arg) {
		Register reg = cg.rm.getRegister();
		String str = AssemblyEmitter.constant(ast.value);
		cg.emit.emitMove(str, reg);

		return reg;
	}

	@Override
	public Register field(Field ast, Void arg) {
		{
			throw new RuntimeException("Not required");
		}
	}

	@Override
	public Register newArray(NewArray ast, Void arg) {
		{
			throw new RuntimeException("Not required");
		}
	}

	@Override
	public Register newObject(NewObject ast, Void arg) {
		{
			throw new RuntimeException("Not required");
		}
	}

	@Override
	public Register nullConst(NullConst ast, Void arg) {
		{
			throw new RuntimeException("Not required");
		}
	}

	@Override
	public Register thisRef(ThisRef ast, Void arg) {
		{
			throw new RuntimeException("Not required");
		}
	}

	/**
	 * depending on the operator negate the value in reg or not
	 *
	 * @param ast
	 *            UnaryOp
	 * @param arg
	 *            Void
	 * @return Register
	 */
	@Override
	public Register unaryOp(UnaryOp ast, Void arg) {
		Register reg = ast.arg().accept(this, arg);

		switch (ast.operator.repr) {
		case ("+"):
			// Does nothing
			break;
		case ("-"):
			cg.emit.emit("neg", reg);
			break;
		}

		return reg;
	}

	/**
	 * move the value from ast.name into the register reg
	 *
	 * @param ast
	 *            Var
	 * @param arg
	 *            Void
	 * @return Register
	 */
	@Override
	public Register var(Var ast, Void arg) {
		Register reg = cg.rm.getRegister();
		cg.emit.emitMove(ast.name, reg);

		return reg;
	}

}
