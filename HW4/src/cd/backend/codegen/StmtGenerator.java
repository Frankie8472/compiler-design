package cd.backend.codegen;

import static cd.backend.codegen.RegisterManager.STACK_REG;

import cd.Config;
import cd.ToDoException;
import cd.backend.ExitCode;
import cd.backend.codegen.RegisterManager.Register;
import cd.ir.Ast;
import cd.ir.Ast.Assign;
import cd.ir.Ast.BuiltInWrite;
import cd.ir.Ast.BuiltInWriteln;
import cd.ir.Ast.ClassDecl;
import cd.ir.Ast.IfElse;
import cd.ir.Ast.MethodCall;
import cd.ir.Ast.MethodDecl;
import cd.ir.Ast.ReturnStmt;
import cd.ir.Ast.Var;
import cd.ir.Ast.VarDecl;
import cd.ir.Ast.WhileLoop;
import cd.ir.AstVisitor;
import cd.ir.Symbol.PrimitiveTypeSymbol;
import cd.ir.Symbol.VariableSymbol;
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
		Register reg =  cg.eg.visit(ast.getMethodCallExpr(), dummy);
		cg.rm.releaseRegister(reg);
		return null;
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
		cg.emit.emitLabel(name);

        cg.emit.emit("push", Register.EBP);
        cg.emit.emitMove(Register.ESP, Register.EBP);

		current.addParameter("this");

		for (String arg_names : ast.argumentNames){
			current.addParameter(LabelUtil.generateLocalLabelName(arg_names, current));
		}

		visit(ast.decls(), current);
		visit(ast.body(), current);

		if(ast.sym.returnType != PrimitiveTypeSymbol.voidType)
		    cg.emitMethodSuffix(false); // leave expression
        else
            cg.emitMethodSuffix(true); // leave expression
		return null;
	}

	@Override
	public Register ifElse(IfElse ast, CurrentContext arg) {
		String else_label = Config.LOCALLABEL + cg.emit.uniqueLabel();
		String end_label = Config.LOCALLABEL + cg.emit.uniqueLabel();

		Register condition = cg.eg.visit(ast.condition(), arg); // will contain boolean 1 or 0

		//test
		cg.emit.emit("testl", condition, condition);
		cg.emit.emit("je", else_label);

		cg.rm.releaseRegister(condition);

		//true
        visit(ast.then(), arg);

        if(!(ast.otherwise() instanceof Ast.Nop)) {
			cg.emit.emit("jmp", end_label);
		}

		//else
        cg.emit.emitLabel(else_label);
        visit(ast.otherwise(), arg);

		//end
        if(!(ast.otherwise() instanceof Ast.Nop)) {
            cg.emit.emitLabel(end_label);
        }
		return null;
	}

	@Override
	public Register whileLoop(WhileLoop ast, CurrentContext arg) {
		String loop_label = Config.LOCALLABEL + cg.emit.uniqueLabel();
		String end_label = Config.LOCALLABEL + cg.emit.uniqueLabel();

		//loop beginning
		cg.emit.emitLabel(loop_label);

		// will contain boolean 1 or 0
		Register condition = cg.eg.visit(ast.condition(), arg);

		//test
		cg.emit.emit("testl", condition, condition);
		cg.emit.emit("je", end_label);

		//loop body
        cg.rm.releaseRegister(condition);
		visit(ast.body(), arg);
        cg.emit.emit("jmp", loop_label);

		//exit loop
		cg.emit.emitLabel(end_label);
		return null;
	}

	@Override
	public Register assign(Assign ast, CurrentContext arg) {
		Register rhsReg = cg.eg.visit(ast.right(), arg);

		if (ast.left() instanceof Ast.Index){
            Ast.Index index = (Ast.Index) ast.left();
		    Register arrayAddr = cg.eg.visit(index.left(), arg);
		    Register arrayIndex = cg.eg.visit(index.right(), arg);

		    cg.emit.emit("null_ptr_check", arrayAddr);

            cg.emit.emit("cmpl", AssemblyEmitter.constant(0), arrayIndex);
            cg.emit.emit("jl", ExitCode.INVALID_ARRAY_BOUNDS.name());
            cg.emit.emit("cmpl", AssemblyEmitter.registerOffset(Config.SIZEOF_PTR, arrayAddr), arrayIndex);
            cg.emit.emit("jge", ExitCode.INVALID_ARRAY_BOUNDS.name());

            cg.emit.emitMove(rhsReg, AssemblyEmitter.arrayAddress(arrayAddr, arrayIndex));
            cg.rm.releaseRegister(arrayAddr);
            cg.rm.releaseRegister(arrayIndex);

		} else if (ast.left() instanceof Ast.Var) {

			Var var = (Var) ast.left();
			Integer offset;
			if(var.sym.kind == VariableSymbol.Kind.FIELD){
                offset = cg.vTables.get(arg.getClassSymbol().name).getFieldOffset(var.name);
                Register temp = cg.rm.getRegister();
                cg.emit.emitLoad(arg.getOffset("this"), Register.EBP, temp);

                cg.emit.emitStore(rhsReg, offset, temp);
                cg.rm.releaseRegister(temp);
            } else {
                offset = arg.getOffset(LabelUtil.generateLocalLabelName(var.name, arg));
                cg.emit.emitStore(rhsReg, offset, Register.EBP);
            }

		} else if (ast.left() instanceof Ast.Field) {
            cg.emit.emit("pushl", rhsReg);
            cg.rm.releaseRegister(rhsReg);

			Ast.Field field = (Ast.Field) ast.left();
			Register classAddr = cg.eg.visit(field.arg(), arg);

            cg.emit.emit("null_ptr_check", classAddr);


            rhsReg = cg.rm.getRegister();
			cg.emit.emit("popl", rhsReg);

			Integer fieldOffset = cg.vTables.get(field.arg().type.name).getFieldOffset(field.fieldName);
            cg.emit.emitMove(rhsReg, AssemblyEmitter.registerOffset(fieldOffset, classAddr));
			cg.rm.releaseRegister(classAddr);

		} else {
			//Not necessary as it seems
			throw new RuntimeException("Unknown AST Type. This should never happen!");
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
		cg.emit.emitRaw("leave");
		cg.emit.emitRaw("ret");
		return null;
	}

	@Override
	public Register varDecl(VarDecl ast, CurrentContext arg) {
		String name;

		switch(ast.sym.kind){
			case LOCAL:
				name = LabelUtil.generateLocalLabelName(ast.name, arg);
				arg.addLocal(name);
				cg.emit.emit("pushl", AssemblyEmitter.constant(0));
				break;
			default:
				break;
		}

		return null;

	}
}
