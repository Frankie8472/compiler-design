package cd.transform.optimizer;

import cd.ir.Ast;
import cd.ir.AstVisitor;
import cd.ir.BasicBlock;
import cd.ir.Symbol;
import cd.transform.analysis.AvailableExpressionDataFlowAnalysis;

import javax.lang.model.type.PrimitiveType;
import java.util.*;

public class AvailableExpressionOptimizer {
    private Integer index = 0;
    private Ast.MethodDecl methodDecl;
    private AvailableExpressionDataFlowAnalysis analysis;

    public AvailableExpressionOptimizer(Ast.MethodDecl methodDecl) {
        this.methodDecl = methodDecl;
        this.analysis = new AvailableExpressionDataFlowAnalysis(methodDecl.cfg);
    }

    public void optimize() {
        for (BasicBlock basicBlock : methodDecl.cfg.allBlocks) {
            Set<Ast.Expr> todo = new LinkedHashSet<>();
            todo.addAll(analysis.outStateOf(basicBlock));
            while (!todo.isEmpty()) {
                Ast.Expr currExpr = todo.iterator().next();
                todo.remove(currExpr);
                for (Ast.Expr afterExpr : todo) {
                    if (afterExpr instanceof Ast.Var || afterExpr instanceof PrimitiveType) {
                        continue;
                    } else if (new EqVisitor().visit(currExpr, afterExpr)) {
                        // calculate currexpr before currexpr in temp
                        Ast.VarDecl varDecl = new Ast.VarDecl(currExpr.type.name, "temp_" + index);
                        varDecl.sym = new Symbol.VariableSymbol("temp_" + index, currExpr.type, Symbol.VariableSymbol.Kind.LOCAL);
                        methodDecl.decls().rwChildren.add(varDecl);
                        rebuildStmtListEq(basicBlock, currExpr, afterExpr);
                    } else if (new SubVisitor().visit(currExpr, afterExpr)) {
                        // calculate afterexpr before currexpr in temp
                        Ast.VarDecl varDecl = new Ast.VarDecl(afterExpr.type.name, "temp_" + index);
                        varDecl.sym = new Symbol.VariableSymbol("temp_" + index, afterExpr.type, Symbol.VariableSymbol.Kind.LOCAL);
                        methodDecl.decls().rwChildren.add(varDecl);
                        rebuildStmtListSub(basicBlock, afterExpr, currExpr, afterExpr);
                    } else if (new SubVisitor().visit(afterExpr, currExpr)) {
                        // calculate currExpr before currExpr in temp
                        Ast.VarDecl varDecl = new Ast.VarDecl(currExpr.type.name, "temp_" + index);
                        varDecl.sym = new Symbol.VariableSymbol("temp_" + index, currExpr.type, Symbol.VariableSymbol.Kind.LOCAL);
                        methodDecl.decls().rwChildren.add(varDecl);
                        rebuildStmtListSub(basicBlock, currExpr, currExpr, afterExpr);
                    }
                }
            }

        }
    }

    private void rebuildStmtListSub(BasicBlock currBlock, Ast.Expr calc, Ast.Expr currExpr, Ast.Expr afterExpr) {
        // Add new Stmt
        List<Ast.Stmt> newList = new ArrayList<>();
        for (Ast.Stmt currStmt : currBlock.stmts) {
            if (currStmt.equals(analysis.exprStmtMap.get(currExpr))) {
                Ast.Var var = new Ast.Var("temp_" + index);
                var.sym = new Symbol.VariableSymbol("temp_" + index, calc.type, Symbol.VariableSymbol.Kind.LOCAL);
                newList.add(new Ast.Assign(var, calc));
                new SubExchangeVisitor().visit(currStmt, calc);
            } else if (currStmt.equals(analysis.exprStmtMap.get(afterExpr))) {
                new SubExchangeVisitor().visit(currStmt, calc);
                index++;
            }
            newList.add(currStmt);
        }
        currBlock.stmts.clear();
        currBlock.stmts.addAll(newList);
    }

    private void rebuildStmtListEq(BasicBlock currBlock, Ast.Expr currExpr, Ast.Expr afterExpr) {
        // Add new Stmt
        List<Ast.Stmt> newList = new ArrayList<>();
        for (Ast.Stmt currStmt : currBlock.stmts) {
            if (currStmt.equals(analysis.exprStmtMap.get(currExpr))) {
                Ast.Var var = new Ast.Var("temp_" + index);
                var.sym = new Symbol.VariableSymbol("temp_" + index, currExpr.type, Symbol.VariableSymbol.Kind.LOCAL);
                newList.add(new Ast.Assign(var, currExpr));
                new ExchangeVisitor().visit(currStmt, currExpr);
            } else if (currStmt.equals(analysis.exprStmtMap.get(afterExpr))) {
                new ExchangeVisitor().visit(currStmt, afterExpr);
                index++;
            }
            newList.add(currStmt);
        }
        currBlock.stmts.clear();
        currBlock.stmts.addAll(newList);
    }

    protected class EqVisitor extends AstVisitor<Boolean, Ast.Expr> {
        //--- Equality impossible
        @Override
        protected Boolean dfltExpr(Ast.Expr ast, Ast.Expr arg) {
            return false;
        }

        //--- Equality possible
        @Override
        public Boolean binaryOp(Ast.BinaryOp ast, Ast.Expr arg) {
            if (!(arg instanceof Ast.BinaryOp)) {
                return false;
            }

            Ast.BinaryOp right = (Ast.BinaryOp) arg;

            if (!Objects.equals(ast.operator, right.operator)) {
                return false;
            }

            return visit(ast.left(), right.left()) && visit(ast.right(), right.right());
        }

