package cd.transform.optimizer;

import cd.ir.Ast;
import cd.ir.AstVisitor;
import cd.ir.BasicBlock;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

public class ForkOptimizer extends AstVisitor<Object, Object> {
    private Ast.MethodDecl methodDecl;

    public ForkOptimizer(Ast.MethodDecl methodDecl) {
        this.methodDecl = methodDecl;
    }

    public void optimize() {
        Set<BasicBlock> killed = new HashSet<>();
        for (BasicBlock topBlock : methodDecl.cfg.allBlocks) {
            if (topBlock.condition != null && topBlock.condition instanceof Ast.BooleanConst) {
                Boolean cond = ((Ast.BooleanConst) topBlock.condition).value;
                BasicBlock currBlock;
                Set<BasicBlock> todo = new LinkedHashSet<>();
                if (cond) { // True, kill false
                    todo.add(topBlock.falseSuccessor());
                    topBlock.successors.remove(topBlock.falseSuccessor());
                } else { // False, kill true
                    todo.add(topBlock.trueSuccessor());
                    topBlock.successors.remove(topBlock.trueSuccessor());
                }
                topBlock.condition = null;
                while (!todo.isEmpty()) {
                    currBlock = todo.iterator().next();
                    todo.remove(currBlock);
                    killed.add(currBlock);
                    for (BasicBlock succBlock : currBlock.successors) {
                        if (!killed.contains(succBlock) && !topBlock.dominanceFrontier.contains(succBlock)) {
                            todo.add(succBlock);
                        } else if (topBlock.dominanceFrontier.contains(succBlock)) {
                            succBlock.predecessors.remove(currBlock);
                        }
                    }
                }
            }
        }
        methodDecl.cfg.allBlocks.removeAll(killed);
    }
}
