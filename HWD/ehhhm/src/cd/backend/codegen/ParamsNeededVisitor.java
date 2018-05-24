package cd.backend.codegen;

import cd.Config;
import cd.ir.Ast;
import cd.ir.AstVisitor;

public class ParamsNeededVisitor extends AstVisitor<Integer, Void> {

    @Override
    public Integer methodCall(Ast.MethodCallExpr ast, Void arg) {
        return ast.allArguments().size();
    }

    @Override
    public Integer visitChildren(Ast ast, Void arg) {
        int lastValue = 0;
        for (Ast child : ast.children()){
            lastValue = Math.max(this.visit(child, arg), lastValue);
        }
        return lastValue;
    }
}
