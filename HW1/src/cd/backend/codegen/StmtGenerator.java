package cd.backend.codegen;

import cd.Config;
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
import cd.util.debug.AstDump;
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
		{
			throw new RuntimeException("Not required");
		}
	}

    @Override
    public Register methodDecl(MethodDecl ast, Void arg) {
        cg.rm.initRegisters();

        cg.emit.emitRaw(".globl " + Config.MAIN);

        // DATA_STR_SECTION
        cg.emit.emitRaw(Config.DATA_STR_SECTION);
        cg.emit.emitLabel("label_print");
        cg.emit.emitRaw(Config.DOT_STRING + " \"%d\"");
        cg.emit.emitLabel("label_new_line");
        cg.emit.emitRaw(Config.DOT_STRING + " \"\\n\"");

        // DATA_INT_SECTION
        cg.emit.emitRaw(Config.DATA_INT_SECTION);
        visitChildren(ast.decls(), arg);

        // TEXT_SECTION
        cg.emit.emitRaw(Config.TEXT_SECTION);
        cg.emit.emitLabel(Config.MAIN);

        // ENTER
        cg.emit.emit("push", Register.EBP);
        cg.emit.emitMove(Register.ESP, Register.EBP);

        // FUNCTION
        Register reg = visitChildren(ast.body(), arg);

        // FREE ALL REGS
        cg.rm.releaseRegister(Register.EAX);
        cg.rm.releaseRegister(Register.EDX);
        cg.rm.releaseRegister(Register.EBX);
        cg.rm.releaseRegister(Register.ECX);
        cg.rm.releaseRegister(Register.EDI);
        cg.rm.releaseRegister(Register.ESI);

        // EAX = 0, LEAVE, RET
        cg.emit.emit("xorl", Register.EAX, Register.EAX);
        cg.emit.emitRaw("leave");
        cg.emit.emitRaw("ret");

        return reg;
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
    public Register assign(Assign ast, Void arg) {
        Register src = cg.eg.visit(ast.right(), arg);
        String dest = "var_" + ((Ast.Var) ast.left()).name; //Not happy with that
        cg.emit.emitMove(src, dest);
        cg.rm.releaseRegister(src);
        return null;
    }

    /*    @Override
        public Register assign(Assign ast, Void arg) {

            Register right = cg.eg.visit(ast.right(), arg);
            Register left = cg.eg.visit(ast.left(), arg);

            cg.emit.emitMove(right, left);

            cg.rm.releaseRegister(right);
            return left;
        }*/
    @Override
    public Register whileLoop(WhileLoop ast, Void arg) {
        {
            throw new RuntimeException("Not required");
        }
    }

    @Override
    public Register builtInWrite(BuiltInWrite ast, Void arg) {
        Register result = cg.eg.visit(ast.arg(), arg);

        cg.emit.emit("push", result);
        cg.emit.emit("push", AssemblyEmitter.labelAddress("label_print"));
        cg.emit.emit("call", Config.PRINTF);
        cg.emit.emit("pop", result);
        cg.emit.emit("pop", result);

        cg.rm.releaseRegister(result);
        return null;
    }

    @Override
    public Register builtInWriteln(BuiltInWriteln ast, Void arg) {
        cg.emit.emit("push", AssemblyEmitter.labelAddress("label_new_line"));
        cg.emit.emit("call", Config.PRINTF);
        cg.emit.emit("addl", 4, Register.ESP);
        return null;
    }
}
