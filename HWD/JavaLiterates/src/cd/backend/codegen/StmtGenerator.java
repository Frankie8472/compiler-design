package cd.backend.codegen;

import static cd.backend.codegen.AssemblyEmitter.constant;
import static cd.backend.codegen.RegisterManager.STACK_REG;

import java.util.List;

import cd.Config;
import cd.backend.codegen.RegisterManager.Register;
import cd.ir.Ast;
import cd.ir.Ast.Assign;
import cd.ir.Ast.BuiltInWrite;
import cd.ir.Ast.BuiltInWriteln;
import cd.ir.Ast.ClassDecl;
import cd.ir.Ast.Expr;
import cd.ir.Ast.Field;
import cd.ir.Ast.IfElse;
import cd.ir.Ast.Index;
import cd.ir.Ast.MethodCall;
import cd.ir.Ast.MethodDecl;
import cd.ir.Ast.Nop;
import cd.ir.Ast.NullConst;
import cd.ir.Ast.ReturnStmt;
import cd.ir.Ast.Var;
import cd.ir.Ast.WhileLoop;
import cd.ir.AstVisitor;
import cd.ir.Symbol.ClassSymbol;
import cd.ir.Symbol.MethodSymbol;
import cd.ir.Symbol.VariableSymbol;
import cd.util.Pair;
import cd.util.debug.AstOneLine;

/**
 * Generates code to process statements and declarations.
 */
class StmtGenerator extends AstVisitor<Register, Pair<String>> {
	protected final AstCodeGenerator cg;

	StmtGenerator(AstCodeGenerator astCodeGenerator) {
		cg = astCodeGenerator;
	}

	public void gen(Ast ast, Pair<String> arg) {
		visit(ast, arg);
	}

	@Override
	public Register visit(Ast ast, Pair<String> arg) {
		try {
			cg.emit.increaseIndent("Emitting " + AstOneLine.toString(ast));
			return super.visit(ast, arg);
		} finally {
			cg.emit.decreaseIndent();
		}
	}

	@Override
	public Register methodCall(MethodCall ast, Pair<String> arg) {
		return cg.eg.gen(ast.getMethodCallExpr(), arg);
	}

	public Register methodCall(MethodSymbol sym, List<Expr> allArguments) {
		throw new RuntimeException("Not required");
	}

	// Emit vtable for arrays of this class:
	@Override
	public Register classDecl(ClassDecl ast, Pair<String> arg) {
		arg = new Pair<String>(ast.name, null);
		return visitChildren(ast, arg);
	}

	@Override
	public Register methodDecl(MethodDecl ast, Pair<String> arg) {
		{
			// Generate generic method
			String name = arg.a + "_" + ast.name;
			cg.emit.raw(".globl " + name);
			cg.emit.label(name);
			int maxOffset = 4;
			for (@SuppressWarnings("unused")
			VariableSymbol var : ast.sym.locals.values()) {
				maxOffset += 4;
			}
			cg.emit.raw("enter " + AssemblyEmitter.constant(maxOffset) + ", $0");

			// Initialize all locals to 0;
			for (int i = -maxOffset; i < 0; i += 4) {
				String loc = AssemblyEmitter.deref(i, Register.EBP);
				cg.emit.mov("$0", loc);
			}
			arg.b = ast.name;
			// TODO Properly save and release used registers before method call, instead of
			// just releasing all
			cg.rm.initRegisters();
			visitChildren(ast, arg);
			cg.emitMethodSuffix(true);

			return null;
		}
	}

	@Override
	public Register varDecl(Ast.VarDecl ast, Pair<String> arg) {

		return null;
	}

	@Override
	public Register ifElse(IfElse ast, Pair<String> arg) {
		Register reg = cg.eg.gen(ast.condition(), arg);
		String labelIf = cg.emit.uniqueLabel() + "_If";
		String labelElse = cg.emit.uniqueLabel() + "_Else";
		String labelEnd = cg.emit.uniqueLabel() + "_IfElseEND";
		// Check for else block, and switch appropriately
		if (ast.children().get(2) instanceof Nop) {
			cg.emit.cmp(0, reg);
			cg.rm.releaseRegister(reg);
			cg.emit.jge(labelEnd);
			// Jmp label for if condition
			cg.emit.label(labelIf);
			gen(ast.then(), arg);
			// Jmp label for end of stmt
			cg.emit.label(labelEnd);
		} else {
			cg.emit.cmp(0, reg);
			cg.rm.releaseRegister(reg);
			cg.emit.jge(labelElse);
			// Jmp label for if condition
			cg.emit.label(labelIf);
			gen(ast.then(), arg);
			cg.emit.jmp(labelEnd);
			// Jmp label for if not condition
			cg.emit.label(labelElse);
			gen(ast.otherwise(), arg);
			// Jmp label for end of stmt
			cg.emit.label(labelEnd);
		}
		return null;
	}

