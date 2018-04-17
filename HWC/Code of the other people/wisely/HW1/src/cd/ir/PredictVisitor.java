package cd.ir;

public class PredictVisitor extends ExprVisitor<Integer, Void> {

    protected Integer dfltExpr(Ast.Expr ast, Void arg) {
        return 1;
    }

    public Integer binaryOp(Ast.BinaryOp ast, Void arg) {
        int n_l = visit(ast.left(), arg);
        int n_r = visit(ast.right(), arg);

        if (n_l == n_r) {
            return n_l+1;
        } else
            return Math.max(n_l, n_r);
    }

    public Integer unaryOp(Ast.UnaryOp ast, Void arg) {
        return visit(ast.arg(), arg);
    }

}
