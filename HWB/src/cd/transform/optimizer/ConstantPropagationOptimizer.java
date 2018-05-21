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
    public Ast var(Ast.Var ast, Map<String, Object> arg) {
        if(arg.get(ast.name) != null){
            Object value = arg.get(ast.name);
            if(value instanceof Integer){
                return new Ast.IntConst((Integer) value);
            } else if(value instanceof Boolean){
                return new Ast.BooleanConst((Boolean) value);
            }
        }
        return null;
    }

    //        public Void assign(Ast.Assign ast, Map<String, Object> arg) {
//            if (ast.right() instanceof Ast.Var) {
//                Ast.Var var = (Ast.Var) ast.right();
//                Object varConstant = arg.get(var.name);
//                if (varConstant != null) {
//                    if(varConstant instanceof Integer){
//                        ast.setRight(new Ast.IntConst((Integer)varConstant));
//                    } else if (varConstant instanceof Boolean){
////                        ast.setRight(new Ast.BooleanConst((Boolean)varConstant));
//                    }
//                }
//            }
//            return visitChildren(ast, arg);
//        }

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
                arg.put(((Ast.Var) ast.left()).name, arg.get(((Ast.Var) ast.right()).name));
            } else {
                arg.put(((Ast.Var) ast.left()).name, ConstantPropagationDataFlowAnalysis.TOP_SYMBOL);
            }
        }

        return null;
    }
}
