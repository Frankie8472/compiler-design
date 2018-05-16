package cd.transform.analysis;

import cd.ir.BasicBlock;
import cd.ir.ControlFlowGraph;

import java.util.HashSet;
import java.util.Set;

public class ForwardFlowAnalysis extends DataFlowAnalysis<Set<String>> {

    public forwardFlowAnalysis(ControlFlowGraph cfg){
        super(cfg);
    }

    public void analyze()

    /**
     * OUT[Bi] = U (U is the set of all expressions that appear in the program
     * @return all definitions this method contains
     */
    @Override
    protected Set<String> initialState() {
        return cfg.definitionVarMap.keySet();
    }

    /**
     * Safe assumption OUT[ENTRY] = ∅
     * @return empty hash_set
     */
    @Override
    protected Set<String> startState() {
        return new HashSet<>();
    }

    /**
     * OUT(B) = gen_B ∪ (IN(B) – kill_B )
     * @param block containing it's kill and gen set
     * @param inState as List of definitions (IN(B))
     * @return OUT(B)
     */

    @Override
    protected Set<String> transferFunction(BasicBlock block, Set<String> inState) {
        Set<String> ret = inState;
        ret.removeAll(block.kill);
        ret.addAll(block.gen);
        return ret;
    }

    /**
     * Simple join function for HashSets <br>
     * A Set does not contain duplicates by nature.
     * @param sets List of definition sets
     * @return concatenation of all definition sets
     */
    @Override
    protected Set<String> join(Set<Set<String>> sets) {
        Set<String> ret = new HashSet<>();
        sets.forEach(ret::addAll);
        return ret;
    }

}
