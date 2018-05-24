package cd.backend.codegen;

import static cd.Config.MAIN;
import static cd.backend.codegen.AssemblyEmitter.constant;
import static cd.backend.codegen.RegisterManager.STACK_REG;

import java.util.ArrayList;
import java.util.Iterator;
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
import cd.ir.Ast.Field;
import cd.ir.Ast.IfElse;
import cd.ir.Ast.Index;
import cd.ir.Ast.MethodCall;
import cd.ir.Ast.MethodDecl;
import cd.ir.Ast.ReturnStmt;
import cd.ir.Ast.Var;
import cd.ir.Ast.VarDecl;
import cd.ir.Ast.WhileLoop;
import cd.ir.AstVisitor;
import cd.ir.Symbol;
import cd.ir.Symbol.MethodSymbol;
import cd.ir.Symbol.PrimitiveTypeSymbol;
import cd.ir.Symbol.VariableSymbol;
import cd.util.debug.AstOneLine;

/**
 * Generates code to process statements and declarations.
 */
class StmtGenerator extends AstVisitor<Register, Void> {
	protected final AstCodeGenerator cg;
	
	public final String ELSE="else";
	public final String ENDIF="endIf";
	public final String LOOP="loop";
	public final String LOOPEND="loopend";
	
	
	public int ifCounter=0;
	public int loopCounter=0;
	
	

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
		return cg.eg.gen(ast.getMethodCallExpr());
	}

	public Register methodCall(MethodSymbol sym, List<Expr> allArguments) {
		throw new RuntimeException("Not required");
	}

	@Override
	public Register classDecl(ClassDecl ast, Void arg) {
		{
			cg.currentClass=ast.sym;
			return visitChildren(ast, arg);
			
		}
	}

	@Override
	public Register methodDecl(MethodDecl ast, Void arg) {
		{
			
			Symbol classSymbol=cg.currentClass;
			
			// Emit the method:
			VTable vtable=cg.vtables.get(classSymbol.name);
			String label=vtable.getLabel(ast.sym);
			ArrayList<VariableSymbol> localTable=new ArrayList<VariableSymbol>();
			
			Iterator<VariableSymbol> it=ast.sym.locals.values().iterator();
			while(it.hasNext()) {
				localTable.add(it.next());
			}
			
			cg.localTables.put(label, localTable);
			
			
			cg.emit.emitRaw(".globl " +label );
			cg.emit.emitLabel(label);
			
						
			/*
			 * Set up stack
			 * 
			 * Should look like this:
			 * 
			 * parameter3
			 * parameter2
			 * parameter1
			 * target
			 * return adress
			 * old ebp <-- new ebp
			 * local 1
			 * local 2
			 * .
			 * .
			 * callee saved registers <--new esp
			 * 
			 */
			
			
			cg.emit.emit("pushl",Register.EBP);
			cg.emit.emitMove(Register.ESP, Register.EBP);
			
			
			//initialize all locals to 0
			for(int i=0;i<localTable.size();i++) {
				cg.emit.emit("pushl",AssemblyEmitter.constant(0));
			}
			for(int i=0;i<RegisterManager.CALLEE_SAVE.length;i++) {
				cg.emit.emit("pushl", RegisterManager.CALLEE_SAVE[i]);
			}
			
			cg.currentLabel=label;
			cg.currentSymbol=ast.sym;
			gen(ast.body());
			
			
			cg.emitMethodSuffix(true);
			return null;
		}
	}

	@Override
	public Register ifElse(IfElse ast, Void arg) {
		ifCounter++;
		int count = ifCounter;
		
		Register cond = cg.eg.gen(ast.condition());
		cg.emit.emit("cmp", AssemblyEmitter.constant(0),cond);
		cg.rm.releaseRegister(cond);
		cg.emit.emit("je", ELSE+count);
		gen(ast.then());
		cg.emit.emit("jmp", ENDIF+count);
		cg.emit.emitLabel(ELSE+count);
		gen(ast.otherwise());
		cg.emit.emitLabel(ENDIF+count);
		return null;
	}

	@Override
	public Register whileLoop(WhileLoop ast, Void arg) {
		loopCounter++;
		int count = loopCounter;

		cg.emit.emitLabel(LOOP+count);
		Register cond = cg.eg.gen(ast.condition());
		cg.emit.emit("cmp",AssemblyEmitter.constant(0),cond);
		cg.rm.releaseRegister(cond);
		cg.emit.emit("je", LOOPEND+count);
		gen(ast.body());
		cg.emit.emit("jmp", LOOP+count);
		cg.emit.emitLabel(LOOPEND+count);
		
		return null;
		}

	@Override
	public Register assign(Assign ast, Void arg) {
		{
			Register rhsReg = cg.eg.gen(ast.right());
			Ast astLeft=ast.left();
			
			if(Var.class.isInstance(astLeft)) {
				int offset;
				Register reg=cg.rm.getRegister();
				switch(((Var)astLeft).sym.kind) {
				case LOCAL:
					List<VariableSymbol> table = cg.localTables.get(cg.currentLabel);
					offset=Config.SIZEOF_PTR*(table.indexOf(((Var)astLeft).sym)+1);
					cg.emit.emitMove(Register.EBP, reg);
					cg.emit.emitStore(rhsReg, -offset, reg);
					break;
				case PARAM:
					MethodSymbol symbol=((MethodSymbol) cg.currentSymbol);
					offset=symbol.parameters.indexOf(((Var)astLeft).sym);
					offset=Config.SIZEOF_PTR*offset+3*Config.SIZEOF_PTR;
					cg.emit.emitMove(Register.EBP, reg);
					cg.emit.emitStore(rhsReg,offset, reg);
					break;
					
				case FIELD:
					cg.emit.emitLoad(2*Config.SIZEOF_PTR, Register.EBP, reg);
					offset=cg.vtables.get(cg.currentClass.name).getOffset(((Var)astLeft).sym);
					cg.emit.emitStore(rhsReg, offset, reg);
					break;
				}
				
				cg.rm.releaseRegister(reg);
				
			
			}
			if(Index.class.isInstance(astLeft)) {
				
				Index ind=(Index)astLeft;
				Register array=cg.eg.gen(ind.left());
				cg.emit.emit("cmp", AssemblyEmitter.constant(0),array);
				cg.emit.emit("je", "_exit4");
				
				Register index=cg.eg.gen(ind.right());
				
				cg.emit.emit("cmp",AssemblyEmitter.constant(0),index );
				cg.emit.emit("jl", "_exit3");
				cg.emit.emit("cmp",AssemblyEmitter.registerOffset(Config.SIZEOF_PTR, array),index);
				cg.emit.emit("jge", "_exit3");
				cg.emit.emitMove(rhsReg,AssemblyEmitter.arrayAddress(array, index));
				cg.rm.releaseRegister(index);
				
			}
			if(Field.class.isInstance(astLeft)) {
				Register register=cg.eg.gen(((Field)astLeft).arg());
				cg.emit.emit("cmp", AssemblyEmitter.constant(0),register);
				cg.emit.emit("je", "_exit4");
				
				int offset=cg.vtables.get(((Field)astLeft).arg().type.name).getOffset(((Field)astLeft).sym);
				cg.emit.emitStore(rhsReg, offset, register);
				cg.rm.releaseRegister(register);

				
			}
			
			cg.rm.releaseRegister(rhsReg);
			return null;
		}
	}

	@Override
	public Register builtInWrite(BuiltInWrite ast, Void arg) {
		{
			Register reg = cg.eg.gen(ast.arg());
			cg.emit.emit("sub", constant(16), STACK_REG);
			cg.emit.emitStore(reg, 4, STACK_REG);
			cg.emit.emitStore("$STR_D", 0, STACK_REG);
			cg.emit.emit("call", Config.PRINTF);
			cg.emit.emit("add", constant(16), STACK_REG);
			cg.rm.releaseRegister(reg);
			return null;
		}
	}

	@Override
	public Register builtInWriteln(BuiltInWriteln ast, Void arg) {
		{
			cg.emit.emit("sub", constant(16), STACK_REG);
			cg.emit.emitStore("$STR_NL", 0, STACK_REG);
			cg.emit.emit("call", Config.PRINTF);
			cg.emit.emit("add", constant(16), STACK_REG);
			return null;
		}
	}

	@Override
	public Register returnStmt(ReturnStmt ast, Void arg) {
		if(ast.arg()==null) {
			cg.emit.emitMove(AssemblyEmitter.constant(0), Register.EAX);
		}else {
			Register res=cg.eg.gen(ast.arg());
			cg.emit.emitMove(res,Register.EAX);
			cg.rm.releaseRegister(res);
		}
		
		
		for(int i=RegisterManager.CALLEE_SAVE.length-1;i>=0;i--) {
			cg.emit.emit("popl", RegisterManager.CALLEE_SAVE[i]);
		}
		
		cg.emit.emitMove(Register.EBP, Register.ESP);
		cg.emit.emit("popl", Register.EBP);
		cg.emit.emitRaw("ret");
		return null;
	}
	

}
