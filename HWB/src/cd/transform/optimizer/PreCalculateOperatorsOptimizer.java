package cd.transform.optimizer;

import cd.ir.Ast;
import cd.ir.AstVisitor;
import cd.ir.BasicBlock;
import cd.util.debug.AstDump;

public class PreCalculateOperatorsOptimizer extends BaseOptimizer<Void> {

    private Ast.MethodDecl methodDecl;

    public PreCalculateOperatorsOptimizer(Ast.MethodDecl methodDecl) {
        this.methodDecl = methodDecl;
    }

    public void optimize() {
        for (BasicBlock block : methodDecl.cfg.allBlocks) {
            for (int i = 0; i < block.stmts.size(); i++) {
                visit(block.stmts.get(i), null);
//                System.out.println(cd.util.debug.AstDump.toString(block.stmts.get(i)));
            }
            if (block.condition != null) {
                visit(block.condition, null);
            }
        }
    }

    /**
     * In this function we concatenate every constant binary expression. If a division by zero exception occurs the
     * exception is bubbled up until the expression is not an constant binary expression. that way less computation has
     * to be done to reach the division by zero.
     *
     * @param ast
     * @param arg
     * @return
     */
    @Override
    public Ast binaryOp(Ast.BinaryOp ast, Void arg) {
        //TODO: When an arithmetic exception happens, it is just ignored and nothing is optimized. handle divide by zero
        dflt(ast, arg);
        if (ast.left() instanceof Ast.IntConst && ast.right() instanceof Ast.IntConst) {
            Integer leftValue = ((Ast.IntConst) ast.left()).value;
            Integer rightValue = ((Ast.IntConst) ast.right()).value;

            switch (ast.operator) {
                case B_PLUS:
                    return new Ast.IntConst(leftValue + rightValue);
                case B_MINUS:
                    return new Ast.IntConst(leftValue - rightValue);
                case B_TIMES:
                    return new Ast.IntConst(leftValue * rightValue);
                case B_DIV:
                    return new Ast.IntConst(leftValue / rightValue);
                case B_MOD:
                    return new Ast.IntConst(leftValue % rightValue);
                case B_EQUAL:
                    return new Ast.BooleanConst(leftValue.equals(rightValue));
                case B_NOT_EQUAL:
                    return new Ast.BooleanConst(!leftValue.equals(rightValue));
                case B_GREATER_OR_EQUAL:
                    return new Ast.BooleanConst(leftValue >= rightValue);
                case B_GREATER_THAN:
                    return new Ast.BooleanConst(leftValue > rightValue);
                case B_LESS_OR_EQUAL:
                    return new Ast.BooleanConst(leftValue <= rightValue);
                case B_LESS_THAN:
                    return new Ast.BooleanConst(leftValue < rightValue);
                default:
                    return null;
            }
        } else if (ast.left() instanceof Ast.BooleanConst && ast.right() instanceof Ast.BooleanConst) {
            Boolean leftValue = ((Ast.BooleanConst) ast.left()).value;
            Boolean rightValue = ((Ast.BooleanConst) ast.right()).value;
            switch (ast.operator) {
                case B_AND:
                    return new Ast.BooleanConst(leftValue && rightValue);
                case B_OR:
                    return new Ast.BooleanConst(leftValue || rightValue);
                case B_EQUAL:
                    return new Ast.BooleanConst(leftValue.equals(rightValue));
                case B_NOT_EQUAL:
                    return new Ast.BooleanConst(!leftValue.equals(rightValue));
                default:
                    return null;
            }

        } else if(isConstantValue(ast.left()) || isConstantValue(ast.right())){
            Ast constant;
            Ast.BinaryOp other;
            Ast otherConstant;
            if(isConstantValue(ast.left())){
                constant = ast.left();
                if(!(ast.right() instanceof Ast.BinaryOp)){
                    return null;
                }
                other = (Ast.BinaryOp) ast.right();
            } else {
                constant = ast.right();
                if(!(ast.left() instanceof Ast.BinaryOp)){
                    return null;
                }
                other = (Ast.BinaryOp) ast.left();
            }
//            if(other.operator == Ast.BinaryOp.BOp.B_PLUS && ast.operator == Ast.BinaryOp.BOp.B_PLUS){
//                if(isConstantValue(other.right())){
//                    ast.setLeft(new Ast.IntConst(((Ast.IntConst)constant).value + ((Ast.IntConst)other.right()).value));
//                    ast.setRight(other.left());
//                }
//            }

            // (1 + 2 + (x / 5)) -> 3 + x/5 -> 15/5 + x/5 ->(15 + x) /5
            // (1 + 2 + (5 / x)) -> 3 + 5/x -> (3*x))/x + 5/x ->(3x + 5)/x

        }
        return null;
    }

    @Override
    public Ast unaryOp(Ast.UnaryOp ast, Void arg) {
        dflt(ast, arg);
        if (ast.operator == Ast.UnaryOp.UOp.U_PLUS) {
            return ast.arg();
        }
        if (ast.arg() instanceof Ast.IntConst && ast.operator == Ast.UnaryOp.UOp.U_MINUS) {
            return new Ast.IntConst(-((Ast.IntConst) ast.arg()).value);
        }
        if (ast.arg() instanceof Ast.BooleanConst && ast.operator == Ast.UnaryOp.UOp.U_BOOL_NOT) {
            return new Ast.BooleanConst(!((Ast.BooleanConst) ast.arg()).value);
        }
        return null;
    }

    private boolean isConstantValue(Ast ast){
        return ast instanceof Ast.IntConst || ast instanceof Ast.BooleanConst;
    }
}
