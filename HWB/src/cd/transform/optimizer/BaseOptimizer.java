package cd.transform.optimizer;

import cd.ir.Ast;
import cd.ir.AstVisitor;
import cd.ir.Symbol;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Map;

public class BaseOptimizer<V> extends AstVisitor<Ast, V> {


    @Override
    protected Ast dflt(Ast ast, V arg) {
        for (int i = 0; i < ast.rwChildren.size(); i++) {
            Ast child = ast.rwChildren.get(i);
            if (child != null) {
                try {
                    Ast returnValue = visit(child, arg);
                    if (returnValue != null) {
                        ast.rwChildren.set(i, returnValue);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    protected Ast.IntConst createNewIntConst(int value){
        Ast.IntConst newConst = new Ast.IntConst(value);
        newConst.type = Symbol.PrimitiveTypeSymbol.intType;
        return newConst;
    }

    protected Ast.BooleanConst createNewBoolConst(boolean value){
        Ast.BooleanConst newConst = new Ast.BooleanConst(value);
        newConst.type = Symbol.PrimitiveTypeSymbol.booleanType;
        return newConst;
    }

    protected Ast.UnaryOp createNewUnaryOp(Ast.UnaryOp.UOp uop, Ast.Expr expr){
        Ast.UnaryOp unaryOp = new Ast.UnaryOp(uop, expr);
        unaryOp.type = expr.type;
        return unaryOp;
    }

    protected Ast.NullConst createNewNullConst(){
        Ast.NullConst nullConst = new Ast.NullConst();
        nullConst.type = Symbol.ClassSymbol.nullType;
        return nullConst;
    }
}