	@Override
	public Register whileLoop(WhileLoop ast, Pair<String> arg) {
		Register reg;
		String labelWhile = cg.emit.uniqueLabel() + "_WhileStart";
		String labelCond = cg.emit.uniqueLabel() + "_WhileCond";

		cg.emit.jmp(labelCond);
		// Jmp label for start of while loop
		cg.emit.label(labelWhile);
		// loop body
		gen(ast.body(), arg);
		// checking condition
		cg.emit.label(labelCond);
		reg = cg.eg.gen(ast.condition(), arg);
		cg.emit.cmp(0, reg);
		cg.emit.jl(labelWhile);
		// if done with loop, release reg and return
		cg.rm.releaseRegister(reg);

		return null;
	}

	@Override
	public Register assign(Assign ast, Pair<String> arg) {

		Register rhsReg = cg.eg.gen(ast.right(), arg);

		if (ast.left() instanceof Var) {
			Var var = (Var) ast.left();
			switch (var.sym.kind) {
			case PARAM:
			case LOCAL:
				int varOffset = ((ClassSymbol) cg.typeSymbols.get(arg.a)).methods.get(arg.b).locAndParaOffsets
						.get(var.name);
				cg.emit.store(rhsReg, varOffset, Register.EBP);
				break;
			case FIELD:
				int fieldOffset = cg.ot.ot.get(arg.a).fieldOffsets.get(var.name);
				Register reg = cg.rm.getRegister();
				cg.emit.load(8, Register.EBP, reg);
				cg.emit.store(rhsReg, fieldOffset, reg);
				cg.rm.releaseRegister(reg);
				break;
			}
		} else if (ast.left() instanceof Index) {
			Register lhs = cg.eg.gen(((Index) ast.left()).left(), arg);
			Register rhs = cg.eg.gen(((Index) ast.left()).right(), arg);
			int size = cg.ot.ot.get(ast.left().type.name).objSize();
			String address = AssemblyEmitter.arrayAddress(lhs, rhs, size);
			cg.emit.mov(rhsReg, address);
			cg.rm.releaseRegister(lhs);
			cg.rm.releaseRegister(rhs);
		} else if (ast.left() instanceof Field) {
			Expr rcvr = ((Field) ast.left()).arg();
			Register rcvrReg = cg.eg.gen(rcvr, arg);
			int fieldOffset = cg.ot.ot.get(rcvr.type.name).fieldOffsets.get(((Field) ast.left()).fieldName);
			cg.emit.store(rhsReg, fieldOffset, rcvrReg);
			cg.rm.releaseRegister(rcvrReg);
		}
		cg.rm.initRegisters();
		return null;

	}

	@Override
	public Register builtInWrite(BuiltInWrite ast, Pair<String> arg) {
		Register reg = cg.eg.gen(ast.arg(), arg);
		cg.emit.emit("sub", constant(16), STACK_REG);
		cg.emit.store(reg, 4, STACK_REG);
		cg.emit.store("$STR_D", 0, STACK_REG);
		cg.emit.emit("call", Config.PRINTF);
		cg.emit.emit("add", constant(16), STACK_REG);
		cg.rm.releaseRegister(reg);
		return null;

	}

	@Override
	public Register builtInWriteln(BuiltInWriteln ast, Pair<String> arg) {
		cg.emit.emit("sub", constant(16), STACK_REG);
		cg.emit.store("$STR_NL", 0, STACK_REG);
		cg.emit.emit("call", Config.PRINTF);
		cg.emit.emit("add", constant(16), STACK_REG);
		return null;

	}

	@Override
	public Register returnStmt(ReturnStmt ast, Pair<String> arg) {
		if (null != ast.arg()) {
			Register reg = cg.eg.gen(ast.arg(), arg);
			cg.emit.mov(reg, RegisterManager.Register.EAX);
			cg.rm.releaseRegister(reg);
		}
		cg.emitMethodSuffix(ast.arg() instanceof NullConst);
		return null;
	}

}
