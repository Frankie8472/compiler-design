package cd.backend.codegen;

import static cd.backend.codegen.AssemblyEmitter.arrayAddress;
import static cd.backend.codegen.RegisterManager.BASE_REG;

import java.util.List;

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
import cd.ir.Ast.ReturnStmt;
import cd.ir.Ast.Var;
import cd.ir.Ast.WhileLoop;
import cd.ir.AstVisitor;
import cd.ir.ExprVisitor;
import cd.ir.Symbol.MethodSymbol;
import cd.util.Pair;
import cd.util.debug.AstOneLine;

/**
 * Generates code to process statements and declarations.
 */
class StmtGenerator extends AstVisitor<Register, CurrentContext> {
	protected final AstCodeGenerator cg;

	StmtGenerator(AstCodeGenerator astCodeGenerator) {
		cg = astCodeGenerator;
	}

	public void gen(Ast ast, CurrentContext context) {
		visit(ast, context);
	}

	@Override
	public Register visit(Ast ast, CurrentContext arg) {
		try {
			cg.emit.increaseIndent("Emitting " + AstOneLine.toString(ast));
			return super.visit(ast, arg);
		} finally {
			cg.emit.decreaseIndent();
		}
	}


	public Register methodCall(MethodSymbol sym, List<Expr> allArguments) {
		throw new RuntimeException("Not required");
	}

}

class StmtGeneratorOpt extends StmtGeneratorRef{

	StmtGeneratorOpt(AstCodeGeneratorRef astCodeGenerator) {
		super(astCodeGenerator);
	}

}

/*
 * StmtGenerator with the reference solution
 */
class StmtGeneratorRef extends StmtGenerator {
	
	/* cg and cgRef are the same instance. cgRef simply
	 * provides a wider interface */
	protected final AstCodeGeneratorRef cgRef;
	
	StmtGeneratorRef(AstCodeGeneratorRef astCodeGenerator) {
		super(astCodeGenerator);
		this.cgRef = astCodeGenerator;
	}

//	@Override
//	public Register methodCall(MethodSymbol mthSymbol, List<Expr> allArgs) {
//		// Push the arguments and the method prefix (caller save register,
//		// and padding) onto the stack.
//		// Note that the space for the arguments is not already reserved,
//		// so we just push them in the Java left-to-right order.
//		//
//		// After each iteration of the following loop, reg holds the
//		// register used for the previous argument.
//		int padding = cgRef.emitCallPrefix(null, allArgs.size());
//
//		Register reg = null;
//		for (int i = 0; i < allArgs.size(); i++) {
//			if (reg != null) {
//				cgRef.rm.releaseRegister(reg);
//			}
//			reg = cgRef.eg.gen(allArgs.get(i));
//			cgRef.push(reg.repr);
//		}
//
//		// Since "this" is the first parameter that push
//		// we have to get it back to resolve the method call
//		cgRef.emit.emitComment("Load \"this\" pointer");
//		cgRef.emit.emitLoad((allArgs.size() - 1) * Config.SIZEOF_PTR, STACK_REG, reg);
//
//		// Check for a null receiver
//		cgRef.emitNullCheck(reg);
//
//		// Load the address of the method to call into "reg"
//		// and call it indirectly.
//		cgRef.emit.emitLoad(0, reg, reg);
//		int mthdoffset = 4 + mthSymbol.vtableIndex * Config.SIZEOF_PTR;
//		cgRef.emit.emitLoad(mthdoffset, reg, reg);
//		cgRef.emit.emit("call", "*" + reg);
//
//		cgRef.emitCallSuffix(reg, allArgs.size(), padding);
//
//		if (mthSymbol.returnType == PrimitiveTypeSymbol.voidType) {
//			cgRef.rm.releaseRegister(reg);
//			return null;
//		}
//		return reg;
//	}

	@Override
	public Register methodCall(MethodCall ast, CurrentContext dummy) {
		Register reg = cgRef.eg.gen(ast.getMethodCallExpr(), dummy);
		if (reg != null)
			cgRef.rm.releaseRegister(reg);

		return reg;
	}
	
	@Override
	public Register classDecl(ClassDecl ast, CurrentContext arg) {
		// Emit each method:
		cgRef.emit.emitCommentSection("Class " + ast.name);
		CurrentContext context = new CurrentContext(ast);
		return visitChildren(ast, context);
	}

	@Override
	public Register methodDecl(MethodDecl ast, CurrentContext arg) {
		cgRef.emitMethodPrefix(ast);
		CurrentContext context = new CurrentContext(arg, ast);
		gen(ast.body(), context);
		cgRef.emitMethodSuffix(false);
		return null;
	}

