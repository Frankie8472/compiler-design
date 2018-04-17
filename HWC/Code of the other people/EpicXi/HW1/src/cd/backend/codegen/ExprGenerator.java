package cd.backend.codegen;

import cd.Config;
import cd.ToDoException;
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
		{
			//Determine which operation is used
			String op;
			switch (ast.operator)
			{
			case B_PLUS: op = "addl"; break;
			case B_MINUS: op = "subl"; break;
			case B_TIMES: op = "imul"; break;
			case B_DIV: op = "idivl"; break;
			default: op = "op";
			}
			
			//Declare the registers for the left and right side of the operation
			Register r1;
			Register r2;
			
			//Calculate register count of left and right subtree
			int rightNeed = ast.right().registerCount();
			int leftNeed = ast.left().registerCount();
			
			
			//Use algorithm from slides to determine which subtree to evaluate first
			if (leftNeed > rightNeed)
			{
				r1 = visit(ast.left(), arg);
				r2 = visit(ast.right(), arg);
			}
			else
			{
				r2 = visit(ast.right(), arg);
				r1 = visit(ast.left(), arg);
			}
			
			//Swap registers in case of the minus operation
			if (ast.operator == BOp.B_MINUS)
			{
				Register temp = r2;
				r2 = r1;
				r1 = temp;
			}
			
			//Emit code for division
			if(ast.operator == BOp.B_DIV)
			{
				
				cg.emit.emitStore(Register.EAX, -8, Register.EBP);
				cg.emit.emitMove(r1, Register.EAX);
				cg.emit.emitRaw("cltd");
				cg.emit.emit("idivl", r2);
				cg.emit.emitMove(Register.EAX, r2);
				cg.emit.emitLoad(-8, Register.EBP, Register.EAX);
			}
			//Emit code for other operators
			else
			{
				cg.emit.emit(op, r1, r2);
			}
			
			//Release the left register as the result is stored in the right one
			cg.rm.releaseRegister(r1);
			
			//Return the register which holds the result
			return r2;
			//throw new ToDoException();
		}
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
			//Get new register to store result in
			Register newRegister = cg.rm.getRegister();
			Register tempRegister = cg.rm.getRegister();
			
			//Prepare stack for function call
			cg.emit.emit("sub", "$24", RegisterManager.STACK_REG);
			cg.emit.emit("leal", "8(" + RegisterManager.STACK_REG + "), " + tempRegister);
			cg.emit.emitStore("$STR_D", 0, RegisterManager.STACK_REG);
			cg.emit.emitStore(tempRegister, 4, RegisterManager.STACK_REG);
			
			cg.rm.releaseRegister(tempRegister);
			
			//Call scanf
			cg.emit.emit("call", Config.SCANF);
			
			//store result of scanf in newRegister
			cg.emit.emitLoad(8, RegisterManager.STACK_REG, newRegister);
			
			//restore stack
			cg.emit.emit("add", "$24", RegisterManager.STACK_REG);
			
			//Return result in register
			return newRegister;
			//throw new ToDoException();
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
			//New register to store the intConst in
			Register newRegister = cg.rm.getRegister();
			cg.emit.emitMove("$" + ast.value, newRegister);
			return newRegister;
			//throw new ToDoException();
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
		{
			//Get register of value without negation
			Register r = visitChildren(ast, arg);
			
			//Ignore plus unary Operator
			if (ast.operator == UnaryOp.UOp.U_MINUS)
			{
				//Negate value
				cg.emit.emit("neg", r);
			}
			
			//Return value in register r
			return r;
			//throw new ToDoException();
		}
	}
	
	@Override
	public Register var(Var ast, Void arg) {
		{
			//Get new register
			Register newRegister = cg.rm.getRegister();
			
			//Move variable using its name in the newRegister
			cg.emit.emitMove(ast.name, newRegister);
			
			//Return the new register
			return newRegister;
			//throw new ToDoException();
		}
	}

}
