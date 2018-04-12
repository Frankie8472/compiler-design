package cd.frontend.semantic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cd.Main;
import cd.ir.Ast;
import cd.ir.Ast.MethodDecl;
import cd.ir.Ast.VarDecl;
import cd.ir.Ast.ClassDecl;
import cd.ir.AstVisitor;
import cd.ir.Symbol;
import cd.ir.Symbol.TypeSymbol;
import cd.ir.Symbol.MethodSymbol;
import cd.ir.Symbol.ArrayTypeSymbol;
import cd.ir.Symbol.PrimitiveTypeSymbol;
import cd.ir.Symbol.ClassSymbol;
import cd.ir.Symbol.VariableSymbol;

public class SemanticAnalyzer extends AstVisitor<Void, SymbolWrapper> {

    public final Main main;

    private Map<String, ClassSymbol> classes = new HashMap<>();

    public SemanticAnalyzer(Main main) {
        this.main = main;
    }

    public void check(List<ClassDecl> classDecls) throws SemanticFailure {
        // Transform classes to symbols
        for (ClassDecl decl : classDecls) {
            if (classes.containsKey(decl.name)) {
                throw new SemanticFailure(SemanticFailure.Cause.DOUBLE_DECLARATION);
            }
            if (decl.name.equals("Object")){
                throw new SemanticFailure(SemanticFailure.Cause.OBJECT_CLASS_DEFINED);
            }
            ClassSymbol symbol = new ClassSymbol(decl);
            decl.sym = symbol;
            classes.put(decl.name, symbol);
        }

        // Fill out superclass
        for (ClassDecl decl : classDecls) {
            decl.sym.superClass = (ClassSymbol) stringToTypeSymbol(decl.superClass);
        }

        // Check for circular inheritance
        for(String className : classes.keySet()){
            Symbol.ClassSymbol currentSymbol =  classes.get(className);
            List<ClassSymbol> foundClasses = new ArrayList<>();
            while (currentSymbol.superClass != Symbol.ClassSymbol.objectType) {
                if (foundClasses.contains(currentSymbol)) {
                    throw new SemanticFailure(SemanticFailure.Cause.CIRCULAR_INHERITANCE);
                }
                foundClasses.add(currentSymbol);
                currentSymbol = currentSymbol.superClass;
                if (!classes.containsValue(currentSymbol)) {
                    throw  new SemanticFailure(SemanticFailure.Cause.NO_SUCH_TYPE);
                }
            }
        }

        for(ClassDecl decl : classDecls){
            visit(decl, null);
        }
    }

    @Override
    public Void classDecl(ClassDecl ast, SymbolWrapper arg) {
        ClassSymbol classSymbol = new ClassSymbol(ast);
        SymbolWrapper wrapper = new SymbolWrapper(classSymbol, null);
        for (VarDecl varDecl : ast.fields()) {
            visit(varDecl, wrapper);
        }

        for (MethodDecl methodDecl : ast.methods()) {
            visit(methodDecl, wrapper);
        }
        ast.sym = classSymbol;
        //todo: check for failure
        return null;
    }

    @Override
    public Void methodDecl(MethodDecl ast, SymbolWrapper arg) {
        MethodSymbol methodSymbol = new MethodSymbol(ast);
        SymbolWrapper wrapper = new SymbolWrapper(methodSymbol, arg);
        methodSymbol.returnType = stringToTypeSymbol(ast.returnType);
        for (int i = 0; i < ast.argumentNames.size(); i++) {
            String name = ast.argumentNames.get(i);
            String type = ast.argumentTypes.get(i);
            methodSymbol.parameters.add(new VariableSymbol(name, stringToTypeSymbol(type), VariableSymbol.Kind.PARAM));
        }

        visit(ast.decls(), wrapper);
        visit(ast.body(), arg);

        ((ClassSymbol) arg.parentSymbol.symbol).methods.put(ast.name, methodSymbol);
        ast.sym = methodSymbol;
        //todo: check for failure
        return null;
    }

    @Override
    public Void varDecl(VarDecl ast, SymbolWrapper arg) {
        VariableSymbol variableSymbol = null;
        if (arg.parentSymbol.symbol instanceof ClassSymbol) {
            variableSymbol = new VariableSymbol(ast.name, stringToTypeSymbol(ast.type), VariableSymbol.Kind.FIELD);
            ((ClassSymbol) arg.parentSymbol.symbol).fields.put(ast.name, variableSymbol);
        } else if (arg.parentSymbol.symbol instanceof MethodSymbol) {
            variableSymbol = new VariableSymbol(ast.name, stringToTypeSymbol(ast.type), VariableSymbol.Kind.LOCAL);
            ((MethodSymbol) arg.parentSymbol.symbol).locals.put(ast.name, variableSymbol);
        }

        ast.sym = variableSymbol;
        //todo: check for failure
        return null;
    }

