package cd.frontend.semantic;

import java.util.ArrayList;
import java.util.List;

import cd.Main;
import cd.ir.Ast;
import cd.ir.Ast.MethodDecl;
import cd.ir.Ast.VarDecl;
import cd.ir.Ast.ClassDecl;
import cd.ir.AstVisitor;
import cd.ir.Symbol.MethodSymbol;
import cd.ir.Symbol.ClassSymbol;
import cd.ir.Symbol.VariableSymbol;

public class SemanticAnalyzer extends AstVisitor<Void, CurrentContext> {

    public final Main main;

    private TypeManager typeManager;

    public SemanticAnalyzer(Main main) {
        this.main = main;
        this.typeManager = new TypeManager();
    }

    public void check(List<ClassDecl> classDecls) throws SemanticFailure {

        // Transform classes to symbols
        for (ClassDecl decl : classDecls) {
//            if(decl.name.equals("Object")){
//                throw new SemanticFailure(SemanticFailure.Cause.OBJECT_CLASS_DEFINED);
//            }  TODO: Done in type manager
            ClassSymbol classSymbol = new ClassSymbol(decl);
            typeManager.addType(classSymbol);
            //todo: chunnt double declaration dur de parser dure?
            //TODO: Answer: JA!
            decl.sym = classSymbol;
        }

        // Fill out superclass
        for (ClassDecl decl : classDecls) {
            decl.sym.superClass = (ClassSymbol) typeManager.stringToTypeSymbol(decl.superClass);
        }

        // Check for circular inheritance
        for (ClassSymbol currentSymbol : typeManager.getTypes()) {
            List<ClassSymbol> foundClasses = new ArrayList<>();

            while (currentSymbol.superClass != ClassSymbol.objectType) {
                if (foundClasses.contains(currentSymbol)) {
                    throw new SemanticFailure(SemanticFailure.Cause.CIRCULAR_INHERITANCE);
                }

                foundClasses.add(currentSymbol);
                currentSymbol = currentSymbol.superClass;

            }
        }

        for (ClassDecl decl : classDecls) {
            visit(decl, null);
        }

        SemanticChecker checker = new SemanticChecker(typeManager);
        checker.check(classDecls);
    }

    @Override
    public Void classDecl(ClassDecl ast, CurrentContext arg) {
        CurrentContext context = new CurrentContext(ast.sym);

        for (VarDecl varDecl : ast.fields()) {
            if (ast.sym.fields.containsKey(varDecl.name)) {
                throw new SemanticFailure(SemanticFailure.Cause.DOUBLE_DECLARATION);
            }
            visit(varDecl, context);
            ast.sym.fields.put(varDecl.name, varDecl.sym);
        }

        for (MethodDecl methodDecl : ast.methods()) {
            if (ast.sym.methods.containsKey(methodDecl.name)) {
                throw new SemanticFailure(SemanticFailure.Cause.DOUBLE_DECLARATION);
            }
            visit(methodDecl, context);
            ast.sym.methods.put(methodDecl.name, methodDecl.sym);
        }

        return null;
    }

    @Override
    public Void methodDecl(MethodDecl ast, CurrentContext arg) {
        /* namespace_0: class
         * namespace_1: field
         * namespace_2: method
         * namespace_3: parameter and local
         * shadowing is active with prio 3 to 0 and then this to all following superclasses
         */
        MethodSymbol methodSymbol = new MethodSymbol(ast);
        CurrentContext context = new CurrentContext(arg, methodSymbol);

        methodSymbol.returnType = typeManager.stringToTypeSymbol(ast.returnType);

        for (int i = 0; i < ast.argumentNames.size(); i++) {
            String name = ast.argumentNames.get(i);
            String type = ast.argumentTypes.get(i);
            for(VariableSymbol sym : methodSymbol.parameters){
                if(sym.name.equals(name)){
                    throw new SemanticFailure(SemanticFailure.Cause.DOUBLE_DECLARATION);
                }
            }
            methodSymbol.parameters.add(new VariableSymbol(name, typeManager.stringToTypeSymbol(type), VariableSymbol.Kind.PARAM));
        }

        for (Ast var : ast.decls().children()) {
            VarDecl decl = (VarDecl) var;

            if (methodSymbol.locals.containsKey(decl.name)){
                throw new SemanticFailure(SemanticFailure.Cause.DOUBLE_DECLARATION);
            }

            for(VariableSymbol sym : methodSymbol.parameters){
                if(sym.name.equals(decl.name)){
                    throw new SemanticFailure(SemanticFailure.Cause.DOUBLE_DECLARATION);
                }
            }

            visit(decl, context);
            methodSymbol.locals.put(decl.name, decl.sym);
        }

        ClassSymbol current = arg.getClassSymbol();
        while(current != ClassSymbol.objectType){
            if (current.methods.containsKey(ast.name) && (
                    (current.methods.get(ast.name).returnType != methodSymbol.returnType) ||
                    (current.methods.get(ast.name).parameters.equals(methodSymbol.parameters)))){
                throw new SemanticFailure(SemanticFailure.Cause.INVALID_OVERRIDE);
            }
            current = current.superClass;
        }

        ast.sym = methodSymbol;
        return null;
    }

    @Override
    public Void varDecl(VarDecl ast, CurrentContext arg) {
        VariableSymbol variableSymbol;
        if (arg.getMethodSymbol() == null) {
            variableSymbol = new VariableSymbol(ast.name, typeManager.stringToTypeSymbol(ast.type), VariableSymbol.Kind.FIELD);
        } else {
            variableSymbol = new VariableSymbol(ast.name, typeManager.stringToTypeSymbol(ast.type), VariableSymbol.Kind.LOCAL);
        }

        ast.sym = variableSymbol;
        return null;
    }

}
