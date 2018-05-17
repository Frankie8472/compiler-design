package cd.transform.analysis;

import cd.ir.Ast;
import cd.ir.BasicBlock;
import cd.ir.ControlFlowGraph;

import java.util.*;

import cd.ir.Ast.Stmt;
import cd.ir.Ast.Var;
import cd.ir.Symbol.VariableSymbol.Kind;


public class ReachingDefinitionAnalysis extends DataFlowAnalysis<Set<Stmt>>{
    private Map<BasicBlock, Set<Stmt>> gen;
    private Map<BasicBlock, Set<Stmt>> kill;
    private Map<Var, Set<Stmt>> varDefMap;

    public ReachingDefinitionAnalysis(ControlFlowGraph cfg){
        super(cfg);
        generateVarDefMap();
        generateDefKillSets();
        iterate();
    }

    @Override
    protected Set<Stmt> initialState() {
        return new HashSet<>();
    }

    @Override
    protected Set<Stmt> startState() {
        return new HashSet<>();
    }

    @Override
    protected Set<Stmt> transferFunction(BasicBlock block, Set<Stmt> inState) {
        Set<Stmt> out = inState;
        out.removeAll(kill.get(block));
        out.addAll(gen.get(block));
        return out;
    }

    @Override
    protected Set<Stmt> join(Set<Set<Stmt>> objects) {
        Set<Stmt> in = new HashSet<>();
        objects.forEach(in::addAll);
        return in;
    }

    private void generateVarDefMap(){
        // Generate varDefMap
        for (BasicBlock basicBlock : cfg.allBlocks){
            for (Stmt stmt : basicBlock.stmts){
                if (stmt instanceof Ast.Assign){
                    Ast.Expr dest = ((Ast.Assign) stmt).left();
                    if (dest instanceof Var){
                        Var var = (Var) dest;
                        if (var.sym.kind.equals(Kind.LOCAL) || var.sym.kind.equals(Kind.PARAM)) { //todo: check if field is a thing here
                            if (!varDefMap.containsKey(var)){
                                varDefMap.put(var, new HashSet<>());
                            }
                            varDefMap.get(var).add(stmt);
                        }
                    }
                }
            }
        }
    }

    private void generateDefKillSets(){
        // Generate def and kill sets
        for (BasicBlock basicBlock : cfg.allBlocks){
            kill.put(basicBlock, new HashSet<>());
            gen.put(basicBlock, new HashSet<>());

            for (Stmt stmt : basicBlock.stmts){
                if (stmt instanceof Ast.Assign){
                    Ast.Expr dest = ((Ast.Assign) stmt).left();
                    if (dest instanceof Var){
                        Var var = (Var) dest;
                        if (var.sym.kind.equals(Kind.PARAM) || var.sym.kind.equals(Kind.PARAM)) {
                            Set<Stmt> killed = varDefMap.get(var);
                            killed.remove(stmt);
                            gen.get(basicBlock).add(stmt);
                            for (Stmt killedStmt : killed) {
                                if (!kill.get(basicBlock).contains(killedStmt)){
                                    kill.get(basicBlock).add(killedStmt);
                                }
                                if (gen.get(basicBlock).contains(killedStmt)){
                                    gen.get(basicBlock).remove(killedStmt);
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
