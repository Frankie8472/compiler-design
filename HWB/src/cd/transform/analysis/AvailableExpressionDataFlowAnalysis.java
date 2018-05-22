package cd.transform.analysis;

import cd.ir.*;
import cd.ir.Ast.Expr;
import cd.ir.Ast.Var;
import cd.ir.Symbol.VariableSymbol.Kind;

import java.util.*;

public class AvailableExpressionDataFlowAnalysis extends ForwardDataFlowAnalysis<Set<Expr>> {
    private Map<BasicBlock, Set<Expr>> gen;
    private Map<BasicBlock, Set<Expr>> kill;
    public Map<Var, Set<Expr>> varExprMap;
    public Map<Expr, Ast.Stmt> exprStmtMap;
    private Set<Expr> allExpr; // U in the slides

    // if tree/subtree the same in outstate, merge! but only then

    public AvailableExpressionDataFlowAnalysis(ControlFlowGraph cfg) {
        super(cfg);
        generateSets();
        iterate();
    }

    @Override
    protected Set<Expr> initialState() {
        return allExpr;
    }

    @Override
    protected Set<Expr> startState() {
        return new LinkedHashSet<>();
    }

    @Override
    protected Set<Expr> transferFunction(BasicBlock block, Set<Expr> inState) {
        Set<Expr> out = inState;
        out.removeAll(kill.get(block));
        out.addAll(gen.get(block));
        return out;
    }

    @Override
    protected Set<Expr> join(Set<Set<Expr>> sets) {
        Set<Expr> in = sets.iterator().next();
        sets.forEach(in::retainAll);
        return in;
    }


    private void generateSets() {
        // Initialize all maps and sets
        allExpr = new LinkedHashSet<>();
        gen = new HashMap<>();
        kill = new HashMap<>();
        varExprMap = new HashMap<>();
        exprStmtMap = new HashMap<>();

        // Generate allExpr set
        for (BasicBlock basicBlock : cfg.allBlocks) {
            ExprVisitor exprVisitor = new ExprVisitor();
            basicBlock.stmts.forEach(stmt -> exprVisitor.visit(stmt, stmt));
            if (basicBlock.condition != null) {
                exprVisitor.visit(basicBlock.condition, null);
            }
        }

        // Generate varExprMap
        VarVisitor varVisitor = new VarVisitor();
        allExpr.forEach(expr -> varVisitor.visit(expr, expr));

        // Generate gen and kill set
        for (BasicBlock basicBlock : cfg.allBlocks) {
            // Initialize gen and kill set
            gen.put(basicBlock, new HashSet<>());
            kill.put(basicBlock, new HashSet<>());
            AssVisitor assVisitor = new AssVisitor();
            basicBlock.stmts.forEach(stmt -> assVisitor.visit(stmt, basicBlock));
            if (basicBlock.condition != null) {
                assVisitor.visit(basicBlock.condition, basicBlock);
            }
        }
    }

    protected class ExprVisitor extends AstVisitor<Void, Ast.Stmt> {
        @Override
        protected Void dfltExpr(Expr ast, Ast.Stmt arg) {
            allExpr.add(ast);
            exprStmtMap.put(ast, arg);

            return null;
        }
    }

    protected class VarVisitor extends AstVisitor<Void, Expr> {
        @Override
        public Void var(Ast.Var ast, Expr arg) {
            if(!ast.sym.kind.equals(Kind.FIELD)) {
                if(!varExprMap.containsKey(ast)){
                    varExprMap.put(ast, new HashSet<>());
                }
                varExprMap.get(ast).add(arg);
            }
            return null;
        }
    }

    protected class AssVisitor extends AstVisitor<Void, BasicBlock> {
        @Override
        public Void assign(Ast.Assign ast, BasicBlock arg) {
            visit(ast.right(), arg);
            if (ast.left() instanceof Var) {
                Var var = (Var) ast.left();
                if (!var.sym.kind.equals(Kind.FIELD)){
                    gen.get(arg).removeAll(varExprMap.get(var));
                    kill.get(arg).addAll(varExprMap.get(var));
                }
            }
            return null;
        }

        @Override
        protected Void dfltExpr(Expr ast, BasicBlock arg) {
            gen.get(arg).add(ast);
            return null;
        }
    }

}