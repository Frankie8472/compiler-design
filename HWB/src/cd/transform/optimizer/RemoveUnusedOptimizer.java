package cd.transform.optimizer;

import cd.ir.*;
import cd.transform.analysis.ConstantPropagationDataFlowAnalysis;
import cd.transform.analysis.LiveVariableAnalysis;

import java.util.Set;

public class RemoveUnusedOptimizer extends AstVisitor<Void, Set<String>> {

    private Ast.MethodDecl methodDecl;
    private LiveVariableAnalysis analysis;

    public RemoveUnusedOptimizer(Ast.MethodDecl methodDecl) {
        this.methodDecl = methodDecl;
        this.analysis = new LiveVariableAnalysis(methodDecl.cfg);
    }

    public void optimize() {
        for (BasicBlock block : methodDecl.cfg.allBlocks) {
            Set<String> currState = analysis.outStateOf(block);
            if (block.condition != null) {
                visit(block.condition, currState);
            }
            for (int i = block.stmts.size() - 1; i >= 0; i--) {
                Ast.Stmt stmt = block.stmts.get(i);
                if (stmt instanceof Ast.Assign) {
                    Ast.Assign assign = (Ast.Assign) stmt;
                    if (assign.left() instanceof Ast.Var && !currState.contains(((Ast.Var) assign.left()).name) && (new CheckVisitor().visit(assign.right(), null))) {
                        block.stmts.set(i, new Ast.Nop());
                    } else {
                        visit(assign, currState);
                    }
                } else {
                    visit(stmt, currState);
                }
            }

        }
    }


//    @Override
//    public Void assign(Ast.Assign ast, Set<String> arg) {
//        visit(ast.right(), arg);
//        if (ast.left() instanceof Ast.Var) {
//            Ast.Var left = (Ast.Var) ast.left();
//            if (!left.sym.kind.equals(Symbol.VariableSymbol.Kind.FIELD)) {
//                arg.add(left.name);
//            }
//        }
//        return null;
//    }

    @Override
    public Void var(Ast.Var ast, Set<String> arg) {
        if (!ast.sym.kind.equals(Symbol.VariableSymbol.Kind.FIELD)) {
            arg.add(ast.name);
        }
        return null;
    }

    private class CheckVisitor extends ExprVisitor<Boolean, Void> {
        @Override
        protected Boolean dfltExpr(Ast.Expr ast, Void arg) {
            boolean result = true;
            for (int i = 0; i < ast.rwChildren.size(); i++) {
                result = result && visit((Ast.Expr) ast.rwChildren.get(i), arg);
            }
            return result;
        }

        @Override
        public Boolean methodCall(Ast.MethodCallExpr ast, Void arg) {
            return false;
        }

        @Override
        public Boolean builtInRead(Ast.BuiltInRead ast, Void arg) {
            return false;
        }

        @Override
        public Boolean cast(Ast.Cast ast, Void arg) {
            return false;
        }

        @Override
        public Boolean newArray(Ast.NewArray ast, Void arg) {
            return false;
        }

        @Override
        public Boolean newObject(Ast.NewObject ast, Void arg) {
            return false;
        }

        @Override
        public Boolean index(Ast.Index ast, Void arg) {
            return false;
        }

        @Override
        public Boolean field(Ast.Field ast, Void arg) {
            return false;
        }

        @Override
        public Boolean binaryOp(Ast.BinaryOp ast, Void arg) {
            if(ast.operator == Ast.BinaryOp.BOp.B_DIV || ast.operator == Ast.BinaryOp.BOp.B_MOD){
                return ast.right() instanceof Ast.IntConst && ((Ast.IntConst) ast.right()).value != 0;
            }
            return dfltExpr(ast, arg);
        }
    }
}
