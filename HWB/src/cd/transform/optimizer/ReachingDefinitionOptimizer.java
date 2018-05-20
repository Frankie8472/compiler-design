package cd.transform.optimizer;

import cd.ir.Ast;
import cd.ir.AstVisitor;
import cd.transform.analysis.ReachingDefinitionDataFlowAnalysis;

public class ReachingDefinitionOptimizer extends AstVisitor<Object, Object> {
    private Ast.MethodDecl methodDecl;
    private ReachingDefinitionDataFlowAnalysis analysis;

    public ReachingDefinitionOptimizer(Ast.MethodDecl methodDecl) {
        this.methodDecl = methodDecl;
        this.analysis = new ReachingDefinitionDataFlowAnalysis(methodDecl.cfg);
    }

    public void optimize() {

    }
}
