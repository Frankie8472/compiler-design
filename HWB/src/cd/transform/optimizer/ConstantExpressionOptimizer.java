package cd.transform.optimizer;

import cd.ir.Ast;
import cd.ir.AstVisitor;
import cd.ir.BasicBlock;
import cd.transform.analysis.ReachingDefinitionDataFlowAnalysis;

public class ConstantExpressionOptimizer extends AstVisitor<Void, Void> {
    private Ast.MethodDecl methodDecl;
    private Boolean changedAst;


    public ConstantExpressionOptimizer(Ast.MethodDecl methodDecl) {
        this.methodDecl = methodDecl;
    }

    public void optimize() {
        for (BasicBlock block : methodDecl.cfg.allBlocks){
            for (Ast.Stmt stmt : block.stmts){
                changedAst = true;
                while(changedAst){
                    visit(stmt, null);
                }
            }
        }
    }

    @Override
    protected Void dflt(Ast ast, Void arg) {
        for (int i = 0; i < ast.rwChildren.size(); i++) {
            Ast child = ast.rwChildren.get(i);
            Ast newAst = null;
            if (child instanceof Ast.BinaryOp){
                Ast.BinaryOp binaryOp = (Ast.BinaryOp) child;
                if (binaryOp.left() instanceof Ast.IntConst && binaryOp.right() instanceof Ast.IntConst){
                    switch (binaryOp.operator){
                        case B_MINUS:
                            newAst = new Ast.IntConst(((Ast.IntConst) binaryOp.left()).value - ((Ast.IntConst) binaryOp.right()).value);
                            break;
                        case B_PLUS:
                            newAst = new Ast.IntConst(((Ast.IntConst) binaryOp.left()).value + ((Ast.IntConst) binaryOp.right()).value);
                            break;
                        case B_TIMES:
                            newAst = new Ast.IntConst(((Ast.IntConst) binaryOp.left()).value * ((Ast.IntConst) binaryOp.right()).value);
                            break;
                        case B_DIV:
                            newAst = new Ast.IntConst(((Ast.IntConst) binaryOp.left()).value / ((Ast.IntConst) binaryOp.right()).value);
                            break;
                        case B_MOD:
                            newAst = new Ast.IntConst(((Ast.IntConst) binaryOp.left()).value % ((Ast.IntConst) binaryOp.right()).value);
                            break;
                        case B_LESS_THAN:
                            newAst = new Ast.BooleanConst(((Ast.IntConst) binaryOp.left()).value < ((Ast.IntConst) binaryOp.right()).value);
                            break;
                        case B_LESS_OR_EQUAL:
                            newAst = new Ast.BooleanConst(((Ast.IntConst) binaryOp.left()).value <= ((Ast.IntConst) binaryOp.right()).value);
                            break;
                        case B_GREATER_THAN:
                            newAst = new Ast.BooleanConst(((Ast.IntConst) binaryOp.left()).value > ((Ast.IntConst) binaryOp.right()).value);
                            break;
                        case B_GREATER_OR_EQUAL:
                            newAst = new Ast.BooleanConst(((Ast.IntConst) binaryOp.left()).value >= ((Ast.IntConst) binaryOp.right()).value);
                            break;
                        case B_EQUAL:
                            newAst = new Ast.BooleanConst(((Ast.IntConst) binaryOp.left()).value == ((Ast.IntConst) binaryOp.right()).value);
                            break;
                        case B_NOT_EQUAL:
                            newAst = new Ast.BooleanConst(((Ast.IntConst) binaryOp.left()).value != ((Ast.IntConst) binaryOp.right()).value);
                            break;
                    }
                } else if ((binaryOp.left() instanceof Ast.BooleanConst && binaryOp.right() instanceof Ast.BooleanConst)){
                    switch (binaryOp.operator){
                        case B_OR:
                            newAst = new Ast.BooleanConst(((Ast.BooleanConst) binaryOp.left()).value || ((Ast.BooleanConst) binaryOp.right()).value);
                            break;
                        case B_AND:
                            newAst = new Ast.BooleanConst(((Ast.BooleanConst) binaryOp.left()).value && ((Ast.BooleanConst) binaryOp.right()).value);
                            break;
                        case B_EQUAL:
                            newAst = new Ast.BooleanConst(((Ast.BooleanConst) binaryOp.left()).value == ((Ast.BooleanConst) binaryOp.right()).value);
                            break;
                        case B_NOT_EQUAL:
                            newAst = new Ast.BooleanConst(((Ast.BooleanConst) binaryOp.left()).value != ((Ast.BooleanConst) binaryOp.right()).value);
                            break;
                    }
                }
            } else if (child instanceof Ast.UnaryOp) {
                Ast.UnaryOp unaryOp = (Ast.UnaryOp) child;
                if (unaryOp.arg() instanceof Ast.IntConst){
                    if(unaryOp.operator.equals(Ast.UnaryOp.UOp.U_MINUS)){
                        newAst = new Ast.IntConst(-(((Ast.IntConst) unaryOp.arg()).value));
                    } else { // Plus operator
                        newAst = new Ast.IntConst(((Ast.IntConst) unaryOp.arg()).value);
                    }
                } else if (unaryOp.arg() instanceof Ast.BooleanConst){
                    // Then operator must be a not
                    newAst = new Ast.BooleanConst(!((Ast.BooleanConst) unaryOp.arg()).value);
                }
            }

            if (newAst != null){
                ast.rwChildren.set(i, newAst);
                changedAst = true;
            } else {
                changedAst = false;
            }
        }
        return null;
    }
}