        @Override
        public Boolean booleanConst(Ast.BooleanConst ast, Ast.Expr arg) {
            if (!(arg instanceof Ast.BooleanConst)) return false;

            Ast.BooleanConst right = (Ast.BooleanConst) arg;

            return ast.value && right.value;

        }

        @Override
        public Boolean intConst(Ast.IntConst ast, Ast.Expr arg) {
            if (!(arg instanceof Ast.IntConst)) return false;

            Ast.IntConst right = (Ast.IntConst) arg;

            return ast.value == right.value;

        }

        @Override
        public Boolean cast(Ast.Cast ast, Ast.Expr arg) {
            if (!(arg instanceof Ast.Cast)) {
                return false;
            }

            Ast.Cast right = (Ast.Cast) arg;

            if (!right.typeName.equals(ast.typeName)) {
                return false;
            }

            return visit(ast.arg(), right.arg());
        }

        @Override
        public Boolean unaryOp(Ast.UnaryOp ast, Ast.Expr arg) {
            if (!(arg instanceof Ast.UnaryOp)) {
                return false;
            }

            Ast.UnaryOp right = (Ast.UnaryOp) arg;

            if (!Objects.equals(ast.operator, right.operator)) {
                return false;
            }

            return visit(ast.arg(), right.arg());
        }

        @Override
        public Boolean var(Ast.Var ast, Ast.Expr arg) {
            if (!(arg instanceof Ast.Var)) return false;

            Ast.Var right = (Ast.Var) arg;

            return Objects.equals(right.sym.name, ast.sym.name);
        }
    }

    protected class SubVisitor extends AstVisitor<Boolean, Ast.Expr> {
        // Check if ARG is a subtree of AST

        //--- Equality impossible
        @Override
        protected Boolean dfltExpr(Ast.Expr ast, Ast.Expr arg) {
            return false;
        }

        //--- Equality possible
        @Override
        public Boolean binaryOp(Ast.BinaryOp ast, Ast.Expr arg) {
            if (!(arg instanceof Ast.BinaryOp)) {
                return visit(ast.left(), arg) || visit(ast.right(), arg);
            }

            Ast.BinaryOp right = (Ast.BinaryOp) arg;

            if (!Objects.equals(ast.operator, right.operator)) {
                return visit(ast.left(), arg) || visit(ast.right(), arg);
            }

            return (visit(ast.left(), right.left()) && visit(ast.right(), right.right())) || (visit(ast.left(), arg) || visit(ast.right(), arg));
        }

        @Override
        public Boolean booleanConst(Ast.BooleanConst ast, Ast.Expr arg) {
            if (!(arg instanceof Ast.BooleanConst)) {
                return false;
            }

            Ast.BooleanConst right = (Ast.BooleanConst) arg;

            return ast.value && right.value;
        }

        @Override
        public Boolean intConst(Ast.IntConst ast, Ast.Expr arg) {
            if (!(arg instanceof Ast.IntConst)) return false;

            Ast.IntConst right = (Ast.IntConst) arg;

            return ast.value == right.value;

        }

        @Override
        public Boolean cast(Ast.Cast ast, Ast.Expr arg) {
            if (!(arg instanceof Ast.Cast)) {
                return visit(ast.arg(), arg);
            }

            Ast.Cast right = (Ast.Cast) arg;

            if (!right.typeName.equals(ast.typeName)) {
                return visit(ast.arg(), arg);
            }

            return visit(ast.arg(), right.arg()) || visit(ast.arg(), arg);
        }

        @Override
        public Boolean unaryOp(Ast.UnaryOp ast, Ast.Expr arg) {
            if (!(arg instanceof Ast.UnaryOp)) {
                return visit(ast.arg(), arg);
            }

            Ast.UnaryOp right = (Ast.UnaryOp) arg;

            if (!Objects.equals(ast.operator, right.operator)) {
                return visit(ast.arg(), arg);
            }

            return visit(ast.arg(), right.arg()) || visit(ast.arg(), arg);
        }

        @Override
        public Boolean var(Ast.Var ast, Ast.Expr arg) {
            if (!(arg instanceof Ast.Var)) return false;

            Ast.Var right = (Ast.Var) arg;

            return Objects.equals(right.sym.name, ast.sym.name);
        }
    }

    protected class ExchangeVisitor extends AstVisitor<Void, Ast.Expr> {
        // Replace ARG in AST through temp_number
        @Override
        protected Void dfltStmt(Ast.Stmt ast, Ast.Expr arg) {
            for (int i = 0; i < ast.children().size(); i++) {
                if (Objects.equals(ast.children().get(i), arg)) {
                    Ast.Var var = new Ast.Var("temp_" + index);
                    var.sym = new Symbol.VariableSymbol("temp_" + index, arg.type, Symbol.VariableSymbol.Kind.LOCAL);
                    ast.children().set(i, var);
                    return null;
                }
            }
            return null;
        }
    }

    protected class SubExchangeVisitor extends AstVisitor<Void, Ast.Expr> {
        // Find and replace ARG in AST through temp_number
        @Override
        protected Void dflt(Ast ast, Ast.Expr arg) {
            for (int i = 0; i < ast.children().size(); i++) {
                if (new EqVisitor().visit(ast.children().get(i), arg)) {
                    Ast.Var var = new Ast.Var("temp_" + index);
                    var.sym = new Symbol.VariableSymbol("temp_" + index, arg.type, Symbol.VariableSymbol.Kind.LOCAL);
                    ast.children().set(i, var);
                    return null;
                }
            }
            visitChildren(ast, arg);
            return null;
        }
    }
}