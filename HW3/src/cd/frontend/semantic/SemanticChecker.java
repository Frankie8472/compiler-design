package cd.frontend.semantic;

import cd.ir.Ast;
import cd.ir.AstVisitor;
import cd.ir.Symbol;

import java.util.List;

public class SemanticChecker extends AstVisitor<Void, Void> {


    public void check(List<Ast.ClassDecl> classDecls) throws SemanticFailure {
        // Global -> all classes
        // Class wide -> fields, methods
        // Method wide -> params, locals

//        SymbolTable table = new SymbolTable();
//
//        for (Ast.ClassDecl decl : classDecls) {
//            table.globalContext.put(decl.name, decl.sym);
//        }
//
//        table.globalContext.forEach((s, classSymbol) -> {
//            Symbol.ClassSymbol currentSymbol = classSymbol;
//
//            while (classSymbol.superClass != Symbol.ClassSymbol.objectType) {
//                if (!table.globalContext.keySet().contains(classSymbol.superClass.name)) {
//                    throw  new SemanticFailure(SemanticFailure.Cause.NO_SUCH_TYPE);
//                }
//
//
//                currentSymbol = classSymbol.superClass;
//                //TODO: Log found classes
//                if (currentSymbol.name.equals(classSymbol.name)) {
//                    throw new SemanticFailure(SemanticFailure.Cause.CIRCULAR_INHERITANCE);
//                }
//            }
//
//        });


    }
}
