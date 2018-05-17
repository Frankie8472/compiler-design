package cd.ir;

import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

public class DominatorTreeAlgorithm {
    private ControlFlowGraph cfg;

    public DominatorTreeAlgorithm(ControlFlowGraph cfg) {
        this.cfg = cfg;
    }

    public void build(){
        buildDominators();
        buildDominatorTree();
        buildDominatorFrontier();
    }

    private void buildDominators() {
        cfg.start.dominators.add(cfg.start);
        List<BasicBlock> allBlocksWithoutStartBlock = cfg.allBlocks.subList(1, cfg.count());

        for (BasicBlock basicBlock : allBlocksWithoutStartBlock) {
            basicBlock.dominators.addAll(cfg.allBlocks);
        }

        Boolean changesInBlock = true;

        while (changesInBlock) {
            for (BasicBlock basicBlock : allBlocksWithoutStartBlock) {
                List<BasicBlock> oldDominatorList = basicBlock.dominators;
                List<BasicBlock> newDominatorList = new ArrayList<>();

                newDominatorList.addAll(basicBlock.predecessors.get(0).dominators);

                if (basicBlock.predecessors.size() == 2){
                    newDominatorList.retainAll(basicBlock.predecessors.get(1).dominators);
                }

                if (!newDominatorList.contains(basicBlock)){
                    newDominatorList.add(basicBlock);
                }

                if (oldDominatorList.containsAll(newDominatorList) && newDominatorList.containsAll(oldDominatorList)) {
                    changesInBlock = false;
                } else {
                    changesInBlock = true;
                    basicBlock.dominators = newDominatorList;
                }
            }
        }
    }

    private void buildDominatorTree() {
        for (BasicBlock basicBlock : cfg.allBlocks) {
            searchParent(basicBlock);

        }
    }

    private void searchParent(BasicBlock basicBlock){
        List<BasicBlock> todo = new LinkedList<>();
        todo.addAll(basicBlock.predecessors);
        while(!todo.isEmpty()) {
            BasicBlock currentBlock = todo.iterator().next();
            todo.remove(currentBlock);
            if (basicBlock.dominators.contains(currentBlock)){
                basicBlock.dominatorTreeParent = currentBlock;
                currentBlock.dominatorTreeChildren.add(basicBlock);
                todo.clear();
            } else {
                todo.addAll(currentBlock.predecessors);
            }
        }
    }

    private void buildDominatorFrontier(){
        for (BasicBlock basicBlock : cfg.allBlocks){
            if (basicBlock.predecessors.size() >= 2) {
                for (BasicBlock predecessorBlock : basicBlock.predecessors) {
                    BasicBlock runner = predecessorBlock;
                    while(!runner.equals(basicBlock.dominatorTreeParent)){
                        runner.dominanceFrontier.add(basicBlock);
                        runner = runner.dominatorTreeParent;
                    }
                }
            }
        }
    }
}