    @Override
    public Void assign(Ast.Assign ast, SymbolWrapper arg) {
        visitChildren(ast, null);
        if (!ast.left().type.equals(ast.right().type)) {    // todo: fix subtyping
            throw new SemanticFailure(SemanticFailure.Cause.TYPE_ERROR); // todo: choose correct cause
        }

        return null;
    }

    @Override
    public Void builtInWrite(Ast.BuiltInWrite ast, SymbolWrapper arg) {
        visit(ast.arg(), null);
        // todo: check for failure
        return null;
    }

    @Override
    public Void builtInWriteln(Ast.BuiltInWriteln ast, SymbolWrapper arg) {
        if (ast.children() != null) {
            throw new SemanticFailure(SemanticFailure.Cause.WRONG_NUMBER_OF_ARGUMENTS); //todo: choose correct cause
        }
        return null;
    }

    @Override
    public Void ifElse(Ast.IfElse ast, SymbolWrapper arg) {
        visit(ast.condition(), null);
        visit(ast.then(), null);
        visit(ast.otherwise(), null);
        // todo: check for failure
        return null;
    }

    @Override
    public Void returnStmt(Ast.ReturnStmt ast, SymbolWrapper arg) {
        visit(ast.arg(), null);
        //todo: check for failure
        return null;
    }

    @Override
    public Void whileLoop(Ast.WhileLoop ast, SymbolWrapper arg) {
        visit(ast.condition(), null);
        visit(ast.body(), null);
        // todo: check for failure
        return null;
    }

    @Override
    public Void binaryOp(Ast.BinaryOp ast, SymbolWrapper arg) {
        visit(ast.left(), null);
        visit(ast.right(), null);
        // todo: check for failure
        return null;
    }

	/*
	todo: needed?!
	@Override
	public Void methodCall(Ast.MethodCall ast, SymbolWrapper arg) {
		return super.methodCall(ast, arg);
	}*/

    @Override
    public Void booleanConst(Ast.BooleanConst ast, SymbolWrapper arg) {
        ast.type = PrimitiveTypeSymbol.booleanType;
        //todo: do we have to check if true or false is wrong written?
        return null;
    }

    @Override
    public Void intConst(Ast.IntConst ast, SymbolWrapper arg) {
        ast.type = PrimitiveTypeSymbol.intType;
        //todo: do we have to check if it actually is an integer?!
        return null;
    }

    @Override
    public Void nullConst(Ast.NullConst ast, SymbolWrapper arg) {
        ast.type = ClassSymbol.nullType; // todo: is this correct?
        return null;
    }

    @Override
    public Void builtInRead(Ast.BuiltInRead ast, SymbolWrapper arg) {
        ast.type = PrimitiveTypeSymbol.intType;
        if (ast.children() != null) {
            throw new SemanticFailure(SemanticFailure.Cause.WRONG_NUMBER_OF_ARGUMENTS);
        }
        return null;
    }

    @Override
    public Void unaryOp(Ast.UnaryOp ast, SymbolWrapper arg) {
        visit(ast.arg(), null);
//        if ((ast.operator.equals(Ast.UnaryOp.UOp.U_BOOL_NOT) && !ast.arg().type.equals(PrimitiveTypeSymbol.booleanType)
//                ||) {
//            throw new SemanticFailure(SemanticFailure.Cause.TYPE_ERROR);
//        }

        ast.type = ast.arg().type;
        return null;
    }


    //todo: is this needed?!
    @Override
    public Void var(Ast.Var ast, SymbolWrapper arg) {
//        ((MethodSymbol) arg).
        //ast.sym - VariableSymbol
        //ast.type - TypeSymbol
        return null;
    }

    // -------------- Helper Functions ---------------

    private TypeSymbol stringToTypeSymbol(String typeName) {
        TypeSymbol type;

        boolean isArray = false;

        if (typeName.endsWith("[]")) {
            isArray = true;
            typeName = typeName.substring(0, typeName.length() - 2);
        }

        switch (typeName) {
            case "int":
                type = PrimitiveTypeSymbol.intType;
                break;
            case "void":
                type = PrimitiveTypeSymbol.voidType;
                break;
            case "boolean":
                type = PrimitiveTypeSymbol.booleanType;
                break;
            case "Object":
                type = ClassSymbol.objectType;
                break;
            default:
                if(!classes.containsKey(typeName)){
                    throw new SemanticFailure(SemanticFailure.Cause.NO_SUCH_TYPE);
                }
                type = classes.get(typeName);
                break;
        }

        if (isArray) {
            type = new ArrayTypeSymbol(type);
        }

        return type;
    }

}
