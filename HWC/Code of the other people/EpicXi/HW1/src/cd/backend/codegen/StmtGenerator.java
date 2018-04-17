package cd.backend.codegen;

import cd.Config;
import cd.ToDoException;
import cd.backend.codegen.RegisterManager.Register;
import cd.ir.Ast;
import cd.ir.Ast.Assign;
import cd.ir.Ast.BuiltInWrite;
import cd.ir.Ast.BuiltInWriteln;
import cd.ir.Ast.IfElse;
import cd.ir.Ast.MethodCall;
import cd.ir.Ast.MethodDecl;
import cd.ir.Ast.WhileLoop;
import cd.ir.AstVisitor;
import cd.util.debug.AstOneLine;
import cd.ir.Ast.Var;

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

	@Override
	public Register methodDecl(MethodDecl ast, Void arg) {
		{
			// Because we only handle very simple programs in HW1,
			// you can just emit the prologue here!
			
			//Define strings for printf and scanf
			cg.emit.emitRaw(Config.DATA_INT_SECTION);
			cg.emit.emitLabel("STR_D");
			cg.emit.emitRaw(Config.DOT_STRING + " \"%d\"");
			cg.emit.emitLabel("STR_N");
			cg.emit.emitRaw(Config.DOT_STRING + " \"\\n\"");
			
			//Define local variables
			visit(ast.decls(), arg);
			
			//Emit text section prologue
			cg.emit.emitRaw(Config.TEXT_SECTION);
			cg.emit.emitRaw(".globl " + Config.MAIN);
			
			//Emit main
			cg.emit.emitLabel(Config.MAIN);
			
			//Push base register
			cg.emit.emit("pushl", RegisterManager.BASE_REG);
			cg.emit.emitMove(RegisterManager.STACK_REG, RegisterManager.BASE_REG);
			
			//Emit method body
			visit(ast.body(), arg);
			
			//Pop base register
			cg.emit.emitMove(RegisterManager.BASE_REG, RegisterManager.STACK_REG);
			cg.emit.emit("popl", RegisterManager.BASE_REG);
			cg.emit.emitRaw("ret");
			
			return null;
			//throw new ToDoException();
		}
	}
	
	@Override
	public Register varDecl(Ast.VarDecl ast, Void arg) {
		//Emit variable name
		cg.emit.emitLabel(ast.name);
		//Initialize with 0
		cg.emit.emitRaw(Config.DOT_INT + " 0");
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

	@Override
	public Register assign(Assign ast, Void arg) {
		{
			// Because we only handle very simple programs in HW1,
			// you can just emit the prologue here!
			
			//Visit right side of assignment
			Register r = cg.eg.visit(ast.right(), arg);
			
			//Variable which is assigned to
			Var targetVariable = (Var) ast.left();
			
			//Emit assignment statement
			cg.emit.emitMove(r, targetVariable.name);
			
			//Release the register. We don't need it anymore because it is stored in the local variable.
			cg.rm.releaseRegister(r);
			
			return null;
			//throw new ToDoException();
		}
	}

	@Override
	public Register builtInWrite(BuiltInWrite ast, Void arg) {
		{
			//Visit Expression inside the write statement
			Register r = cg.eg.visit(ast.arg(), arg);
			
			//Make place on the stack for the function call
			cg.emit.emit("sub", "$24", RegisterManager.STACK_REG);
			
			//Put arguments on stack
			cg.emit.emitStore("$STR_D", 0, RegisterManager.STACK_REG);
			cg.emit.emitStore(r, 4, RegisterManager.STACK_REG);
			
			//Call function
			cg.emit.emit("call", Config.PRINTF);
			
			//Restore stack pointer
			cg.emit.emit("add", "$24", RegisterManager.STACK_REG);
			
			//Release register
			cg.rm.releaseRegister(r);
			
			return null;
			//throw new ToDoException();
		}
	}

	@Override
	public Register builtInWriteln(BuiltInWriteln ast, Void arg) {
		{
			//Call to printf
			cg.emit.emit("sub", "$24", RegisterManager.STACK_REG);
			cg.emit.emitStore("$STR_N", 0, RegisterManager.STACK_REG);
			cg.emit.emit("call", Config.PRINTF);
			cg.emit.emit("add", "$24", RegisterManager.STACK_REG);
			
			return null;
			//throw new ToDoException();
		}
	}

}
