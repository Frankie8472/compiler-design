package cd.backend.codegen;

import static cd.backend.codegen.AssemblyEmitter.constant;
import static cd.backend.codegen.AssemblyEmitter.labelAddress;
import static cd.backend.codegen.RegisterManager.STACK_REG;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import cd.util.debug.AstOneLine;

/**
 * Generates code to process statements and declarations.
 */
class StmtGenerator extends AstVisitor<Register, CurrentContext> {
	protected final AstCodeGenerator cg;

	private Boolean first_class = true;

	private static Map<String, Map<String, Integer>> utable = new HashMap<>();

	private Integer currentVTableOffset = 0;

	StmtGenerator(AstCodeGenerator astCodeGenerator) {
		cg = astCodeGenerator;
	}

	public void gen(Ast ast) {
		visit(ast, null);
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

	@Override
	public Register methodCall(MethodCall ast, CurrentContext dummy) { // todo
		// wenn wird das ufgrüäfe? happens in exprgenerator oder?
        // Wenns nur e method call ohni assignemnt isch. muess eifach de ExprGenerator calle.
		return cg.eg.visit(ast.getMethodCallExpr(), dummy);
	}

	// @frankie: Was isch das?
	public Register methodCall(MethodSymbol sym, List<Expr> allArguments) {
		throw new RuntimeException("Not required");
	}

	// Emit vtable for arrays of this class:
	@Override
	public Register classDecl(ClassDecl ast, CurrentContext arg) {
		CurrentContext current = new CurrentContext(ast.sym);
		currentVTableOffset = 4;
		Map<String, Integer> vtable = new HashMap<>();
		// Registers are initialized in AstCodeGenerator-constructor with method initMethodData();
		// Create offset table for vtables
		for (VarDecl varDecl : ast.fields()){
			vtable.put(varDecl.name, currentVTableOffset);
			currentVTableOffset += 4;
		}

		for (MethodDecl methodDecl : ast.methods()){
			vtable.put(methodDecl.name, currentVTableOffset);
			currentVTableOffset += 4;
		}

		utable.put(ast.name, vtable);

		// add vptr here but only for baseclass, yet confused...
		visitChildren(ast, current);


		return null;
	}

	@Override
	public Register methodDecl(MethodDecl ast, CurrentContext arg) {
		CurrentContext current = new CurrentContext(arg, ast.sym);
		String name = current.getClassSymbol().name + "_" + current.getMethodSymbol().name;

		cg.emit.emitRaw(Config.TEXT_SECTION);
		cg.emit.emitLabel(name);

        cg.emit.emit("push", Register.EBP);
        cg.emit.emitMove(Register.ESP, Register.EBP);
        // Align stack to be on an address dividable by 16. Important for Macs.
		cg.emit.emit("and", -16, STACK_REG);

		for (String arg_names : ast.argumentNames){
			current.addParameter(name + "_" + arg_names);
		}

		visit(ast.decls(), current);
		visit(ast.body(), current);

		cg.emitMethodSuffix(true); // leave expression
		return null;

	}

	@Override
	public Register ifElse(IfElse ast, CurrentContext arg) { // todo

		throw new ToDoException();
	}

	@Override
	public Register whileLoop(WhileLoop ast, CurrentContext arg) { // todo

		throw new ToDoException();
	}

	@Override
	public Register assign(Assign ast, CurrentContext arg) {
		/*
			if (!(ast.left() instanceof Var))
				throw new RuntimeException("LHS must be var in HW1");
			*/
		Register lhsReg = visit(ast.left(), arg);
		Register rhsReg = visit(ast.right(), arg);
		if (ast.left() instanceof Ast.Index){
			cg.emit.emitStore(rhsReg, 0, lhsReg);
		} else if (ast.left() instanceof Ast.Var) {
			Var var = (Var) ast.left();
			cg.emit.emit("movl", rhsReg, AstCodeGenerator.VAR_PREFIX + var.name);
			cg.rm.releaseRegister(rhsReg);
		} else if (ast.left() instanceof Ast.Field) {
			// thinking of giving fields, methods, arguments special labels:
			// "classlabel" + "_" + "method/field"
			// "classlabel" + "_" + "method/field" + "_" + "var_" + "varname"
			// but then I must remember in which class/ method I am and inheritance
			// todo: thoughts on that, j?
		} else {
			throw new ToDoException(); // Todo: choose right errocode
		}

		cg.rm.releaseRegister(rhsReg);
		cg.rm.releaseRegister(lhsReg);
		return null;

	}

	@Override
	public Register builtInWrite(BuiltInWrite ast, CurrentContext arg) {
		Register reg = visit(ast.arg(), arg);
		cg.emit.emit("sub", constant(16), STACK_REG);
		cg.emit.emitStore(reg, 4, STACK_REG);
		cg.emit.emitStore(AssemblyEmitter.labelAddress(AstCodeGenerator.DECIMAL_FORMAT_LABEL), 0, STACK_REG);
		cg.emit.emit("call", Config.PRINTF);
		cg.emit.emit("add", constant(16), STACK_REG);
		cg.rm.releaseRegister(reg);
		return null;
	}

	@Override
	public Register builtInWriteln(BuiltInWriteln ast, CurrentContext arg) {
		cg.emit.emit("sub", constant(16), STACK_REG);
		cg.emit.emitStore(AssemblyEmitter.labelAddress(AstCodeGenerator.NEW_LINE_LABEL), 0, STACK_REG);
		cg.emit.emit("call", Config.PRINTF);
		cg.emit.emit("add", constant(16), STACK_REG);
		return null;
	}

	@Override
	public Register returnStmt(ReturnStmt ast, CurrentContext arg) { // todo
		Register ret;

		if (ast.arg() == null){
			cg.emit.emitMove(constant(0), Register.EAX);
		} else {
			ret = visit(ast.arg(), arg);
			cg.emit.emitMove(ret, Register.EAX);
			cg.rm.releaseRegister(ret);
		}

		return Register.EAX;
	}

	@Override
	public Register varDecl(VarDecl ast, CurrentContext arg) {
		String name;

		switch(ast.sym.kind){
			case FIELD:
				Register temp = null;
				name = arg.getClassSymbol().name + "_" + ast.name;
				cg.emit.emitRaw(Config.DATA_INT_SECTION);
				cg.emit.emitRaw(".globl " + name);
				cg.emit.emitLabel(name);
				//should instead of 0, a ptr to the heap location of the data be saved?
				// do I need this, if I move int here after?
				cg.emit.emitConstantData("0");

				//try for heap allocation todo: jcheck
				if (cg.rm.isInUse(Register.EAX)){
					temp = cg.rm.getRegister();
					cg.emit.emitMove(Register.EAX, temp);
				}

				cg.emit.emit("pushl", constant(1));
				cg.emit.emit("pushl", Config.SIZEOF_PTR);
				cg.emit.emit("call", Config.CALLOC);
				cg.emit.emit("addl", constant(2*Config.SIZEOF_PTR), Register.ESP);
				cg.emit.emitMove(Register.EAX, labelAddress(name));

				if (temp != null){
					cg.emit.emitMove(temp, Register.EAX);
					cg.rm.releaseRegister(temp);
				} else {
					cg.rm.releaseRegister(Register.EAX);
				}

				// todo: add to vtable
				break;
			case LOCAL:
				name = arg.getClassSymbol().name + "_" + arg.getMethodSymbol().name + "_" + ast.name;
				arg.addLocal(name);
				cg.emit.emit("subl", constant(4), Register.ESP);
				break;
			default:
				//todo: giz de error überhaupt?
				break;
		}

		return null;

	}
}
