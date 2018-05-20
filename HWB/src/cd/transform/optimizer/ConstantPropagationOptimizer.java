package cd.transform.optimizer;

import cd.ir.*;
import cd.transform.analysis.ConstantPropagationDataFlowAnalysis;

import java.util.HashMap;
import java.util.Map;

public class ConstantPropagationOptimizer extends AstVisitor<Void, Map<String, Object>> {

    private Ast.MethodDecl methodDecl;
    private ConstantPropagationDataFlowAnalysis analysis;

    public ConstantPropagationOptimizer(Ast.MethodDecl methodDecl) {
        this.methodDecl = methodDecl;
        this.analysis = new ConstantPropagationDataFlowAnalysis(methodDecl.cfg);
    }

    public void optimize() {

        for (BasicBlock block : methodDecl.cfg.allBlocks) {
            Map<String, Object> currState = new HashMap<>(analysis.inStateOf(block));
            for (Ast.Stmt stmt : block.stmts) {
                visit(stmt, currState);
            }

            if (block.condition != null) {
                visit(block.condition, currState);
            }
        }
    }

    @Override
    protected Void dflt(Ast ast, Map<String, Object> arg) {
        for (int i = 0; i < ast.rwChildren.size(); i++) {
            Ast child = ast.rwChildren.get(i);
            if (child instanceof Ast.Var) {
                Ast.Var var = (Ast.Var) child;
                if (arg.get(var.name) != null) {
                    if (arg.get(var.name) instanceof Ast.IntConst){
                        ast.rwChildren.set(i, new Ast.IntConst(((Ast.IntConst) arg.get(var.name)).value));
                    } else if (arg.get(var.name) instanceof Ast.BooleanConst){
                        ast.rwChildren.set(i, new Ast.BooleanConst(((Ast.BooleanConst) arg.get(var.name)).value));
                    }
                }
            }
        }
        visitChildren(ast, arg);
        return null;
    }

    @Override
    public Void assign(Ast.Assign ast, Map<String, Object> arg) {
        visit(ast.right(), arg);

        // Update var in currState if redefined
        if (ast.left() instanceof Ast.Var) {
            Ast.Var left = (Ast.Var) ast.left();
            if (!left.sym.kind.equals(Symbol.VariableSymbol.Kind.FIELD)) {
                if (ast.right() instanceof Ast.IntConst){
                    arg.put(left.sym.name, ((Ast.IntConst) ast.right()).value);
                } else if (ast.right() instanceof Ast.BooleanConst) {
                    arg.put(left.sym.name, ((Ast.BooleanConst) ast.right()).value);
                } else {
                    arg.put(left.sym.name, ConstantPropagationDataFlowAnalysis.TOP_SYMBOL);
                }
            }
        }
        return null;
    }
}
