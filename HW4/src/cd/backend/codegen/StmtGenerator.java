package cd.backend.codegen;

import static cd.backend.codegen.RegisterManager.STACK_REG;

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
import cd.ir.Symbol;
import cd.ir.Symbol.MethodSymbol;
import cd.util.debug.AstOneLine;

/**
 * Generates code to process statements and declarations.
 */
class StmtGenerator extends AstVisitor<Register, CurrentContext> {
	protected final AstCodeGenerator cg;


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

	public Register methodCall(MethodCall ast, CurrentContext dummy) {
		return cg.eg.visit(ast.getMethodCallExpr(), dummy);
	}

	@Override
	public Register classDecl(ClassDecl ast, CurrentContext arg) {
		CurrentContext current = new CurrentContext(ast.sym);

		cg.vTables.get(ast.name).emitStaticMethodVTable(cg.emit);

		visitChildren(ast, current);

		return null;
	}

	@Override
	public Register methodDecl(MethodDecl ast, CurrentContext arg) {
		CurrentContext current = new CurrentContext(arg, ast.sym);
		String name = LabelUtil.generateMethodLabelName(current.getClassSymbol().name, ast.name);

		cg.emit.emitRaw(Config.TEXT_SECTION);
		cg.emit.emitLabel(name);

        cg.emit.emit("push", Register.EBP);
        cg.emit.emitMove(Register.ESP, Register.EBP);
        // Align stack to be on an address dividable by 16. Important for Macs.
//		cg.emit.emit("and", -16, STACK_REG);


		current.addParameter("this");

		for (String arg_names : ast.argumentNames){
			current.addParameter(LabelUtil.generateLocalLabelName(arg_names, current));
		}

		visit(ast.decls(), current);
		visit(ast.body(), current);

		if(ast.sym.returnType != Symbol.PrimitiveTypeSymbol.voidType)
		    cg.emitMethodSuffix(false); // leave expression
        else
            cg.emitMethodSuffix(true); // leave expression
		return null;

	}

	@Override
	public Register ifElse(IfElse ast, CurrentContext arg) { // todo
		String else_label = cg.emit.uniqueLabel();
		String end_label = cg.emit.uniqueLabel();

		Register condition = cg.eg.visit(ast.condition(), arg); // will contain boolean 1 or 0

		//test
		cg.emit.emit("testl", condition, condition);
		cg.emit.emit("je", else_label);

		//true
        cg.emit.emit("pushl", condition);
        cg.rm.releaseRegister(condition);
        visit(ast.then(), arg);
        condition = cg.rm.getRegister();
        cg.emit.emit("popl", condition);
		cg.emit.emit("jmp", end_label);

		//else
        cg.emit.emitLabel(else_label);
        cg.emit.emit("pushl", condition);
        cg.rm.releaseRegister(condition);
        visit(ast.otherwise(), arg);
        condition = cg.rm.getRegister();
        cg.emit.emit("popl", condition);

		//end
		cg.emit.emitLabel(end_label);

		cg.rm.releaseRegister(condition);
		return null;
	}

	@Override
	public Register whileLoop(WhileLoop ast, CurrentContext arg) {
		String loop_label = cg.emit.uniqueLabel();
		String end_label = cg.emit.uniqueLabel();

		//loop beginning
		cg.emit.emitLabel(loop_label);
		Register condition = cg.eg.visit(ast.condition(), arg); // will contain boolean 1 or 0

		//test
		cg.emit.emit("testl", condition, condition);
		cg.emit.emit("je", end_label);

		//loop body
        cg.emit.emit("pushl", condition);
        cg.rm.releaseRegister(condition);
		visit(ast.body(), arg);
        condition = cg.rm.getRegister();
        cg.emit.emit("popl", condition);
        cg.emit.emit("jmp", loop_label);

		//exit loop
		cg.emit.emitLabel(end_label);

		cg.rm.releaseRegister(condition);
		return null;
	}

