package cd.transform.analysis;

import cd.ir.*;
import cd.ir.Ast.Var;
import cd.ir.Ast.Expr;
import cd.ir.Symbol.VariableSymbol.Kind;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class j_AvailableExpressionDataFlowAnalysis extends ForwardDataFlowAnalysis<Set<Expr>> {
    private Map<BasicBlock, Set<Expr>> gen;
    private Map<BasicBlock, Set<Expr>> kill;
    private Map<Var, Set<Expr>> varExprMap;
    private Set<Expr> U;

    public j_AvailableExpressionDataFlowAnalysis(ControlFlowGraph cfg){
        super(cfg);
        generateSets();
        iterate();
    }

    @Override
    protected Set<Expr> initialState() {
        return null;
    }

    @Override
    protected Set<Expr> startState() {
        return null;
    }

    @Override
    protected Set<Expr> transferFunction(BasicBlock block, Set<Expr> inState) {
        return null;
    }

    @Override
    protected Set<Expr> join(Set<Set<Expr>> sets) {
        return null;
    }



    private void generateSets(){
        for (BasicBlock basicBlock : cfg.allBlocks){

            U = new HashSet<>();
            gen.put(basicBlock, new HashSet<>());
            kill.put(basicBlock, new HashSet<>());

            Visitor visitor = new Visitor();
            basicBlock.stmts.forEach(stmt -> visitor.visit(stmt, basicBlock));
            if(basicBlock.condition != null){
                visitor.visit(basicBlock.condition, basicBlock);
            }
        }
    }

    protected class Visitor extends AstVisitor<Void, BasicBlock> {
        @Override
        protected Void dfltExpr(Expr ast, BasicBlock arg) {
            U.add(ast);
            return null;
        }

        @Override
        public Void assign(Ast.Assign ast, BasicBlock arg) {
            if (ast.left() instanceof Var){
                Var var = (Var) ast.left();
                if (!var.sym.kind.equals(Kind.FIELD)){

                }
            }

            return null;
        }
    }

    private class ExprWrapper{
        Expr expr;

        public ExprWrapper(Expr expr){
            this.expr = expr;
        }

        @Override
        public boolean equals(Object obj) {
            if(obj instanceof ExprWrapper){
                return new ExprComparer().visit(expr, ((ExprWrapper) obj).expr);
            }
            return false;
        }
    }

    private class ExprComparer extends ExprVisitor<Boolean, Ast>{

        @Override
        protected Boolean dfltExpr(Expr ast, Ast arg) {
            for(Ast child : ast.rwChildren){
                boolean match = false;
                for (Ast otherChild : arg.rwChildren){
                    if (visit((Expr)child, otherChild)){
                        match = true;
                        break;
                    }
                }
                if(!match){
                    return false;
                }
            }
            return false;
        }

        @Override
        public Boolean var(Var ast, Ast arg) {
            return arg instanceof Var && ast.name.equals(((Var) arg).name);
        }

        @Override
        public Boolean binaryOp(Ast.BinaryOp ast, Ast arg) {
            if(!(arg instanceof Ast.BinaryOp)){
                return false;
            }
            Ast.BinaryOp other = (Ast.BinaryOp) arg;
            if (ast.operator != other.operator){
                return false;
            }
            if(ast.operator.isCommutative()){
                return dfltExpr(ast, arg);
            } else {
                return visit(ast.left(), other.left()) && visit(ast.right(), other.right());
            }
        }

        @Override
        public Boolean booleanConst(Ast.BooleanConst ast, Ast arg) {
            return arg instanceof Ast.BooleanConst && ast.value == ((Ast.BooleanConst) arg).value;
        }

        @Override
        public Boolean intConst(Ast.IntConst ast, Ast arg) {
            return arg instanceof Ast.IntConst && ast.value == ((Ast.IntConst) arg).value;
        }

        @Override
        public Boolean index(Ast.Index ast, Ast arg) {
            return super.index(ast, arg);
        }

        @Override
        public Boolean builtInRead(Ast.BuiltInRead ast, Ast arg) {
            return false;
        }

        @Override
        public Boolean methodCall(Ast.MethodCallExpr ast, Ast arg) {
            return false;
        }

        @Override
        public Boolean newArray(Ast.NewArray ast, Ast arg) {
            return false;
        }

        @Override
        public Boolean newObject(Ast.NewObject ast, Ast arg) {
            return false;
        }

        @Override
        public Boolean nullConst(Ast.NullConst ast, Ast arg) {
            return arg instanceof Ast.NullConst;
        }

        @Override
        public Boolean unaryOp(Ast.UnaryOp ast, Ast arg) {
            return (arg instanceof Ast.UnaryOp) && ast.operator == ((Ast.UnaryOp) arg).operator && visit(ast.arg(), ((Ast.UnaryOp) arg).arg());
        }

        @Override
        public Boolean thisRef(Ast.ThisRef ast, Ast arg) {
            return false;
        }

        @Override
        public Boolean field(Ast.Field ast, Ast arg) {
            return false;
        }

        @Override
        public Boolean cast(Ast.Cast ast, Ast arg) {
            return false;
        }

    }
}
