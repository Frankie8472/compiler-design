package cd.transform.optimizer;

import cd.ir.Ast;
import cd.ir.AstVisitor;
import cd.ir.BasicBlock;
import cd.ir.Symbol;
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
                    return createNewIntConst(leftValue + rightValue);
                case B_MINUS:
                    return createNewIntConst(leftValue - rightValue);
                case B_TIMES:
                    return createNewIntConst(leftValue * rightValue);
                case B_DIV:
                    if (rightValue == 0) {
                        return null;
                    } else {
                        return createNewIntConst(leftValue / rightValue);
                    }
                case B_MOD:
                    if (rightValue == 0) {
                        return null;
                    } else {
                        return createNewIntConst(leftValue % rightValue);
                    }
                case B_EQUAL:
                    return createNewBoolConst(leftValue.equals(rightValue));
                case B_NOT_EQUAL:
                    return createNewBoolConst(!leftValue.equals(rightValue));
                case B_GREATER_OR_EQUAL:
                    return createNewBoolConst(leftValue >= rightValue);
                case B_GREATER_THAN:
                    return createNewBoolConst(leftValue > rightValue);
                case B_LESS_OR_EQUAL:
                    return createNewBoolConst(leftValue <= rightValue);
                case B_LESS_THAN:
                    return createNewBoolConst(leftValue < rightValue);
                default:
                    return null;
            }
        } else if (ast.left() instanceof Ast.BooleanConst && ast.right() instanceof Ast.BooleanConst) {
            Boolean leftValue = ((Ast.BooleanConst) ast.left()).value;
            Boolean rightValue = ((Ast.BooleanConst) ast.right()).value;
            switch (ast.operator) {
                case B_AND:
                    return createNewBoolConst(leftValue && rightValue);
                case B_OR:
                    return createNewBoolConst(leftValue || rightValue);
                case B_EQUAL:
                    return createNewBoolConst(leftValue.equals(rightValue));
                case B_NOT_EQUAL:
                    return createNewBoolConst(!leftValue.equals(rightValue));
                default:
                    return null;
            }

        } else if (isConstant(ast.left())) {
            Ast constant = ast.left();
            if ((ast.right() instanceof Ast.BinaryOp)) {
                Ast.BinaryOp other = (Ast.BinaryOp) ast.right();
                if (ast.left() instanceof Ast.IntConst) {
                    simplifyIntEquation(ast, other, ((Ast.IntConst) constant).value, true);
                } else if (ast.left() instanceof Ast.BooleanConst) {
                    simplifyBooleanEquation(ast, other, ((Ast.BooleanConst) constant).value);
                }
            }
        } else if (isConstant(ast.right())) {
            Ast constant = ast.right();
            if ((ast.left() instanceof Ast.BinaryOp)) {
                Ast.BinaryOp other = (Ast.BinaryOp) ast.left();
                if (ast.right() instanceof Ast.IntConst) {
                    simplifyIntEquation(ast, other, ((Ast.IntConst) constant).value, false);
                } else if (ast.right() instanceof Ast.BooleanConst) {
                    simplifyBooleanEquation(ast, other, ((Ast.BooleanConst) constant).value);
                }
            }
        }
        return handleSpecialCases(ast);
    }

    /**
     * This function simplifies equations with only plus and minus. Constants that are known are put together to reduce
     * the amount of operators used in the calculation. An expression like (a + (1 + ( (4 - 5) + 3))) simplifies to
     * (a + 3). Since the Minus is not commutative the method got big and ugly.
     *
     * This Method modifies the binary operation given to achieve the simplification.
     *
     * @param ast The Binary Operation to modify.
     * @param other The subExpressiong of the binary operation ast.
     * @param astConstant the value of the constant.
     * @param leftSideConstant Indicates whether the constant in the expression to simplify is on the left or the right
     *                         side of the binary operation other. In the example 1 + (3 + x) the 1 is on the left side.
     */
    private void simplifyIntEquation(Ast.BinaryOp ast, Ast.BinaryOp other, int astConstant, boolean leftSideConstant) {
        if ((ast.operator == Ast.BinaryOp.BOp.B_MINUS || ast.operator == Ast.BinaryOp.BOp.B_PLUS) &&
                (other.operator == Ast.BinaryOp.BOp.B_MINUS || other.operator == Ast.BinaryOp.BOp.B_PLUS)) {

            if (ast.operator == other.operator) {
                if (ast.operator == Ast.BinaryOp.BOp.B_PLUS) {
                    if (other.left() instanceof Ast.IntConst) {
                        ast.setLeft(createNewIntConst(astConstant + ((Ast.IntConst) other.left()).value));
                        ast.setRight(other.right());
                    } else if (other.right() instanceof Ast.IntConst) {
                        ast.setLeft(createNewIntConst(astConstant + ((Ast.IntConst) other.right()).value));
                        ast.setRight(other.left());
                    }
                } else {
                    if (leftSideConstant) {
                        if (other.left() instanceof Ast.IntConst) {
                            ast.setLeft(createNewIntConst(astConstant - ((Ast.IntConst) other.left()).value));
                            ast.setRight(other.right());
                            ast.operator = Ast.BinaryOp.BOp.B_PLUS;
                        } else if (other.right() instanceof Ast.IntConst) {
                            ast.setLeft(createNewIntConst(astConstant + ((Ast.IntConst) other.right()).value));
                            ast.setRight(other.left());
                            ast.operator = Ast.BinaryOp.BOp.B_MINUS;
                        }
                    } else {
                        if (other.left() instanceof Ast.IntConst) {
                            ast.setLeft(createNewIntConst(((Ast.IntConst) other.left()).value - astConstant));
                            ast.setRight(other.right());
                            ast.operator = Ast.BinaryOp.BOp.B_MINUS;
                        } else if (other.right() instanceof Ast.IntConst) {
                            ast.setLeft(createNewIntConst((-((Ast.IntConst) other.right()).value) - astConstant));
                            ast.setRight(other.left());
                            ast.operator = Ast.BinaryOp.BOp.B_PLUS;
                        }
                    }
                }

            } else if (other.operator == Ast.BinaryOp.BOp.B_PLUS && ast.operator == Ast.BinaryOp.BOp.B_MINUS) {
                if (other.left() instanceof Ast.IntConst) {
                    if (leftSideConstant) {
                        ast.setLeft(createNewIntConst(astConstant - ((Ast.IntConst) other.left()).value));
                    } else {
                        ast.setLeft(createNewIntConst(((Ast.IntConst) other.left()).value - astConstant));
                        ast.operator = Ast.BinaryOp.BOp.B_PLUS;
                    }
                    ast.setRight(other.right());
                } else if (other.right() instanceof Ast.IntConst) {
                    if (leftSideConstant) {
                        ast.setLeft(createNewIntConst(astConstant - ((Ast.IntConst) other.right()).value));
                    } else {
                        ast.setLeft(createNewIntConst(((Ast.IntConst) other.right()).value - astConstant));
                        ast.operator = Ast.BinaryOp.BOp.B_PLUS;
                    }
                    ast.setRight(other.left());
                }
            } else if (other.operator == Ast.BinaryOp.BOp.B_MINUS && ast.operator == Ast.BinaryOp.BOp.B_PLUS) {
                if (other.right() instanceof Ast.IntConst) {
                    ast.setLeft(createNewIntConst(astConstant - ((Ast.IntConst) other.right()).value));
                    ast.setRight(other.left());
                } else if (other.left() instanceof Ast.IntConst) {
                    ast.setLeft(createNewIntConst(astConstant + ((Ast.IntConst) other.left()).value));
                    ast.setRight(other.right());
                    ast.operator = Ast.BinaryOp.BOp.B_MINUS;
                }
            }
        } else if (ast.operator == Ast.BinaryOp.BOp.B_TIMES && other.operator == Ast.BinaryOp.BOp.B_TIMES) {
            if (other.left() instanceof Ast.IntConst) {
                ast.setLeft(createNewIntConst(astConstant * ((Ast.IntConst) other.left()).value));
                ast.setRight(other.right());
            } else if (other.right() instanceof Ast.IntConst) {
                ast.setLeft(createNewIntConst(astConstant * ((Ast.IntConst) other.right()).value));
                ast.setRight(other.left());
            }
        }
    }

    private void simplifyBooleanEquation(Ast.BinaryOp ast, Ast.BinaryOp other, boolean astConstant) {
        if ((ast.operator == Ast.BinaryOp.BOp.B_AND || ast.operator == Ast.BinaryOp.BOp.B_OR) &&
                (other.operator == Ast.BinaryOp.BOp.B_AND || other.operator == Ast.BinaryOp.BOp.B_OR)) {
            if (ast.operator == Ast.BinaryOp.BOp.B_AND && other.operator == Ast.BinaryOp.BOp.B_AND) {
                if (other.left() instanceof Ast.BooleanConst) {
                    ast.setLeft(createNewBoolConst(astConstant && ((Ast.BooleanConst) other.left()).value));
                    ast.setRight(other.right());
                } else if (other.right() instanceof Ast.BooleanConst) {
                    ast.setLeft(createNewBoolConst(astConstant && ((Ast.BooleanConst) other.right()).value));
                    ast.setRight(other.left());
                }
            } else if (ast.operator == Ast.BinaryOp.BOp.B_OR && other.operator == Ast.BinaryOp.BOp.B_OR) {
                {
                    if (other.left() instanceof Ast.BooleanConst) {
                        ast.setLeft(createNewBoolConst(astConstant || ((Ast.BooleanConst) other.left()).value));
                        ast.setRight(other.right());
                    } else if (other.right() instanceof Ast.BooleanConst) {
                        ast.setLeft(createNewBoolConst(astConstant || ((Ast.BooleanConst) other.right()).value));
                        ast.setRight(other.left());
                    }
                }
            }
        }
    }

    private Ast.Expr handleSpecialCases(Ast.BinaryOp ast) {
        int value;
        Ast.Expr other;
        if (ast.left() instanceof Ast.IntConst) {
            value = ((Ast.IntConst) ast.left()).value;
            other = ast.right();
        } else if (ast.right() instanceof Ast.IntConst) {
            value = ((Ast.IntConst) ast.right()).value;
            other = ast.left();
            if(ast.operator == Ast.BinaryOp.BOp.B_DIV && value == 1){
                return other;
            }
        } else {
            return null;
        }
        if (value == 0) {
            if (ast.operator == Ast.BinaryOp.BOp.B_PLUS || ast.operator == Ast.BinaryOp.BOp.B_MINUS) {
                return other;
            }
            if (ast.operator == Ast.BinaryOp.BOp.B_TIMES) {
                return createNewIntConst(0);
            }
        } else if (value == 1) {
            if (ast.operator == Ast.BinaryOp.BOp.B_TIMES) {
                return other;
            }
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
            return createNewIntConst(-((Ast.IntConst) ast.arg()).value);
        }
        if (ast.arg() instanceof Ast.BooleanConst && ast.operator == Ast.UnaryOp.UOp.U_BOOL_NOT) {
            return createNewBoolConst(!((Ast.BooleanConst) ast.arg()).value);
        }
        return null;
    }

    private boolean isConstant(Ast ast) {
        return ast instanceof Ast.IntConst || ast instanceof Ast.BooleanConst;
    }

}
