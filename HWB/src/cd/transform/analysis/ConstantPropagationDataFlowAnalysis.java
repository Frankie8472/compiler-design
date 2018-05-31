package cd.transform.analysis;

import cd.ir.Ast;
import cd.ir.BasicBlock;
import cd.ir.ControlFlowGraph;
import cd.ir.Symbol;

import javax.lang.model.type.PrimitiveType;
import java.util.*;

public class ConstantPropagationDataFlowAnalysis extends ForwardDataFlowAnalysis<Map<String, Object>> {

    public final static Integer TOP_SYMBOL = null;
    public final static Object NULL_SYMBOL = new Object();
    private Ast.MethodDecl decl;

    public ConstantPropagationDataFlowAnalysis(Ast.MethodDecl decl) {
        super(decl.cfg);
        this.decl = decl;
        iterate();
    }

    @Override
    protected Map<String, Object> initialState() {
        return new HashMap<>();
    }

    @Override
    protected Map<String, Object> startState() {
        Map<String, Object> startMap = new HashMap<>();
        for (Ast astDecl : this.decl.decls().children()) {
            if (astDecl instanceof Ast.VarDecl) {
                Ast.VarDecl varDecl = (Ast.VarDecl) astDecl;
                if (varDecl.type.equals(Symbol.PrimitiveTypeSymbol.intType.name)) {
                    startMap.put(varDecl.name, 0);
                } else if (varDecl.type.equals(Symbol.PrimitiveTypeSymbol.booleanType.name)) {
                    startMap.put(varDecl.name, false);
                } else if (varDecl.type.equals(Symbol.ClassSymbol.nullType.name)) {
                    startMap.put(varDecl.name, NULL_SYMBOL);
                }
            }
        }
        return startMap;
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
                    } else if (assign.right() instanceof Ast.NullConst) {
                        outState.put(((Ast.Var) assign.left()).name, NULL_SYMBOL);
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
