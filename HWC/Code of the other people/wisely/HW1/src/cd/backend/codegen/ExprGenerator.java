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
import cd.ir.PredictVisitor;
import cd.util.debug.AstOneLine;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	@Override
	public Register binaryOp(BinaryOp ast, Void arg) {

			//throw new ToDoException();
			Register reg_right;
			Register reg_left;

			// PredictVisitor use commented out, to try if the compiler otherwise passes the online tests

			PredictVisitor pv = new PredictVisitor();
			int pv_l = pv.visit(ast.left(), null);
			int pv_r = pv.visit(ast.right(), null);
			if (pv_l < pv_r) {
				reg_right = visit(ast.right(), arg);
				reg_left = visit(ast.left(), arg);
			} else {
				reg_left = visit(ast.left(), arg);
				reg_right = visit(ast.right(), arg);
			}

			//reg_right = visit(ast.right(), arg);
			//reg_left = visit(ast.left(), arg);

			switch (ast.operator) {
				case B_PLUS:
					cg.emit.emit("addl", reg_right, reg_left);
					break;
				case B_MINUS:
					cg.emit.emit("subl", reg_right, reg_left);
					break;
				case B_TIMES:
					cg.emit.emit("imull", reg_right, reg_left);
					break;
				case B_DIV: {
					if (cg.rm.isInUse(Register.EAX) && cg.rm.isInUse(Register.EDX)) {
						//store edx and eax in other register, so that we can use them.
						Register temp_eax = cg.rm.getRegister();
						cg.emit.emitMove(Register.EAX, temp_eax);
						Register temp_edx = cg.rm.getRegister();
						cg.emit.emitMove(Register.EDX, temp_edx);

						div(reg_left, reg_right);

						//move temp_eax and temp_edx back
						cg.emit.emitMove(temp_eax, Register.EAX);
						cg.emit.emitMove(temp_edx, Register.EDX);
						//release registers
						cg.rm.releaseRegister(temp_eax);
						cg.rm.releaseRegister(temp_edx);
					} else if(cg.rm.isInUse(Register.EAX)) {
						Register temp_eax = cg.rm.getRegister();
						if(cg.rm.isInUse(Register.EDX)){
							Register edx = temp_eax;
							temp_eax = cg.rm.getRegister();

							cg.rm.releaseRegister(edx);
						}
						cg.emit.emitMove(Register.EAX, temp_eax);

						div(reg_left, reg_right);

						cg.emit.emitMove(temp_eax, Register.EAX);
						cg.rm.releaseRegister(temp_eax);
					}else if (cg.rm.isInUse(Register.EDX)) {
						Register temp_edx = cg.rm.getRegister();
						if(cg.rm.isInUse(Register.EAX)){
							Register eax = temp_edx;
							temp_edx = cg.rm.getRegister();

							cg.rm.releaseRegister(eax);
						}
						cg.emit.emitMove(Register.EDX, temp_edx);

						div(reg_left, reg_right);

						cg.emit.emitMove(temp_edx, Register.EDX);
						cg.rm.releaseRegister(temp_edx);
					}else {
						div(reg_left, reg_right);
					}
					break;
				}
				default:
					throw new RuntimeException("not yet supported");

			}
			cg.rm.releaseRegister(reg_right);
			return reg_left;
	}

	private void div(Register reg_left, Register reg_right){
		cg.emit.emitMove(reg_left, Register.EAX);
		cg.emit.emitRaw("cltd");
		cg.emit.emit("idivl", reg_right);
		//result is in eax -> move it to reg_right
		cg.emit.emitMove(Register.EAX, reg_left);
	}

	@Override
	public Register booleanConst(BooleanConst ast, Void arg) {
		{
			throw new RuntimeException("Not required");
		}
	}

	@Override
	public Register builtInRead(BuiltInRead ast, Void arg) {
		{
			//throw new ToDoException();
			Register reg = cg.rm.getRegister();
			cg.emit.emit("subl", 16, cg.rm.STACK_REG);
			cg.emit.emit("leal", cg.emit.registerOffset(8, cg. rm.STACK_REG), reg);
			cg.emit.emitMove(reg, cg.emit.registerOffset(4, cg.rm.STACK_REG));
			cg.emit.emitMove(cg.emit.labelAddress("labelScanf"), cg.emit.registerOffset(0, cg.rm.STACK_REG));
			cg.emit.emit("call", Config.SCANF);
			//überlegen, ob 28 sinn macht...hmmm....
			//cg.emit.emitMove(cg.emit.registerOffset(28, cg.rm.STACK_REG), reg);

			cg.emit.emitMove(cg.emit.registerOffset(8, cg. rm.STACK_REG), reg);
			cg.emit.emit("addl", 16, cg.rm.STACK_REG);
			return reg;
		}
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

	@Override
	public Register intConst(IntConst ast, Void arg) {
		{
			//throw new ToDoException();
			Register reg = cg.rm.getRegister();
			cg.emit.emitMove(cg.emit.constant(ast.value), reg);
			return reg;
		}
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

	@Override
	public Register unaryOp(UnaryOp ast, Void arg) {
		//throw new ToDoException();
		Register reg = visit(ast.arg(), arg);
		String op = ast.operator.repr;
		if (op.equals("-")) {
            cg.emit.emit("negl", reg);
        } else if (op.equals("!")) {
            cg.emit.emit("subl", 1, reg);
            cg.emit.emit("sbb", reg, reg);
            cg.emit.emit("and", 1, reg);
        } else if (op.equals("+")){
			//do nothing
		}
		return reg;


	}
	
	@Override
	public Register var(Var ast, Void arg) {
		{
			//throw new ToDoException();
			Register reg = cg.rm.getRegister();
			cg.emit.emitMove(ast.name, reg);
			return reg;
		}
	}

}
