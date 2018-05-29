package cd.transform.optimizer;

import cd.ir.*;
import cd.transform.analysis.ConstantPropagationDataFlowAnalysis;

import java.util.HashMap;
import java.util.Map;

public class ConstantPropagationOptimizer extends BaseOptimizer<Map<String, Object>> {

    private Ast.MethodDecl methodDecl;
    private ConstantPropagationDataFlowAnalysis analysis;

    public ConstantPropagationOptimizer(Ast.MethodDecl methodDecl) {
        this.methodDecl = methodDecl;
        this.analysis = new ConstantPropagationDataFlowAnalysis(methodDecl);
    }

    public void optimize() {
        for (BasicBlock block : methodDecl.cfg.allBlocks) {
            Map<String, Object> currState = new HashMap<>(analysis.inStateOf(block));
            for (Ast.Stmt stmt : block.stmts) {
                visit(stmt, currState);
                System.out.println(currState);
            }
            if (block.condition != null) {
                visit(block.condition, currState);
            }
        }
    }

    @Override
    public Ast var(Ast.Var ast, Map<String, Object> arg) {
        System.out.println("Hallo du");
        if(arg.get(ast.name) != null){
            Object value = arg.get(ast.name);
            if(value instanceof Integer){
                return createNewIntConst((Integer) value);
            } else if(value instanceof Boolean){
                return createNewBoolConst((Boolean) value);
            }
        }
        return null;
    }

    @Override
    public Ast assign(Ast.Assign ast, Map<String, Object> arg) {
        // Update var in currState if redefined
        Ast result = visit(ast.right(), arg);
        if(result != null){
            ast.setRight((Ast.Expr) result);
        }
        if(ast.left() instanceof Ast.Var){
            if (ast.right() instanceof Ast.IntConst) {
                arg.put(((Ast.Var) ast.left()).name, ((Ast.IntConst) ast.right()).value);
            } else if (ast.right() instanceof Ast.BooleanConst) {
                arg.put(((Ast.Var) ast.left()).name, ((Ast.BooleanConst) ast.right()).value);
            }else if(ast.right() instanceof Ast.Var){
//                System.out.println("hallo");
                arg.put(((Ast.Var) ast.left()).name, arg.get(((Ast.Var) ast.right()).name));
            } else {
                arg.put(((Ast.Var) ast.left()).name, ConstantPropagationDataFlowAnalysis.TOP_SYMBOL);
            }
        }

        return null;
    }
}
