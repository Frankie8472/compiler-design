package cd.transform.optimizer;

import cd.ir.*;
import cd.transform.analysis.ConstantPropagationDataFlowAnalysis;

import java.util.HashMap;
import java.util.Map;

public class ConstantPropagationOptimizer extends AstVisitor<Void, Map<String, Integer>> {

    private Ast.MethodDecl methodDecl;
    private ConstantPropagationDataFlowAnalysis analysis;

    public ConstantPropagationOptimizer(Ast.MethodDecl methodDecl) {
        this.methodDecl = methodDecl;
        this.analysis = new ConstantPropagationDataFlowAnalysis(methodDecl.cfg);
    }

    public void optimize() {

        for (BasicBlock block : methodDecl.cfg.allBlocks) {
            Map<String, Integer> currState = new HashMap<>(analysis.inStateOf(block));
            for (Ast.Stmt stmt : block.stmts) {
                visit(stmt, currState);
                if (stmt instanceof Ast.Assign) {
                    Ast.Assign assign = (Ast.Assign) stmt;
                    if (assign.left() instanceof Ast.Var) {
                        if (assign.right() instanceof Ast.IntConst) {
                            currState.put(((Ast.Var) assign.left()).name, ((Ast.IntConst) assign.right()).value);
                        } else {
                            currState.put(((Ast.Var) assign.left()).name, ConstantPropagationDataFlowAnalysis.TOP_SYMBOL);
                        }
                    }
                }
            }
            if (block.condition != null) {
                visit(block.condition, currState);
            }
        }
    }

    @Override
    public Void dfltExpr(Ast.Expr ast, Map<String, Integer> arg) {
        return replaceVarWithIntConst(ast, arg);

    }

    @Override
    public Void dfltStmt(Ast.Stmt ast, Map<String, Integer> arg) {
            return replaceVarWithIntConst(ast, arg);
    }

    @Override
    public Void assign(Ast.Assign ast, Map<String, Integer> arg) {
        if(ast.right() instanceof Ast.Var){
            Ast.Var var = (Ast.Var) ast.right();
            if(arg.get(var.name) != null) {
                ast.setRight(new Ast.IntConst(arg.get(var.name)));
            }
        }
        return visitChildren(ast, arg);
    }

    private Void replaceVarWithIntConst(Ast ast, Map<String, Integer> arg) {
        for (int i = 0; i < ast.rwChildren.size(); i++) {
            Ast child = ast.rwChildren.get(i);
            if (child instanceof Ast.Var) {
                Ast.Var var = (Ast.Var) child;
                if (arg.get(var.name) != null) {
                    ast.rwChildren.set(i, new Ast.IntConst(arg.get(var.name)));
                }
            }
        }
        return visitChildren(ast, arg);
    }
}
