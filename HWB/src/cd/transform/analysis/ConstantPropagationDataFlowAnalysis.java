package cd.transform.analysis;

import cd.ir.Ast;
import cd.ir.BasicBlock;
import cd.ir.ControlFlowGraph;
import cd.ir.Symbol;

import java.util.*;

public class ConstantPropagationDataFlowAnalysis extends ForwardDataFlowAnalysis<Map<String, Object>> {

    public final static Integer TOP_SYMBOL = null;


    public ConstantPropagationDataFlowAnalysis(ControlFlowGraph cfg) {
        super(cfg);
        iterate();
    }

    @Override
    protected Map<String, Object> initialState() {
        return new HashMap<>();
    }

    @Override
    protected Map<String, Object> startState() {
        return new HashMap<>();
    }

    @Override
    protected Map<String, Object> transferFunction(BasicBlock block, Map<String, Object> inState) {
        // If somethings not in the set -> bottom
        Map<String, Object> outState = new HashMap<>(inState);

        for (Ast.Stmt stmt : block.stmts) {
            if (stmt instanceof Ast.Assign) {
                Ast.Assign assign = (Ast.Assign) stmt;
                if (assign.left() instanceof Ast.Var) {//&& !((Ast.Var) assign.left()).sym.kind.equals(Symbol.VariableSymbol.Kind.FIELD)) {
                    if (assign.right() instanceof Ast.IntConst) {
                        outState.put(((Ast.Var) assign.left()).name, ((Ast.IntConst) assign.right()).value);
                    } else if (assign.right() instanceof Ast.BooleanConst) {
                        outState.put(((Ast.Var) assign.left()).name, ((Ast.BooleanConst) assign.right()).value);
                    } else if (assign.right() instanceof Ast.Var) {
                        outState.put(((Ast.Var) assign.left()).name, outState.get(((Ast.Var) assign.right()).name));
                    } else {
                        outState.put(((Ast.Var) assign.left()).name, TOP_SYMBOL);
                    }
                }
            }
        }
        return outState;
    }

    @Override
    protected Map<String, Object> join(Set<Map<String, Object>> maps) {
        Map<String, Object> outstate = new HashMap<>();
        for (Map<String, Object> map : maps) {
            for (String key : map.keySet()) {
                for (Map<String, Object> other : maps) {
                    if (other != map) {
                        if (!other.containsKey(key) || (other.containsKey(key) && other.get(key) != map.get(key))) {
                            outstate.put(key, TOP_SYMBOL);
                        } else {
                            outstate.put(key, map.get(key));
                        }
                    }
                }
            }
        }
        return outstate;
    }
}