	@Override
	public Register ifElse(IfElse ast, CurrentContext arg) {
		String falseLbl = cgRef.emit.uniqueLabel();
		String doneLbl = cgRef.emit.uniqueLabel();

		cgRef.genJumpIfFalse(ast.condition(), falseLbl, arg);
		gen(ast.then(), arg);
		cgRef.emit.emit("jmp", doneLbl);
		cgRef.emit.emitLabel(falseLbl);
		gen(ast.otherwise(), arg);
		cgRef.emit.emitLabel(doneLbl);

		return null;
	}

	@Override
	public Register whileLoop(WhileLoop ast, CurrentContext arg) {
		String nextLbl = cgRef.emit.uniqueLabel();
		String doneLbl = cgRef.emit.uniqueLabel();

		cgRef.emit.emitLabel(nextLbl);
		cgRef.genJumpIfFalse(ast.condition(), doneLbl, arg);
		gen(ast.body(), arg);
		cgRef.emit.emit("jmp", nextLbl);
		cgRef.emit.emitLabel(doneLbl);

		return null;
	}

	@Override
	public Register assign(Assign ast, CurrentContext arg) {
		class AssignVisitor extends ExprVisitor<Void, Expr> {

			@Override
			public Void var(Var ast, Expr right) {
				final Register rhsReg = cgRef.eg.gen(right, arg);
				cgRef.emit.emitStore(rhsReg, ast.sym.offset, BASE_REG);
				cgRef.rm.releaseRegister(rhsReg);
				return null;
			}

			@Override
			public Void field(Field ast, Expr right) {
				final Register rhsReg = cgRef.eg.gen(right, arg);
				Pair<Register> regs = cgRef.egRef.genPushing(rhsReg, ast.arg(), arg);
				cgRef.emitNullCheck(regs.b, ast.arg(), arg);
				
				cgRef.emit.emitStore(regs.a, ast.sym.offset, regs.b);
				cgRef.rm.releaseRegister(regs.b);
				cgRef.rm.releaseRegister(regs.a);
				
				return null;
			}

			@Override
			public Void index(Index ast, Expr right) {
				Register rhsReg = cgRef.egRef.gen(right, arg);
				
				Pair<Register> regs = cgRef.egRef.genPushing(rhsReg, ast.left(), arg);
				rhsReg = regs.a;
				Register arrReg = regs.b;
				cgRef.emitNullCheck(arrReg, ast.left(), arg);
				
				regs = cgRef.egRef.genPushing(arrReg, ast.right(), arg);
				arrReg = regs.a;
				Register idxReg = regs.b;
				
				// Check array bounds
				cgRef.emitArrayBoundsCheck(arrReg, idxReg, ast, arg);
				
				cgRef.emit.emitMove(rhsReg, arrayAddress(arrReg, idxReg));
				cgRef.rm.releaseRegister(arrReg);
				cgRef.rm.releaseRegister(idxReg);
				cgRef.rm.releaseRegister(rhsReg);

				return null;
			}

			@Override
			protected Void dfltExpr(Expr ast, Expr arg) {
				throw new RuntimeException("Store to unexpected lvalue " + ast);
			}

		}
		new AssignVisitor().visit(ast.left(), ast.right());

		return null;
	}

	@Override
	public Register builtInWrite(BuiltInWrite ast, CurrentContext arg) {
		Register reg = cgRef.eg.gen(ast.arg(), arg);
		int padding = cgRef.emitCallPrefix(null, 1);
		cgRef.push(reg.repr);
		cgRef.emit.emit("call", AstCodeGeneratorRef.PRINT_INTEGER);
		cgRef.emitCallSuffix(null, 1, padding);
		cgRef.rm.releaseRegister(reg);

		return null;
	}

	@Override
	public Register builtInWriteln(BuiltInWriteln ast, CurrentContext arg) {
		int padding = cgRef.emitCallPrefix(null, 0);
		cgRef.emit.emit("call", AstCodeGeneratorRef.PRINT_NEW_LINE);
		cgRef.emitCallSuffix(null, 0, padding);
		return null;
	}

	@Override
	public Register returnStmt(ReturnStmt ast, CurrentContext arg) {
		if (ast.arg() != null) {
			Register reg = cgRef.eg.gen(ast.arg(), arg);
			cgRef.emit.emitMove(reg, "%eax");
			cgRef.emitMethodSuffix(false);
			cgRef.rm.releaseRegister(reg);
		} else {
			cgRef.emitMethodSuffix(true); // no return value -- return NULL as
										// a default (required for main())
		}

		return null;
	}

}
