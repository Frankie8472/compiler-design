package cd.transform.analysis;

import cd.ir.Ast.Stmt;
import cd.ir.Ast.Assign;
import cd.ir.Ast.Var;
import cd.ir.AstVisitor;
import cd.ir.BasicBlock;
import cd.ir.ControlFlowGraph;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import cd.ir.Symbol.VariableSymbol.Kind;


public class LiveVariableAnalysis extends BackwardDataFlowAnalysis<Set<String>> {
    private Map<BasicBlock, Set<String>> def = new HashMap<>();
    private Map<BasicBlock, Set<String>> use = new HashMap<>();
    private Map<Var, Set<Stmt>> varDefMap = new HashMap<>();

    public LiveVariableAnalysis(ControlFlowGraph cfg) {
        super(cfg);
        generateDefUse();
        iterate();
    }

    @Override
    protected Set<String> initialState() {
        return new HashSet<>();
    }

    @Override
    protected Set<String> endState() {
        return new HashSet<>();
    }

    @Override
    protected Set<String> transferFunction(BasicBlock block, Set<String> outState) {
        Set<String> in = new HashSet<>(outState);
        in.removeAll(def.get(block));
        in.addAll(use.get(block));
        return in;
    }

    @Override
    protected Set<String> join(Set<Set<String>> objects) {
        Set<String> out = new HashSet<>();
        objects.forEach(vars -> {
            if (vars != null) {
                out.addAll(vars);
            }
        });
        return out;
    }

    private void generateDefUse() {
        for (BasicBlock basicBlock : cfg.allBlocks) {
            use.put(basicBlock, new HashSet<>());
            def.put(basicBlock, new HashSet<>());

            Visitor visitor = new Visitor();
            for (Stmt stmt : basicBlock.stmts) {
                visitor.visit(stmt, basicBlock);
            }
            if (basicBlock.condition != null) {
                visitor.visit(basicBlock.condition, basicBlock);
            }
        }
    }

    private void addToUse(BasicBlock block, Var var) {
        if (!def.get(block).contains(var.name)) {
            use.get(block).add(var.name);
        }
    }

    private void addToDef(BasicBlock block, Var var) {
        if (!use.get(block).contains(var.name)) {
            def.get(block).add(var.name);
        }
    }

    // Visitor for detecting all var's in an expr
    protected class Visitor extends AstVisitor<Void, BasicBlock> {
        @Override
        public Void assign(Assign ast, BasicBlock arg) {
            visit(ast.right(), arg);
            if (ast.left() instanceof Var) {
                Var left = (Var) ast.left();
                if (!left.sym.kind.equals(Kind.FIELD)) {
                    addToDef(arg, left);
                }
            } else {
                visit(ast.left(), arg);
            }
            return null;
        }

        @Override
        public Void var(Var ast, BasicBlock arg) {
            if (!ast.sym.kind.equals(Kind.FIELD)) {
                addToUse(arg, ast);
            }
            return null;
        }
    }
}
