package cd.backend.codegen;

import java.io.IOException;

import org.antlr.runtime.tree.TreeWizard.Visitor;

import cd.Config;
import cd.ToDoException;
import cd.backend.codegen.RegisterManager.Register;
import cd.ir.Ast;
import cd.ir.Ast.Assign;
import cd.ir.Ast.BuiltInWrite;
import cd.ir.Ast.BuiltInWriteln;
import cd.ir.Ast.IfElse;
import cd.ir.Ast.IntConst;
import cd.ir.Ast.MethodCall;
import cd.ir.Ast.MethodDecl;
import cd.ir.Ast.WhileLoop;
import cd.ir.Ast.VarDecl;
import cd.ir.Ast.Var;
import cd.ir.AstVisitor;
import cd.util.debug.AstOneLine;

import javax.print.DocFlavor;

import static cd.Config.*;
import static cd.backend.codegen.RegisterManager.BASE_REG;
import static cd.backend.codegen.RegisterManager.STACK_REG;

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
		{
			throw new RuntimeException("Not required");
		}
	}

	/**
	 * declare void main(){...}
	 *
	 * @param ast MethodDecl
	 * @param arg Void
	 * @return null
	 */
	@Override
	public Register methodDecl(MethodDecl ast, Void arg) {
		cg.emit.emitRaw(DATA_INT_SECTION);
		ast.decls().accept(this, arg);

		cg.emit.emitRaw(DATA_STR_SECTION);
		cg.emit.emitLabel("LC0");
		cg.emit.emit(DOT_STRING, "\"%d\"");
		cg.emit.emitLabel("LC1");
		cg.emit.emit(DOT_STRING, "\"\\n\"");

		cg.emit.emitRaw(TEXT_SECTION);
		cg.emit.emit(".globl", MAIN);
		cg.emit.emitLabel(MAIN);

		cg.emit.emit("pushl", BASE_REG);
		cg.emit.emitMove(STACK_REG, BASE_REG);
		cg.emit.emit("andl", -16, STACK_REG);
		cg.emit.emit("subl", 32, STACK_REG);

		// initialize the registers
		cg.rm.initRegisters();
		// call the body
		ast.body().accept(this, arg);
		// leave the main method
		cg.emit.emit("movl", 0, Register.EAX);
		cg.emit.emitRaw("leave");
		cg.emit.emitRaw("ret");

		return null;
	}

	@Override
	public Register ifElse(IfElse ast, Void arg) {
		{
			throw new RuntimeException("Not required");
		}
	}

	@Override
	public Register whileLoop(WhileLoop ast, Void arg) {
		{
			throw new RuntimeException("Not required");
		}
	}

	/**
	 * assign var=RHS
	 *
	 * @param ast Assign
	 * @param arg Void
	 * @return null
	 */
	@Override
	public Register assign(Assign ast, Void arg) {
		Var left = (Var) ast.left();

		Register right = cg.eg.visit(ast.right(), arg);
		cg.emit.emitMove(right, left.name);
		cg.rm.releaseRegister(right);

		return null;

	}

	/**
	 * call printf("%d", n) where n is an integer
	 *
	 * @param ast BuiltInWrite
	 * @param arg Void
	 * @return null
	 */
	@Override
	public Register builtInWrite(BuiltInWrite ast, Void arg) {
		Register reg = cg.eg.visit(ast.arg(), arg);
		cg.emit.emitStore(reg, 4, STACK_REG);
		cg.rm.releaseRegister(reg);

		String str = AssemblyEmitter.labelAddress("LC0");
		cg.emit.emitStore(str, 0, STACK_REG);

		cg.emit.emit("call", PRINTF);

		return null;
	}

	/**
	 * call printf("\n")
	 *
	 * @param ast BuiltInWriteLn
	 * @param arg Void
	 * @return null
	 */
	@Override
	public Register builtInWriteln(BuiltInWriteln ast, Void arg) {
		String str = AssemblyEmitter.labelAddress("LC1");
		cg.emit.emitStore(str, 0, STACK_REG);

		cg.emit.emit("call", PRINTF);

		return null;
	}

	/**
	 * declare int a; (and set a to 0)
	 *
	 * @param ast VarDecl
	 * @param arg Void
	 * @return null
	 */
	@Override
	public Register varDecl(VarDecl ast, Void arg) {
		cg.emit.emitLabel(ast.name);
		cg.emit.emitConstantData("0");

		return null;
	}
}
