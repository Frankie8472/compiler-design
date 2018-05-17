package cd.transform.analysis;

import cd.ir.Ast;
import cd.ir.AstVisitor;
import cd.ir.BasicBlock;
import cd.ir.ControlFlowGraph;

import java.util.*;

public class LiveVariableDataFlowAnalysis extends DataFlowAnalysis<Set<Object>> {

    public LiveVariableDataFlowAnalysis(ControlFlowGraph cfg) {
        super(cfg);
        iterate();
    }

    @Override
    protected Set<Object> initialState() {
        return new HashSet<>();
    }

    @Override
    protected Set<Object> startState() {
        return new HashSet<>();
    }

    @Override
    protected Set<Object> transferFunction(BasicBlock block, Set<Object> inState) {
        List<String> usedVars = new ArrayList<>();
        List<String> declaredVars = new ArrayList<>();

        block.stmts.stream().filter(stmt -> stmt instanceof Ast.Assign && ((Ast.Assign) stmt).left() instanceof Ast.Var)
                .forEach(stmt -> declaredVars.add(((Ast.Var)((Ast.Assign)stmt).left()).name));
        block.stmts.forEach(stmt -> new AstVisitor<Void, Void>(){
            @Override
            public Void var(Ast.Var ast, Void arg) {
                usedVars.add(ast.name);
                return null;
            }
        }.visit(stmt,null));

        Set<Object> outState = new HashSet<>(inState);

        outState.addAll(usedVars);
        outState.removeAll(declaredVars);

        return outState;
    }

    @Override
    protected Set<Object> join(Set<Set<Object>> objects) {
        Set<Object> result = new HashSet<>();
        objects.forEach(result::addAll);
        return result;
    }
}
