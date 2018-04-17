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

import java.util.ResourceBundle;

/**
 * Generates code to process statements and declarations.
 */
class StmtGenerator extends AstVisitor<Register, Void> {
	protected final AstCodeGenerator cg;

	StmtGenerator(AstCodeGenerator astCodeGenerator) {
		cg = astCodeGenerator;
		cg.rm.initRegisters();
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
			//throw new ToDoException();
			cg.emit.emitRaw(Config.DATA_STR_SECTION);
			cg.emit.emitLabel("labelScanf");
			cg.emit.emitRaw(Config.DOT_STRING + " \"%d\"");
			cg.emit.emitLabel("labelPrintf");
			cg.emit.emitRaw(Config.DOT_STRING + " \"%d\"");
			cg.emit.emitLabel("labelPrintfNL");
			cg.emit.emitRaw(Config.DOT_STRING + " \"\\n\"");
			cg.emit.emitRaw(Config.DATA_INT_SECTION);
			for(Ast d : ast.decls().children()) {
				cg.emit.emitLabel(((Ast.VarDecl)d).name);
				cg.emit.emitRaw(Config.DOT_INT + " 0");
			}
			cg.emit.emitRaw(Config.TEXT_SECTION);
			cg.emit.emitRaw(".globl " + Config.MAIN);
			cg.emit.emitLabel(Config.MAIN);

			cg.emit.emit("pushl", cg.rm.BASE_REG);
			cg.emit.emitMove(cg.rm.STACK_REG, cg.rm.BASE_REG);
			cg.emit.emit("andl", cg.emit.constant(-16), cg.rm.STACK_REG);
			//cg.emit.emit("subl", ast.decls().rwChildren().size() * 4, cg.rm.STACK_REG);

			//visit(ast.body(), arg);
			for (Ast a : ast.body().children()) {
				visit(a, arg);
			}

			cg.emit.emitMove(cg.rm.BASE_REG, cg.rm.STACK_REG);
			cg.emit.emit("popl", cg.rm.BASE_REG);
			cg.emit.emitRaw("xorl %eax, %eax");
			cg.emit.emitRaw("ret");

			Register reg = cg.rm.getRegister();
			return reg;
		}
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
			//throw new ToDoException();
			//Register dest = cg.eg.visit(ast.left(), arg);
			String dest = ((Ast.Var)ast.left()).name;
			Register src = cg.eg.visit(ast.right(), arg);
			cg.emit.emitMove(src, dest);
			cg.rm.releaseRegister(src);
			return null;
		}
	}

	@Override
	public Register builtInWrite(BuiltInWrite ast, Void arg) {
		{
			//throw new ToDoException();
			Register reg = cg.eg.visit(ast.arg(), arg);
			cg.emit.emit("subl", 16, cg.rm.STACK_REG);
			cg.emit.emitMove(reg, cg.emit.registerOffset(4, cg.rm.STACK_REG));
			cg.emit.emitMove( AssemblyEmitter.labelAddress("labelPrintf"), cg.emit.registerOffset(0,cg.rm.STACK_REG));
			cg.emit.emit("call", Config.PRINTF);
			cg.emit.emit("addl", 16, cg.rm.STACK_REG);
			cg.rm.releaseRegister(reg);
			return null;
		}
	}

	@Override
	public Register builtInWriteln(BuiltInWriteln ast, Void arg) {
		{
			//throw new ToDoException();
			//Register reg = visit(ast, arg);
			cg.emit.emit("subl", 16, cg.rm.STACK_REG);
			//cg.emit.emitMove(reg, cg.emit.registerOffset(4, cg.rm.STACK_REG));
			//cg.emit.emitMove("labelPrintfNL", "%eax");
			cg.emit.emitMove("$labelPrintfNL", cg.emit.registerOffset(0,cg.rm.STACK_REG));
			cg.emit.emitRaw("call " + Config.PRINTF);
			cg.emit.emit("addl", 16, cg.rm.STACK_REG);
			//cg.rm.releaseRegister(reg);
			return null;
		}
	}

}
