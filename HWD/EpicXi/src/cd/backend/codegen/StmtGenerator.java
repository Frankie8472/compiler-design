package cd.backend.codegen;

import static cd.Config.MAIN;
import static cd.backend.codegen.AssemblyEmitter.constant;
import static cd.backend.codegen.RegisterManager.STACK_REG;

import java.util.ArrayList;
import java.util.List;

import cd.Config;
import cd.ToDoException;
import cd.backend.codegen.RegisterManager.Register;
import cd.ir.Ast;
import cd.ir.Ast.Assign;
import cd.ir.Ast.BuiltInWrite;
import cd.ir.Ast.BuiltInWriteln;
import cd.ir.Ast.ClassDecl;
import cd.ir.Ast.Expr;
import cd.ir.Ast.IfElse;
import cd.ir.Ast.MethodCall;
import cd.ir.Ast.MethodDecl;
import cd.ir.Ast.ReturnStmt;
import cd.ir.Ast.Var;
import cd.ir.Ast.VarDecl;
import cd.ir.Ast.WhileLoop;
import cd.ir.AstVisitor;
import cd.ir.Symbol.MethodSymbol;
import cd.ir.Symbol.VariableSymbol;
import cd.util.debug.AstOneLine;

/**
 * Generates code to process statements and declarations.
 */
class StmtGenerator extends AstVisitor<Register, Void> {
	protected final AstCodeGenerator cg;

	StmtGenerator(AstCodeGenerator astCodeGenerator) {
		cg = astCodeGenerator;
	}

	public void gen(Ast ast) {
		visit(ast, null);
	}

	@Override
	public Register visit(Ast ast, Void arg) {
		try {
			cg.emit.increaseIndent("Emitting " + AstOneLine.toString(ast));
			return super.visit(ast, arg);
		} finally {
			cg.emit.decreaseIndent();
		}
	}

	@Override
	public Register methodCall(MethodCall ast, Void dummy) {
		Register register = cg.eg.gen(ast.getMethodCallExpr());
		cg.rm.releaseRegister(register);
		return null;
	}

	public Register methodCall(MethodSymbol sym, List<Expr> allArguments) {
		throw new RuntimeException("Not required");
	}

	// Emit vtable for arrays of this class:
	@Override
	public Register classDecl(ClassDecl ast, Void arg) {
		{
			return visitChildren(ast, arg);
		}
	}

	@Override
	public Register methodDecl(MethodDecl ast, Void arg) {
		{
			cg.emit.emitLabel(ast.sym.methodLabel);
			
			int localVariablesCount = ast.sym.locals.size();
			cg.emit.emit("enter", constant(Config.SIZEOF_PTR * localVariablesCount), "$0");
			cg.emit.emit("and", -16, STACK_REG);
			gen(ast.body());
			cg.emitMethodSuffix(true);
			return null;
		}
	}
	

	@Override
	public Register ifElse(IfElse ast, Void arg) {
		
		String elseLabel = cg.emit.uniqueLabel();
		String finalLabel = cg.emit.uniqueLabel();
		
		Register savedReg1 = cg.pushRegister(Register.EAX);
		
		Register cond = cg.eg.gen(ast.condition());
		cg.emit.emit("cmpl", "$0", cond);
		
		cg.rm.releaseRegister(cond);
		cg.popRegister(savedReg1);
		
		cg.emit.emit("je", elseLabel);
		visit(ast.then(), arg);
		cg.emit.emit("jmp", finalLabel);
		cg.emit.emitLabel(elseLabel);
		visit(ast.otherwise(), arg);
		cg.emit.emitLabel(finalLabel);
		
		return null;
	}

	@Override
	public Register whileLoop(WhileLoop ast, Void arg) {
		
		String startLabel = cg.emit.uniqueLabel();
		String finalLabel = cg.emit.uniqueLabel();
		
		cg.emit.emitLabel(startLabel);
		
		Register savedReg1 = cg.pushRegister(Register.EAX);
		
		Register cond = cg.eg.gen(ast.condition());
		
		cg.emit.emit("cmpl", "$0", cond);
		
		cg.rm.releaseRegister(cond);
		
		cg.popRegister(savedReg1);
		
		cg.emit.emit("je", finalLabel);
		
		gen(ast.body());
		
		cg.emit.emit("jmp", startLabel);
		cg.emit.emitLabel(finalLabel);
		
		return null;
	}
	

	@Override
	public Register assign(Assign ast, Void arg) {
		{			
			Register savedReg1 = cg.pushRegister(Register.EAX);
			Register savedReg2 = cg.pushRegister(Register.EDX);
			
			Register rhsReg = cg.eg.gen(ast.right());
			Register lhsReg = cg.aeg.gen(ast.left());
			cg.emit.emitMove(rhsReg, AssemblyEmitter.registerOffset(0, lhsReg));
			
			cg.rm.releaseRegister(lhsReg);
			cg.rm.releaseRegister(rhsReg);
			
			cg.popRegister(savedReg2);
			cg.popRegister(savedReg1);

			return null;
		}
	}

	@Override
	public Register builtInWrite(BuiltInWrite ast, Void arg) {
		{
			Register reg = cg.eg.gen(ast.arg());
			cg.emit.emit("sub", constant(16), STACK_REG);
			cg.emit.emitStore(reg, 4, STACK_REG);
			cg.emit.emitStore("$STR_D", 0, STACK_REG);
			cg.emit.emit("call", Config.PRINTF);
			cg.emit.emit("add", constant(16), STACK_REG);
			cg.rm.releaseRegister(reg);
			return null;
		}
	}

	@Override
	public Register builtInWriteln(BuiltInWriteln ast, Void arg) {
		{
			cg.emit.emit("sub", constant(16), STACK_REG);
			cg.emit.emitStore("$STR_NL", 0, STACK_REG);
			cg.emit.emit("call", Config.PRINTF);
			cg.emit.emit("add", constant(16), STACK_REG);
			return null;
		}
	}

	@Override
	public Register returnStmt(ReturnStmt ast, Void arg) {
		Register reg = cg.eg.gen(ast.arg());
		//cg.rm.getRegister(Register.EAX);
		cg.emit.emitMove(reg, Register.EAX);
		cg.rm.releaseRegister(reg);
		cg.emitMethodSuffix(false);
		return null;
	}

}
