package cd.transform.optimizer;

import cd.ir.Ast;
import cd.ir.AstVisitor;
import cd.ir.BasicBlock;
import cd.ir.Symbol;
import cd.transform.analysis.LiveVariableAnalysis;
import cd.util.debug.AstDump;

import java.util.*;

public class RemoveUnusedVarDecl extends AstVisitor<Void, List<String>> {

    private Ast.MethodDecl methodDecl;

    public RemoveUnusedVarDecl(Ast.MethodDecl methodDecl) {
        this.methodDecl = methodDecl;
    }

    public void optimize() {
        List<String> removeList = new ArrayList<>();
        for (Ast astDecl : methodDecl.decls().children()) {
            removeList.add(((Ast.VarDecl) astDecl).name);
        }
        for(BasicBlock block : methodDecl.cfg.allBlocks){
            for(Ast.Stmt stmt : block.stmts){
                visit(stmt, removeList);
            }
            if(block.condition != null){
                visit(block.condition, removeList);
            }
        }
        for (int i=0; i < methodDecl.decls().rwChildren.size(); i++) {
            if(removeList.contains(((Ast.VarDecl) methodDecl.decls().rwChildren.get(i)).name)){
                methodDecl.decls().rwChildren.remove(i);
                i--;
            }
        }

        for(String var : removeList) {
            methodDecl.sym.locals.remove(var);
        }
    }

    @Override
    public Void var(Ast.Var ast, List<String> arg) {
        arg.remove(ast.name);
        return super.var(ast, arg);
    }
}