	@Override
	public Register assign(Assign ast, CurrentContext arg) {
		/*
			if (!(ast.left() instanceof Var))
				throw new RuntimeException("LHS must be var in HW1");
			*/
//		Register lhsReg = cg.eg.visit(ast.left(), arg);

		Register rhsReg = cg.eg.visit(ast.right(), arg);
		cg.emit.emit("pushl", rhsReg);
		cg.rm.releaseRegister(rhsReg);

		if (ast.left() instanceof Ast.Index){
            rhsReg = cg.rm.getRegister();
            cg.emit.emit("popl", rhsReg);
            Ast.Index index = (Ast.Index) ast.left();
		    Register arrayAddr = cg.eg.visit(index.left(), arg);
		    Register arrayIndex = cg.eg.visit(index.right(), arg);

            cg.emit.emitMove(rhsReg, AssemblyEmitter.arrayAddress(arrayAddr, arrayIndex));
//			cg.emit.emitStore(rhsReg, 0, lhsReg);
//            cg.rm.releaseRegister(lhsReg);

		} else if (ast.left() instanceof Ast.Var) {

			rhsReg = cg.rm.getRegister();
			cg.emit.emit("popl", rhsReg);

			Var var = (Var) ast.left();
			Integer offset;
			if(var.sym.kind == Symbol.VariableSymbol.Kind.FIELD){
                offset = cg.vTables.get(arg.getClassSymbol().name).getFieldOffset(var.name);
                Register temp = cg.rm.getRegister();
                cg.emit.emitLoad(arg.getOffset("this"), Register.EBP, temp);

                cg.emit.emitStore(rhsReg, offset, temp);
                cg.rm.releaseRegister(temp);
            } else {
                offset = arg.getOffset(LabelUtil.generateLocalLabelName(var.name, arg));
                cg.emit.emitStore(rhsReg, offset, Register.EBP);
            }
//			cg.emit.emit("movl", rhsReg, AstCodeGenerator.VAR_PREFIX + var.name);
//			cg.rm.releaseRegister(rhsReg);

		} else if (ast.left() instanceof Ast.Field) {
			Ast.Field field = (Ast.Field) ast.left();
			Register classAddr = cg.eg.visit(field.arg(), arg);

			rhsReg = cg.rm.getRegister();
			cg.emit.emit("popl", rhsReg);

			Integer fieldOffset = cg.vTables.get(field.arg().type.name).getFieldOffset(field.fieldName);
            cg.emit.emitMove(rhsReg, AssemblyEmitter.registerOffset(fieldOffset, classAddr));
			cg.rm.releaseRegister(classAddr);

		} else {
			throw new ToDoException(); // Todo: choose right errorcode
		}

		cg.rm.releaseRegister(rhsReg);

		return null;

	}

	@Override
	public Register builtInWrite(BuiltInWrite ast, CurrentContext arg) {
		Register reg = cg.eg.visit(ast.arg(), arg);
		cg.emit.emit("sub", AssemblyEmitter.constant(16), STACK_REG);
		cg.emit.emitStore(reg, 4, STACK_REG);
		cg.emit.emitStore(AssemblyEmitter.labelAddress(AstCodeGenerator.DECIMAL_FORMAT_LABEL), 0, STACK_REG);
		cg.emit.emit("call", Config.PRINTF);
		cg.emit.emit("add", AssemblyEmitter.constant(16), STACK_REG);
		cg.rm.releaseRegister(reg);
		return null;
	}

	@Override
	public Register builtInWriteln(BuiltInWriteln ast, CurrentContext arg) {
		cg.emit.emit("sub", AssemblyEmitter.constant(16), STACK_REG);
		cg.emit.emitStore(AssemblyEmitter.labelAddress(AstCodeGenerator.NEW_LINE_LABEL), 0, STACK_REG);
		cg.emit.emit("call", Config.PRINTF);
		cg.emit.emit("add", AssemblyEmitter.constant(16), STACK_REG);
		return null;
	}

	@Override
	public Register returnStmt(ReturnStmt ast, CurrentContext arg) {
		Register ret;

		if (ast.arg() == null){
			cg.emit.emitMove(AssemblyEmitter.constant(0), Register.EAX);
		} else {
			ret = cg.eg.visit(ast.arg(), arg);
			cg.emit.emitMove(ret, Register.EAX);
			cg.rm.releaseRegister(ret);
		}

		return null;
	}

	@Override
	public Register varDecl(VarDecl ast, CurrentContext arg) {
		String name;

		switch(ast.sym.kind){
			case FIELD:
//				Register temp = null;
//				name = arg.getClassSymbol().name + "_" + ast.name;
//				cg.emit.emitRaw(Config.DATA_INT_SECTION);
//				cg.emit.emitLabel(name);
//				//should instead of 0, a ptr to the heap location of the data be saved?
//				// do I need this, if I move int here after?
//				cg.emit.emitConstantData("0");
//
//				//try for heap allocation todo: jcheck
//				if (cg.rm.isInUse(Register.EAX)){
//					temp = cg.rm.getRegister();
//					cg.emit.emitMove(Register.EAX, temp);
//				}
//
//				cg.emit.emit("pushl", constant(1));
//				cg.emit.emit("pushl", Config.SIZEOF_PTR);
//				cg.emit.emit("call", Config.CALLOC);
//				cg.emit.emit("addl", constant(2*Config.SIZEOF_PTR), Register.ESP);
//				cg.emit.emitMove(Register.EAX, labelAddress(name));
//
//				if (temp != null){
//					cg.emit.emitMove(temp, Register.EAX);
//					cg.rm.releaseRegister(temp);
//				}
//
//				// todo: add to vtable
				break;
			case LOCAL:
				name = LabelUtil.generateLocalLabelName(ast.name, arg);
				arg.addLocal(name);
				cg.emit.emit("subl", AssemblyEmitter.constant(4), Register.ESP);
				break;
			default:
				//todo: giz de error überhaupt?
				break;
		}

		return null;

	}
}
