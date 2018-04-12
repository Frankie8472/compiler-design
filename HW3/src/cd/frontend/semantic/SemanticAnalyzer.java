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

    private TypeManager typeManager;


    public SemanticAnalyzer(Main main) {
        this.main = main;
        this.typeManager = new TypeManager();
    }

    public void check(List<ClassDecl> classDecls) throws SemanticFailure {

        // Transform classes to symbols
        for (ClassDecl decl : classDecls) {
            ClassSymbol symbol = new ClassSymbol(decl);
            typeManager.addType(symbol);

            decl.sym = symbol;
        }

        // Fill out superclass
        for (ClassDecl decl : classDecls) {
            decl.sym.superClass = (ClassSymbol) typeManager.stringToTypeSymbol(decl.superClass);
        }

        // Check for circular inheritance
        for(Symbol.ClassSymbol currentSymbol : typeManager.getTypes()){
            List<ClassSymbol> foundClasses = new ArrayList<>();
            while (currentSymbol.superClass != Symbol.ClassSymbol.objectType) {
                if (foundClasses.contains(currentSymbol)) {
                    throw new SemanticFailure(SemanticFailure.Cause.CIRCULAR_INHERITANCE);
                }
                foundClasses.add(currentSymbol);
                currentSymbol = currentSymbol.superClass;
                if (!typeManager.getTypes().contains(currentSymbol)) {
                    throw  new SemanticFailure(SemanticFailure.Cause.NO_SUCH_TYPE);
                }
            }
        }

        for(ClassDecl decl : classDecls){
            visit(decl, null);
        }

        SemanticChecker checker = new SemanticChecker(typeManager);
        checker.check(classDecls);
    }

    @Override
    public Void classDecl(ClassDecl ast, SymbolWrapper arg) {
        ClassSymbol classSymbol = new ClassSymbol(ast);
        SymbolWrapper wrapper = new SymbolWrapper(classSymbol, null);
        for (VarDecl varDecl : ast.fields()) {
            if(classSymbol.fields.containsKey(varDecl.name)){
                throw new SemanticFailure(SemanticFailure.Cause.DOUBLE_DECLARATION);
            }
            visit(varDecl, wrapper);
            classSymbol.fields.put(varDecl.name, varDecl.sym);
        }

        for (MethodDecl methodDecl : ast.methods()) {
            if(classSymbol.methods.containsKey(methodDecl.name)){
                throw new SemanticFailure(SemanticFailure.Cause.DOUBLE_DECLARATION);
            }
            visit(methodDecl, wrapper);
            classSymbol.methods.put(methodDecl.name, methodDecl.sym);
        }
        ast.sym = classSymbol;
        return null;
    }

    @Override
    public Void methodDecl(MethodDecl ast, SymbolWrapper arg) {
        MethodSymbol methodSymbol = new MethodSymbol(ast);
        SymbolWrapper wrapper = new SymbolWrapper(methodSymbol, arg);
        methodSymbol.returnType = typeManager.stringToTypeSymbol(ast.returnType);
        for (int i = 0; i < ast.argumentNames.size(); i++) {
            String name = ast.argumentNames.get(i);
            String type = ast.argumentTypes.get(i);
            if(methodSymbol.parameters.containsKey(name)){
                throw new SemanticFailure(SemanticFailure.Cause.DOUBLE_DECLARATION);
            }
            methodSymbol.parameters.put(name, new VariableSymbol(name, typeManager.stringToTypeSymbol(type), VariableSymbol.Kind.PARAM));
        }
        for (Ast var : ast.decls().rwChildren())
        {
            VarDecl decl = (VarDecl) var;
            if (methodSymbol.parameters.containsKey(decl.name) || methodSymbol.locals.containsKey(decl.name)){
                throw new SemanticFailure(SemanticFailure.Cause.DOUBLE_DECLARATION);
            }
            visit(decl, wrapper);
            methodSymbol.locals.put(decl.name, decl.sym);
        }

        ast.sym = methodSymbol;
        return null;
    }

    @Override
    public Void varDecl(VarDecl ast, SymbolWrapper arg) {
        VariableSymbol variableSymbol = null;
        if (arg.symbol instanceof ClassSymbol) {
            variableSymbol = new VariableSymbol(ast.name, typeManager.stringToTypeSymbol(ast.type), VariableSymbol.Kind.FIELD);
        } else if (arg.symbol instanceof MethodSymbol) {
            variableSymbol = new VariableSymbol(ast.name, typeManager.stringToTypeSymbol(ast.type), VariableSymbol.Kind.LOCAL);
        }

        ast.sym = variableSymbol;
        return null;
    }

    // -------------- Helper Functions ---------------


}
