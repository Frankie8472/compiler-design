package cd.transform.analysis;

import static java.util.Collections.unmodifiableMap;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import cd.ir.BasicBlock;
import cd.ir.ControlFlowGraph;

/**
 * The abstract superclass of all backward data-flow analyses. This class provides a framework to
 * implement concrete analyses by providing {@link #initialState()},
 * {@link #endState()}, {@link #transferFunction(BasicBlock, Object)}, and
 * {@link #join(Set)} methods.
 *
 * @param <State> The type of states the analysis computes, specified by a concrete subclass.
 *                Typically, this is a set or map type.
 */
public abstract class BackwardDataFlowAnalysis<State> {

    protected final ControlFlowGraph cfg;
    private Map<BasicBlock, State> inStates;
    private Map<BasicBlock, State> outStates;

    public BackwardDataFlowAnalysis(ControlFlowGraph cfg) {
        this.cfg = cfg;
    }

    /**
     * Returns the in-state of basic block <code>block</code>.
     */
    public State inStateOf(BasicBlock block) {
        return inStates.get(block);
    }

    /**
     * Returns the out-state of basic block <code>block</code>.
     */
    public State outStateOf(BasicBlock block) {
        return outStates.get(block);
    }

    /**
     * Do backward flow fixed-point iteration until out-states do not change anymore.
     * Subclasses should call this method in their constructor after the required
     * initialization.
     */
    protected void iterate() {
        inStates = new HashMap<>();
        outStates = new HashMap<>();
        for (BasicBlock block : cfg.allBlocks)
            outStates.put(block, initialState());

        Set<BasicBlock> todo = new HashSet<>();
        todo.addAll(cfg.allBlocks);
        while (!todo.isEmpty()) {
            BasicBlock block = todo.iterator().next();
            todo.remove(block);

            /* calculate in-state */
            State outState;
            if (block == cfg.end)
                outState = endState();
            else {
                Set<State> predInStates = new HashSet<>();
                for (BasicBlock pred : block.successors)
                    predInStates.add(inStates.get(pred));
                outState = join(predInStates);
            }
            outStates.put(block, outState);

            State newInState = transferFunction(block, outState);

            /* if out-state changed, recalculate successors */
            if (!newInState.equals(inStates.get(block))) {
                inStates.put(block, newInState);
                todo.addAll(block.predecessors);
            }
        }
        inStates = unmodifiableMap(inStates);
    }

    /**
     * Returns the initial state for all blocks except the {@link ControlFlowGraph#end end}
     * block.
     */
    protected abstract State initialState();

    /**
     * Returns the initial state for the {@link ControlFlowGraph#end end} block.
     */
    protected abstract State endState();

    /**
     * Calculates the in-state for a basic block <code>block</code> and an out-state
     * <code>outState</code>
     */
    protected abstract State transferFunction(BasicBlock block, State outState);

    /**
     * Merges together several in-states and returns the out-state for the transfer function.
     */
    protected abstract State join(Set<State> states);
}
