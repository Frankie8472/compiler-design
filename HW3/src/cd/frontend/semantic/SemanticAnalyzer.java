package cd.frontend.semantic;

import java.util.List;

import cd.Main;
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
import com.sun.java.util.jar.pack.Instruction;

public class SemanticAnalyzer extends AstVisitor<Void ,Symbol> {
	
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
	public Void classDecl(ClassDecl ast, Symbol arg) {
		ClassSymbol classSymbol = new ClassSymbol(ast);
		for (VarDecl varDecl : ast.fields()) {
			visit(varDecl, classSymbol);
		}

		for (MethodDecl methodDecl : ast.methods()) {
			visit(methodDecl, classSymbol);
		}
		ast.sym = classSymbol;
		//todo: check for failure
		return null;
	}

	@Override
	public Void methodDecl(MethodDecl ast, Symbol arg) {
		MethodSymbol methodSymbol = new MethodSymbol(ast);
		methodSymbol.returnType = stringToTypeSymbol(ast.returnType);
		for (int i = 0; i < ast.argumentNames.size(); i++) {
			String name = ast.argumentNames.get(i);
			String type = ast.argumentTypes.get(i);
			methodSymbol.parameters.add(new VariableSymbol(name, stringToTypeSymbol(type), VariableSymbol.Kind.PARAM));
		}

		visitChildren(ast, methodSymbol);

		((ClassSymbol) arg).methods.put(ast.name, methodSymbol);
		ast.sym = methodSymbol;
		//todo: check for failure
		return null;
	}

	@Override
	public Void varDecl(VarDecl ast, Symbol arg) {
		VariableSymbol variableSymbol = null;
		if (arg instanceof ClassSymbol) {
			variableSymbol = new VariableSymbol(ast.name, stringToTypeSymbol(ast.type), VariableSymbol.Kind.FIELD);
			((ClassSymbol) arg).fields.put(ast.name, variableSymbol);
		} else if (arg instanceof MethodSymbol){
			variableSymbol = new VariableSymbol(ast.name, stringToTypeSymbol(ast.type), VariableSymbol.Kind.LOCAL);
			((MethodSymbol) arg).locals.put(ast.name, variableSymbol);
		}

		ast.sym = variableSymbol;
		//todo: check for failure
		return null;
	}

	@Override
	public Void assign(Ast.Assign ast, Symbol arg) {
		visitChildren(ast, null);
		if (!ast.left().type.equals(ast.right().type)) {	// todo: fix subtyping
			throw new SemanticFailure(SemanticFailure.Cause.TYPE_ERROR); // todo: choose correct cause
		}

		return null;
	}

	@Override
	public Void builtInWrite(Ast.BuiltInWrite ast, Symbol arg) {
		visit(ast.arg(), null);
		// todo: check for failure
		return null;
	}

	@Override
	public Void builtInWriteln(Ast.BuiltInWriteln ast, Symbol arg) {
		if (ast.children() != null) {
			throw new SemanticFailure(SemanticFailure.Cause.WRONG_NUMBER_OF_ARGUMENTS); //todo: choose correct cause
		}
		return null;
	}

	@Override
	public Void ifElse(Ast.IfElse ast, Symbol arg) {
		visit(ast.condition(), null);
		visit(ast.then(), null);
		visit(ast.otherwise(), null);
		// todo: check for failure
		return null;
	}

	@Override
	public Void returnStmt(Ast.ReturnStmt ast, Symbol arg) {
		visit(ast.arg(), null);
		//todo: check for failure
		return null;
	}

	@Override
	public Void whileLoop(Ast.WhileLoop ast, Symbol arg) {
		visit(ast.condition(), null);
		visit(ast.body(), null);
		// todo: check for failure
		return null;
	}

	@Override
	public Void binaryOp(Ast.BinaryOp ast, Symbol arg) {
		visit(ast.left(), null);
		visit(ast.right(), null);
		// todo: check for failure
		return null;
	}

	/*
	todo: needed?!
	@Override
	public Void methodCall(Ast.MethodCall ast, Symbol arg) {
		return super.methodCall(ast, arg);
	}*/

	@Override
	public Void booleanConst(Ast.BooleanConst ast, Symbol arg) {
		ast.type = PrimitiveTypeSymbol.booleanType;
		//todo: do we have to check if true or false is wrong written?
		return null;
	}

	@Override
	public Void intConst(Ast.IntConst ast, Symbol arg) {
		ast.type = PrimitiveTypeSymbol.intType;
		//todo: do we have to check if it actually is an integer?!
		return null;
	}

	@Override
	public Void nullConst(Ast.NullConst ast, Symbol arg) {
		ast.type = ClassSymbol.nullType; // todo: is this correct?
		return null;
	}

	@Override
	public Void builtInRead(Ast.BuiltInRead ast, Symbol arg) {
		ast.type = PrimitiveTypeSymbol.intType;
		if (ast.children() != null) {
			throw new SemanticFailure(SemanticFailure.Cause.WRONG_NUMBER_OF_ARGUMENTS);
		}
		return null;
	}

	@Override
	public Void unaryOp(Ast.UnaryOp ast, Symbol arg) {
		visit(ast.arg(), null);
		if ((ast.operator.equals(Ast.UnaryOp.UOp.U_BOOL_NOT) && !ast.arg().type.equals(PrimitiveTypeSymbol.booleanType)
			|| ){
			throw new SemanticFailure(SemanticFailure.Cause.TYPE_ERROR);
		}

		ast.type = ast.arg().type;
		return null;
	}

	/*
	//todo: is this needed?!
	@Override
	public Void var(Ast.Var ast, Symbol arg) {
		//ast.sym - VariableSymbol
		//ast.type - TypeSymbol
		return null;
	}*/

    // -------------- Helper Functions ---------------

    private Symbol.TypeSymbol stringToTypeSymbol(String typeName) {
        Symbol.TypeSymbol type;

        boolean isArray = false;

        if (typeName.endsWith("[]")) {
            isArray = true;
            typeName = typeName.substring(0, typeName.length() - 2);
        }

        switch (typeName) {
            case "int":
                type = Symbol.PrimitiveTypeSymbol.intType;
                break;
            case "void":
                type = Symbol.PrimitiveTypeSymbol.voidType;
                break;
            case "boolean":
                type = Symbol.PrimitiveTypeSymbol.booleanType;
                break;
            case "Object":
                type = Symbol.ClassSymbol.objectType;
                break;
            default:
                type = new Symbol.ClassSymbol(typeName);
                break;
        }

        if (isArray) {
            type = new Symbol.ArrayTypeSymbol(type);
        }

        return type;
    }

}
