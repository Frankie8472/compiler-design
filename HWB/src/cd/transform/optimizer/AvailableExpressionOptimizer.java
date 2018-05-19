package cd.transform.optimizer;

import cd.ir.Ast;
import cd.ir.AstVisitor;
import cd.ir.Ast.Expr;
import cd.ir.ControlFlowGraph;
import cd.ir.Symbol;
import cd.transform.analysis.AvailableExpressionDataFlowAnalysis;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

public class AvailableExpressionOptimizer extends AstVisitor<> {
    private ControlFlowGraph cfg;
    private AvailableExpressionDataFlowAnalysis aedfa;
    private Integer leafCounter = 0;
    private Map<Object, Integer> leafValue = new HashMap<>();

    public AvailableExpressionOptimizer(ControlFlowGraph cfg, AvailableExpressionDataFlowAnalysis aedfa){
        this.aedfa = aedfa;
    }

    // check if outstates have something in common, that can be replaced!
    // if some expr in outstates equal -> replacable by tempVar (only calc one time)
    // - tree equality
    // - subtree equality
    // traverse whole tree too the leaves and make prime(op)^(cantor(left,right)
    //

    protected class IdVisitor extends AstVisitor<BigInteger, Expr> {
        //-- Leafs --
        @Override
        public BigInteger var(Ast.Var ast, Expr arg) {
            if(ast.sym.kind.equals(Symbol.VariableSymbol.Kind.FIELD)){
                return null;
            }

            if (!leafValue.containsKey(ast.sym.name){
                leafValue.put(ast.sym.name, ++leafCounter);
            }

            return leafValue.get(ast.sym.name);
        }

        @Override
        public BigInteger booleanConst(Ast.BooleanConst ast, Expr arg) {
            if (!leafValue.containsKey(ast.value){
                leafValue.put(ast.value, ++leafCounter);
            }

            return leafValue.get(ast.value);
        }

        @Override
        public BigInteger intConst(Ast.IntConst ast, Expr arg) {
            if (!leafValue.containsKey(ast.value){
                leafValue.put(ast.value, ++leafCounter);
            }

            return leafValue.get(ast.value);        }

        //-----------
        //-- Skip ---
        @Override
        protected BigInteger dfltExpr(Expr ast, Expr arg) {
            return null;
        }

        //-----------
        //-- Nodes --
        @Override
        public BigInteger binaryOp(Ast.BinaryOp ast, Expr arg) {
            BigInteger left = visit(ast.left(), arg);
            BigInteger right = visit(ast.right(), arg);
            Integer tmp;

            switch(ast.operator){
                case B_GREATER_OR_EQUAL:
                    tmp = 2;
                    break;
                case B_LESS_OR_EQUAL:
                    tmp = 3;
                    break;
                case B_GREATER_THAN:
                    tmp = 5;
                    break;
                case B_LESS_THAN:
                    tmp = 7;
                    break;
                case B_MOD:
                    tmp = 11;
                    break;
                case B_NOT_EQUAL:
                    tmp = 13;
                    break;
                case B_EQUAL:
                    tmp = 17;
                    break;
                case B_AND:
                    tmp = 19;
                    break;
                case B_OR:
                    tmp = 23;
                    break;
                case B_PLUS:
                    tmp = 29;
                    break;
                case B_TIMES:
                    tmp = 31;
                    break;
                case B_DIV:
                    tmp = 37;
                    break;
                case B_MINUS:
                    tmp = 41;
                    break;
                default:
                    tmp = null;
                    break;
            }

            if (tmp == null || left == null || right == null){
                return null;
            }

            BigInteger node = new BigInteger(String.valueOf(tmp));

            return super.binaryOp(ast, arg);
        }

        @Override
        public BigInteger builtInRead(Ast.BuiltInRead ast, Expr arg) {
            BigInteger node = new BigInteger(String.valueOf(43));
            return super.builtInRead(ast, arg);
        }

        @Override
        public BigInteger cast(Ast.Cast ast, Expr arg) {
            BigInteger node = new BigInteger(String.valueOf(47));

            return super.cast(ast, arg);
        }

        @Override
        public BigInteger unaryOp(Ast.UnaryOp ast, Expr arg) {
            Integer tmp;
            switch(ast.operator){
                case U_BOOL_NOT:
                    tmp = 59;
                    break;
                case U_MINUS:
                    tmp = 61;
                    break;
                case U_PLUS:
                    tmp = 67;
                    break;
            }

            BigInteger node = new BigInteger(String.valueOf(tmp));

            return super.unaryOp(ast, arg);
        }

        private BigInteger extendedContor(BigInteger op, BigInteger left, BigInteger right){
            return op.pow(left.add(right));
            // todo: make in a first step all integer, second boolean
        }
    }
}
