package cd.transform.analysis;

import cd.ir.Ast;
import cd.ir.Ast.Stmt;
import cd.ir.Ast.Var;
import cd.ir.AstVisitor;
import cd.ir.BasicBlock;
import cd.ir.ControlFlowGraph;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import cd.ir.Symbol.VariableSymbol.Kind;


public class LiveVariablesAnalysis extends DataFlowAnalysis<Set<Var>>{
    private Map<BasicBlock, Set<Ast.Var>> def;
    private Map<BasicBlock, Set<Ast.Var>> use;
    private Map<Ast.Var, Set<Ast.Stmt>> varDefMap;

    public LiveVariablesAnalysis(ControlFlowGraph cfg){
        super(cfg);
        generateDefUse();
        iterate();
    }

    @Override
    protected Set<Var> initialState() {
        return null;
    }

    @Override
    protected Set<Var> startState() {
        return null;
    }

    @Override
    protected Set<Var> transferFunction(BasicBlock block, Set<Var> inState) {
        return null;
    }

    @Override
    protected Set<Var> join(Set<Set<Var>> objects) {
        return null;
    }

    private void generateDefUse(){
         for(BasicBlock basicBlock : cfg.allBlocks){
             use.put(basicBlock, new HashSet<>());
             def.put(basicBlock, new HashSet<>());

             for(Stmt stmt : basicBlock.stmts){
                 Visitor visitor = new Visitor();
                 visitor.visit(stmt, basicBlock);
                 if(basicBlock.condition != null){
                     visitor.visit(basicBlock.condition, basicBlock);
                 }
             }
         }
    }

    private void addToUse(BasicBlock block, Var var){
        if (!def.get(block).contains(var)){
            use.get(block).add(var);
        }
    }

    private void addToDef(BasicBlock block, Var var){
        if (!use.get(block).contains(var)){
            def.get(block).add(var);
        }
    }

    protected class Visitor extends AstVisitor<Void, BasicBlock> {
        @Override
        public Void assign(Ast.Assign ast, BasicBlock arg) {
            visit(ast.right(), arg);
            if (ast.left() instanceof Var){
                Var left = (Var) ast.left();
                if(!left.sym.kind.equals(Kind.FIELD)){
                    addToDef(arg, left);
                }
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
