package cd.transform.analysis;

import cd.ir.*;
import cd.ir.Ast.Var;
import cd.ir.Ast.Expr;
import cd.ir.Symbol.VariableSymbol.Kind;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class AvailableExpressionDataFlowAnalysis extends ForwardDataFlowAnalysis<Set<Expr>> {
    private Map<BasicBlock, Set<Expr>> gen;
    private Map<BasicBlock, Set<Expr>> kill;
    private Map<Var, Set<Expr>> varExprMap;
    private Set<Expr> U;

    public AvailableExpressionDataFlowAnalysis(ControlFlowGraph cfg){
        super(cfg);
        generateSets();
        iterate();
    }

    @Override
    protected Set<Expr> initialState() {
        return null;
    }

    @Override
    protected Set<Expr> startState() {
        return null;
    }

    @Override
    protected Set<Expr> transferFunction(BasicBlock block, Set<Expr> inState) {
        return null;
    }

    @Override
    protected Set<Expr> join(Set<Set<Expr>> sets) {
        return null;
    }



    private void generateSets(){
        for (BasicBlock basicBlock : cfg.allBlocks){

            U = new HashSet<>();
            gen.put(basicBlock, new HashSet<>());
            kill.put(basicBlock, new HashSet<>());

            Visitor visitor = new Visitor();
            basicBlock.stmts.forEach(stmt -> visitor.visit(stmt, basicBlock));
            if(basicBlock.condition != null){
                visitor.visit(basicBlock.condition, basicBlock);
            }
        }
    }

    protected class Visitor extends AstVisitor<Void, BasicBlock> {
        @Override
        protected Void dfltExpr(Expr ast, BasicBlock arg) {
            U.add(ast);
            return null;
        }

        @Override
        public Void assign(Ast.Assign ast, BasicBlock arg) {
            if (ast.left() instanceof Var){
                Var var = (Var) ast.left();
                if (!var.sym.kind.equals(Kind.FIELD)){

                }
            }

            return null;
        }
    }
}
