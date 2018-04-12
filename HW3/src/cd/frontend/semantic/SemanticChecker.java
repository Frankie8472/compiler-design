package cd.frontend.semantic;

import cd.ir.Ast;
import cd.ir.AstVisitor;
import cd.ir.Symbol;

import java.util.List;

public class SemanticChecker extends AstVisitor<Void, SymbolWrapper> {


    public void check(List<Ast.ClassDecl> classDecls) throws SemanticFailure {

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
        ast.type = Symbol.PrimitiveTypeSymbol.booleanType;
        //todo: do we have to check if true or false is wrong written?
        return null;
    }

    @Override
    public Void intConst(Ast.IntConst ast, SymbolWrapper arg) {
        ast.type = Symbol.PrimitiveTypeSymbol.intType;
        //todo: do we have to check if it actually is an integer?!
        return null;
    }

    @Override
    public Void nullConst(Ast.NullConst ast, SymbolWrapper arg) {
        ast.type = Symbol.ClassSymbol.nullType; // todo: is this correct?
        return null;
    }

    @Override
    public Void builtInRead(Ast.BuiltInRead ast, SymbolWrapper arg) {
        ast.type = Symbol.PrimitiveTypeSymbol.intType;
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

}
