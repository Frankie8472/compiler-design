package cd.transform.analysis;

import cd.ir.Ast;
import cd.ir.BasicBlock;
import cd.ir.ControlFlowGraph;

import java.util.*;

public class ConstantPropagationDataFlowAnalysis extends ForwardDataFlowAnalysis<Map<String, Integer>> {

    public final static Integer TOP_SYMBOL = null;


    public ConstantPropagationDataFlowAnalysis(ControlFlowGraph cfg) {
        super(cfg);
        iterate();
    }

    @Override
    protected Map<String, Integer> initialState() {
        return new HashMap<>();
    }

    @Override
    protected Map<String, Integer> startState() {
        return new HashMap<>();
    }

    @Override
    protected Map<String, Integer> transferFunction(BasicBlock block, Map<String, Integer> inState) {
        Map<String, Integer> outState = new HashMap<>(inState);

        for (Ast.Stmt stmt : block.stmts) {
            if (stmt instanceof Ast.Assign) {
                Ast.Assign assign = (Ast.Assign) stmt;
                if (assign.left() instanceof Ast.Var) {
                    if (assign.right() instanceof Ast.IntConst) {
                        outState.put(((Ast.Var) assign.left()).name, ((Ast.IntConst) assign.right()).value);
                    } else {
                        outState.put(((Ast.Var) assign.left()).name, TOP_SYMBOL);
                    }
                }
            }
        }


        return outState;
    }

    @Override
    protected Map<String, Integer> join(Set<Map<String, Integer>> maps) {
        Map<String, Integer> outstate = new HashMap<>();
        for (Map<String, Integer> map : maps) {
            for (String key : map.keySet()) {
                if (outstate.containsKey(key) && !Objects.equals(outstate.get(key), map.get(key))) {
                    outstate.put(key, TOP_SYMBOL);
                } else {
                    outstate.put(key, map.get(key));
                }
            }
        }

        return outstate;
    }
}
