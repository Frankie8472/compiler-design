package cd.backend.codegen;

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
import cd.ir.Ast.NewArray;
import cd.ir.Ast.NewObject;
import cd.ir.Ast.NullConst;
import cd.ir.Ast.ThisRef;
import cd.ir.Ast.UnaryOp;
import cd.ir.Ast.Var;
import cd.ir.ExprVisitor;
import cd.util.debug.AstOneLine;

import javax.management.RuntimeErrorException;

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
		//TODO: binaryOP
		//cg.emit.emitRaw("# BINARYOP");
		Register src = null;
		Register dest = null;
		cg.emit.emitRaw("#START");
		cg.emit.emitRaw("#"+ast.right().rwChildren.size());
		cg.emit.emitRaw("#SECOND");
		cg.emit.emitRaw("#"+ast.left().rwChildren.size());
		cg.emit.emitRaw("#END");

		if (ast.right().rwChildren.size() > ast.left().rwChildren.size()) {
			src = cg.eg.visit(ast.right(), arg);
			dest = cg.eg.visit(ast.left(), arg);
		} else {
			dest = cg.eg.visit(ast.left(), arg);
			src = cg.eg.visit(ast.right(), arg);
		}

		String op = "";
		switch(ast.operator) {
			case B_MINUS:
				op = "subl";
				cg.emit.emit(op, src, dest);
				cg.rm.releaseRegister(src);
				break;

			case B_PLUS:
				op = "addl";
				cg.emit.emit(op, src, dest);
				cg.rm.releaseRegister(src);
				break;

			case B_TIMES:
				op = "imull";
				cg.emit.emit(op, src, dest);
				cg.rm.releaseRegister(src);
				break;

			case B_DIV:
				Register eax_old = null, edx_old = null;
				Register eax = Register.EAX;
				Register edx = Register.EDX;

				// check if eax is in use and if its eax, if not
				if (cg.rm.isInUse(eax)) {
					if (!dest.equals(eax)) {
						eax_old = cg.rm.getRegister();
						cg.emit.emitMove(eax, eax_old);
						cg.emit.emitMove(dest, eax);
					} // else dest.equals(eax)
				} else {
					cg.emit.emitMove(dest, eax);
				}

				// check if edx is in use
				if (cg.rm.isInUse(edx)) {
					edx_old = cg.rm.getRegister();
					cg.emit.emitMove(edx, edx_old);
					if (src.equals(edx)) {
						src = edx_old;
					}
				}

				cg.emit.emitRaw("cltd");
				cg.emit.emit("idivl", src); //result/quotient in eax, reminder in edx

				// move result and restore old vars
				if(eax_old != null){
					cg.emit.emitMove(eax_old, eax);
					cg.rm.releaseRegister(eax_old);
				} else if (!dest.equals(eax)) {
					cg.emit.emitMove(eax, dest);
				}

				if(edx_old != null) {
					if (src.equals(edx_old)){
						cg.rm.releaseRegister(edx);
					} else {
						cg.emit.emitMove(edx_old, edx);
					}
					cg.rm.releaseRegister(edx_old);
				}
				break;

			case B_OR:					break;
			case B_AND:					break;
			case B_EQUAL:				break;
			case B_NOT_EQUAL:			break;
			case B_MOD:					break;
			case B_LESS_THAN:			break;
			case B_GREATER_THAN:		break;
			case B_LESS_OR_EQUAL:		break;
			case B_GREATER_OR_EQUAL:	break;
			default:					break;
		}

		return dest;
	}

	@Override
	public Register booleanConst(BooleanConst ast, Void arg) {
		{
			throw new RuntimeException("Not required");
		}
	}

	@Override
	public Register builtInRead(BuiltInRead ast, Void arg) {
		//TODO: read
		//cg.emit.emitRaw("# READ");

		Register dest = cg.rm.getRegister();
		cg.emit.emit("subl", 4, Register.ESP);
		cg.emit.emit("movl", Register.ESP, "(%esp)");
		cg.emit.emit("push", AssemblyEmitter.labelAddress("label_int"));
		cg.emit.emit("call", "scanf");
		cg.emit.emitMove("4(%esp)", dest);
		cg.emit.emit("addl", 8, Register.ESP);

		return dest;
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
		//TODO: intconst
		//cg.emit.emitRaw("# INTCONST");

		Register dest = cg.rm.getRegister();
		int src = ast.value;
		cg.emit.emit("movl", src, dest);
		return dest;
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
		// TODO: unaryOP
		//cg.emit.emitRaw("# UNARYOP");

		String op;
		Register dest = visit(ast.arg(), arg);
		switch(ast.operator) {
			case U_MINUS:		op = "negl"; cg.emit.emit(op, dest); break;
			case U_BOOL_NOT:	op = "notl"; cg.emit.emit(op, dest); break;
			case U_PLUS:		break;
			default:			break;
		}

		return dest;

	}
	
	@Override
	public Register var(Var ast, Void arg) {
		//TODO: var
		//cg.emit.emitRaw("# VAR");

		Register dest = cg.rm.getRegister();
		String src = "var_" + ast.name;
		cg.emit.emitMove(src, dest);

		return dest;

	}
}
