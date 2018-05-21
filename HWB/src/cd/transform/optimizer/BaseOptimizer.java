package cd.transform.optimizer;

import cd.ir.Ast;
import cd.ir.AstVisitor;

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
}
