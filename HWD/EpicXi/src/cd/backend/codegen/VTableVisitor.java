package cd.backend.codegen;

import static cd.Config.MAIN;
import static cd.backend.codegen.AssemblyEmitter.constant;
import static cd.backend.codegen.RegisterManager.STACK_REG;

import java.util.ArrayList;
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
import cd.ir.Symbol.ClassSymbol;
import cd.ir.Symbol.MethodSymbol;
import cd.ir.Symbol.VariableSymbol;
import cd.util.debug.AstOneLine;

/**
 * Generates code to process statements and declarations.
 */
class VTableVisitor extends AstVisitor<VTable, VTable> {
	
	protected static int currentLabelNo = 0;
	protected int currentClassNo = 3;
	
	public static String getNewLabel() {
		return ".ML" + (currentLabelNo++);
	}
	
	@Override
	public VTable classDecl(ClassDecl ast, VTable arg) {
		
		//class identifier
		if (arg.classNumber == 0)
		{
			currentClassNo += 2;
			arg.classNumber = currentClassNo;
		}
		
		//Return if already visited
		if (ast.sym.vTable != null) 
		{
			for (String fieldName : ast.sym.vTable.fields.keySet()) arg.fields.put(fieldName, ast.sym.vTable.fields.get(fieldName));
			for (String methodName : ast.sym.vTable.methodSymbols.keySet()) arg.methodSymbols.put(methodName, ast.sym.vTable.methodSymbols.get(methodName));
			return arg;
		}
		
		//Visit superclass first
		ClassSymbol superClassSymbol = ast.sym.superClass;
		if (superClassSymbol != null && superClassSymbol != ClassSymbol.objectType)
		{
			arg = visit(superClassSymbol.ast, arg);
		}
		
		super.classDecl(ast, arg);
		
		ast.sym.vTable = arg;
		
		return arg;
	}
	
	@Override
	public VTable methodDecl(MethodDecl ast, VTable arg) {
		String newLabel = getNewLabel();
		
		ast.sym.methodLabel = newLabel;
		
		//set local variable offsets
		List<VariableSymbol> varSymbolList = new ArrayList<VariableSymbol>();
		varSymbolList.addAll(ast.sym.locals.values());
		int varOffset = -Config.SIZEOF_PTR;
		for (VariableSymbol variableSymbol : varSymbolList)
		{
			variableSymbol.offset = varOffset;
			varOffset -= Config.SIZEOF_PTR;
		}
		
		//set param offsets
		List<VariableSymbol> paramSymbolList = ast.sym.parameters;
		//After EBP and Return address
		int paramOffset = 2 * Config.SIZEOF_PTR;
		for (VariableSymbol parameterSymbol : paramSymbolList)
		{
			parameterSymbol.offset = paramOffset;
			paramOffset += Config.SIZEOF_PTR;
		}

		//put new label or replace old label
		boolean overriddenMethod = arg.methodSymbols.containsKey(ast.name);
		int oldOffset = 0;
		if (overriddenMethod) oldOffset = arg.methodSymbols.get(ast.name).offset;
		
		arg.methodSymbols.put(ast.name, ast.sym);
		
		if (overriddenMethod) ast.sym.offset = oldOffset;
		else ast.sym.offset = (arg.fields.size() + arg.methodSymbols.size()) * Config.SIZEOF_PTR;

		return arg;
	}
	
	@Override
	public VTable varDecl(VarDecl ast, VTable arg) {
		
		//put new field if not already there
		arg.fields.put(ast.name, ast.sym);
		
		ast.sym.offset = (arg.fields.size() + arg.methodSymbols.size()) * Config.SIZEOF_PTR;
		
		return arg;
	}

}
