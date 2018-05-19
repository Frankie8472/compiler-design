package cd.transform.optimizer;

import cd.ir.Ast;
import cd.ir.AstVisitor;
import cd.ir.BasicBlock;
import cd.util.debug.AstDump;

public class PreCalculateOperatorsOptimizer extends AstVisitor<Ast, Void> {

    private Ast.MethodDecl methodDecl;

    public PreCalculateOperatorsOptimizer(Ast.MethodDecl methodDecl) {
        this.methodDecl = methodDecl;
    }

    public void optimize() {
        for (BasicBlock block : methodDecl.cfg.allBlocks) {
            for (int i = 0; i < block.stmts.size(); i++) {
                visit(block.stmts.get(i), null);
            }
            if (block.condition != null) {
                visit(block.condition, null);
            }
        }
    }

    @Override
    protected Ast dflt(Ast ast, Void arg) {
        for (int i = 0; i < ast.rwChildren.size(); i++) {
            Ast child = ast.rwChildren.get(i);
            if (child != null) {
                try {
                    Ast returnValue = visit(child, arg);
                    if (returnValue != null) {
                        ast.rwChildren.set(i, returnValue);
                    }
                } catch (ArithmeticException ignored) {

                }
            }
        }
        return null;
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
                default:
                    return null;
            }

        } else {
            return null;
        }
    }

    @Override
    public Ast unaryOp(Ast.UnaryOp ast, Void arg) {
        dflt(ast, arg);
        if (ast.arg() instanceof Ast.IntConst) {
            switch (ast.operator) {
                case U_PLUS:
                    return ast.arg();
                case U_MINUS:
                    return new Ast.IntConst(((Ast.IntConst) ast.arg()).value);
                default:
                    return null;
            }
        }
        if (ast.arg() instanceof Ast.BooleanConst) {
            if (ast.operator == Ast.UnaryOp.UOp.U_BOOL_NOT) {
                return new Ast.BooleanConst(!((Ast.BooleanConst) ast.arg()).value);
            }
        }
        return null;
    }
}
