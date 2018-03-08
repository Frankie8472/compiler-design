package cd.backend.codegen;

import cd.Config;
import cd.ToDoException;
import cd.backend.codegen.RegisterManager.Register;
import cd.frontend.parser.ParseFailure;
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
import jdk.nashorn.internal.ir.debug.ASTWriter;

/**
 * Generates code to process statements and declarations.
 */
class StmtGenerator extends AstVisitor<Register, Void> {
	protected final AstCodeGenerator cg;
	private MethodDecl methodDecl;

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
			//TODO: prologue
			//cg.emit.emitRaw("# PROLOGUE");

			cg.rm.initRegisters();

			cg.emit.emitRaw(".globl " + Config.MAIN);

			// DATA_STR_SECTION
			cg.emit.emitRaw(Config.DATA_STR_SECTION);

			cg.emit.emitLabel("label_int");
			cg.emit.emitRaw(Config.DOT_STRING + " \"%d\"");

            cg.emit.emitLabel("label_new_line");
            cg.emit.emitRaw(Config.DOT_STRING + " \"\\n\"");

			// DATA_INT_SECTION
			cg.emit.emitRaw(Config.DATA_INT_SECTION);

			visitChildren(ast.decls(), arg);

			cg.emit.emitRaw(Config.TEXT_SECTION);
			cg.emit.emitLabel("main");

			cg.emit.emit("push", Register.EBP);
			cg.emit.emitMove(Register.ESP, Register.EBP);

			Register reg = cg.sg.visit(ast.body(), arg);

			cg.emit.emit("xorl", Register.EAX, Register.EAX);
            cg.emit.emitRaw("leave");
            cg.emit.emitRaw("ret");

            return reg;
		}
	}

	@Override
	public Register varDecl(Ast.VarDecl ast, Void arg) {
		cg.emit.emitLabel("var_" + ast.name);
		cg.emit.emitConstantData("0");
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
		//TODO: assign
		//cg.emit.emitRaw("# ASSIGN");

		Register src = cg.eg.visit(ast.right(), arg);
		String dest = "var_" + ((Ast.Var) ast.left()).name;
		cg.emit.emitMove(src, dest);
		cg.rm.releaseRegister(src);

		return null;
	}

	@Override
	public Register builtInWrite(BuiltInWrite ast, Void arg) {
	    //TODO: write
		//cg.emit.emitRaw("# WRITE");

		Register reg = cg.eg.visit(ast.arg(), arg);
        cg.emit.emit("push", reg);
        cg.emit.emit("push", AssemblyEmitter.labelAddress("label_int"));
        cg.emit.emit("call", Config.PRINTF);
        cg.rm.releaseRegister(reg);
		cg.emit.emit("addl", 8, Register.ESP);

		return null;
	}

	@Override
	public Register builtInWriteln(BuiltInWriteln ast, Void arg) {
        //TODO: writeln
		//cg.emit.emitRaw("# WRITELN");

		cg.emit.emit("push", AssemblyEmitter.labelAddress("label_new_line"));
        cg.emit.emit("call", Config.PRINTF);
		cg.emit.emit("addl", 4, Register.ESP);

		return null;
	}

}
