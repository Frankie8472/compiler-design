package cd.frontend.semantic;

import java.util.List;

import cd.Main;
import cd.ToDoException;
import cd.ir.Ast;
import cd.ir.Ast.MethodDecl;
import cd.ir.Ast.VarDecl;
import cd.ir.Ast.ClassDecl;
import cd.ir.AstVisitor;
import cd.ir.Symbol;
import cd.ir.Symbol.MethodSymbol;
import cd.ir.Symbol.ArrayTypeSymbol;
import cd.ir.Symbol.PrimitiveTypeSymbol;
import cd.ir.Symbol.TypeSymbol;
import cd.ir.Symbol.ClassSymbol;
import cd.ir.Symbol.VariableSymbol;

import javax.lang.model.type.PrimitiveType;

public class SemanticAnalyzer extends AstVisitor<Symbol, Symbol>{
	
	public final Main main;
	
	public SemanticAnalyzer(Main main) {
		this.main = main;
	}
	
	public void check(List<ClassDecl> classDecls) 
	throws SemanticFailure {
		{
			for(ClassDecl decl : classDecls) {
				visit(decl, null);
			}
		}
	}

	@Override
	public Symbol classDecl(ClassDecl ast, Symbol sym) {
		ClassSymbol classSymbol = new ClassSymbol(ast);
		for (VarDecl varDecl : ast.fields()) {
			classSymbol.fields.put(varDecl.name, (VariableSymbol) visit(varDecl, classSymbol));
		}

		for (MethodDecl methodDecl : ast.methods()) {
			classSymbol.methods.put(methodDecl.name, (MethodSymbol) visit(methodDecl, classSymbol));
		}


		return null;
	}

	@Override
	public Symbol varDecl(VarDecl ast, Symbol sym) {
		VariableSymbol variableSymbol = null;
		if (sym instanceof ClassSymbol) {
			variableSymbol = new VariableSymbol(ast.name, stringToTypeSymbol(ast.type), VariableSymbol.Kind.FIELD);
		}

		return variableSymbol;
	}

	@Override
	public Symbol methodDecl(MethodDecl ast, Symbol arg) {
		MethodSymbol methodSymbol = null;

		return methodSymbol;
	}

	public TypeSymbol stringToTypeSymbol(String type) {
		if (type == null) {
			return PrimitiveTypeSymbol.voidType;
		}else if (type.equals("int")){
			return PrimitiveTypeSymbol.intType;
		} else if (type.equals("boolean")){
			return PrimitiveTypeSymbol.booleanType;
		} else if (type.equals("int[]")){
			return new ArrayTypeSymbol(PrimitiveTypeSymbol.intType);
		} else if (type.equals("boolean[]")){
			return new ArrayTypeSymbol(PrimitiveTypeSymbol.booleanType);
		} else if (type.contains("[]")){
			return new ArrayTypeSymbol(new TypeSymbol(type.substring(0, type.length()-3)) {
				@Override
				public boolean isReferenceType() {
					return true;
				}
			});
		} else {
			return new TypeSymbol(type) {
				@Override
				public boolean isReferenceType() {
					return true;
				}
			};
		}
	}

}
