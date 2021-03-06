package cd.frontend.parser;

import java.util.ArrayList;
import java.util.List;

import cd.frontend.parser.JavaliParser.ClassDeclContext;
import cd.ir.Ast;
import cd.ir.Ast.ClassDecl;
import com.sun.org.apache.bcel.internal.generic.NOP;
import org.antlr.runtime.tree.ParseTree;

public final class JavaliAstVisitor extends JavaliBaseVisitor<Ast> {

	public List<ClassDecl> classDecls = new ArrayList<>();

	@Override
	public Ast visitClassDecl(ClassDeclContext ctx) {
	    String name = ctx.Identifier().get(0).getText();
	    String parent = "Object";
	    if (ctx.Identifier().size() > 1) {
            parent = ctx.Identifier().get(1).getText(); // No Interface and no multiple inheritance so only one parent
        }

        List<Ast> children = new ArrayList<>();
	    if(ctx.memberList() != null && ctx.memberList().children != null) {
            ctx.memberList().children.forEach(child -> {
                Ast ast = visit(child);
                if (ast instanceof Ast.Seq) {
                    children.addAll(ast.rwChildren);
                } else {
                    children.add(ast);
                }
            });
        }
//        ctx.memberList().varDecl().forEach(terminalNode -> children.addAll(visit(terminalNode).rwChildren));
//        ctx.memberList().methodDecl().forEach(terminalNode -> children.add(visit(terminalNode)));

		ClassDecl decl = new ClassDecl(name, parent, children);

		classDecls.add(decl);
		return decl;
	}

    @Override
    public Ast visitMethodDecl(JavaliParser.MethodDeclContext ctx) {
        String type = "void";
        if (ctx.type() != null){
            type = ctx.type().getText();
        }

        String name = ctx.Identifier().getText();

        List<String> paramNames = new ArrayList<>();
        List<String> paramTypes = new ArrayList<>();

        if(ctx.formalParamList() != null) {
            ctx.formalParamList().Identifier().forEach(terminalNode -> paramNames.add(terminalNode.getText()));
            ctx.formalParamList().type().forEach(terminalNode -> paramTypes.add(terminalNode.getText()));
        }

        List<Ast> decls = new ArrayList<>();
        ctx.varDecl().forEach(terminalNode -> decls.addAll(visit(terminalNode).rwChildren));

        List<Ast> body = new ArrayList<>();
        ctx.stmt().forEach(terminalNode -> body.add(visit(terminalNode)));

        return new Ast.MethodDecl(type, name, paramTypes, paramNames, new Ast.Seq(decls), new Ast.Seq(body));
    }

    @Override
    public Ast visitVarDecl(JavaliParser.VarDeclContext ctx) {
	    String type = ctx.type().getText();
	    List<Ast> decls = new ArrayList<>();
	    ctx.Identifier().forEach(terminalNode -> decls.add(new Ast.VarDecl(type, terminalNode.getText())));

        return new Ast.Seq(decls);
    }

    @Override
    public Ast visitStmt(JavaliParser.StmtContext ctx) {
	    return visit(ctx.getChild(0));
    }

    @Override
    public Ast visitWriteStmt(JavaliParser.WriteStmtContext ctx) {
	    if(ctx.expr() != null){
	        return new Ast.BuiltInWrite((Ast.Expr) visit(ctx.expr()));
        }
        return new Ast.BuiltInWriteln();
    }

    @Override
    public Ast visitReturnStmt(JavaliParser.ReturnStmtContext ctx) {
	    Ast.Expr expr = null;
	    if(ctx.expr() != null){
            expr = (Ast.Expr) visit(ctx.expr());
        }
        return new Ast.ReturnStmt(expr);
    }

    @Override
    public Ast visitWhileStmt(JavaliParser.WhileStmtContext ctx) {
        return new Ast.WhileLoop((Ast.Expr) visit(ctx.expr()), visit(ctx.stmtBlock()));
    }

    @Override
    public Ast visitIfStmt(JavaliParser.IfStmtContext ctx) {
	    Ast.Expr expr = (Ast.Expr) visit(ctx.expr());
        Ast then = visit(ctx.stmtBlock(0));
        Ast otherwise = new Ast.Nop();
        if (ctx.stmtBlock(1) != null){
            otherwise = visit(ctx.stmtBlock(1));
        }
        return new Ast.IfElse(expr, then, otherwise);
    }

    @Override
    public Ast visitStmtBlock(JavaliParser.StmtBlockContext ctx) {
        List<Ast> stmts = new ArrayList<>();
        ctx.stmt().forEach(stmtContext -> stmts.add(visit(stmtContext)));

        return new Ast.Seq(stmts);
    }

    @Override
    public Ast visitMethodCallStmt(JavaliParser.MethodCallStmtContext ctx) {
        return new Ast.MethodCall((Ast.MethodCallExpr) visit(ctx.methodCallExpr()));
    }

    @Override
    public Ast visitExprBOpAdd(JavaliParser.ExprBOpAddContext ctx) {
        return new Ast.BinaryOp((Ast.Expr) visit(ctx.expr(0)), getBOpFromRepr(ctx.op.getText()), (Ast.Expr) visit(ctx.expr(1)));
    }

    @Override
    public Ast visitExprBOpComp(JavaliParser.ExprBOpCompContext ctx) {
        return new Ast.BinaryOp((Ast.Expr) visit(ctx.expr(0)), getBOpFromRepr(ctx.op.getText()), (Ast.Expr) visit(ctx.expr(1)));
    }

    @Override
    public Ast visitExprBOpAnd(JavaliParser.ExprBOpAndContext ctx) {
        return new Ast.BinaryOp((Ast.Expr) visit(ctx.expr(0)), getBOpFromRepr(ctx.op.getText()), (Ast.Expr) visit(ctx.expr(1)));
    }

    @Override
    public Ast visitExprBOpEq(JavaliParser.ExprBOpEqContext ctx) {
        return new Ast.BinaryOp((Ast.Expr) visit(ctx.expr(0)), getBOpFromRepr(ctx.op.getText()), (Ast.Expr) visit(ctx.expr(1)));
    }

    @Override
    public Ast visitExprBOpMult(JavaliParser.ExprBOpMultContext ctx) {
        return new Ast.BinaryOp((Ast.Expr) visit(ctx.expr(0)), getBOpFromRepr(ctx.op.getText()), (Ast.Expr) visit(ctx.expr(1)));
    }

    @Override
    public Ast visitExprBOpOr(JavaliParser.ExprBOpOrContext ctx) {
        return new Ast.BinaryOp((Ast.Expr) visit(ctx.expr(0)), getBOpFromRepr(ctx.op.getText()), (Ast.Expr) visit(ctx.expr(1)));
    }

    @Override
    public Ast visitExprIdentifierAccess(JavaliParser.ExprIdentifierAccessContext ctx) {
        return visit(ctx.identifierAccess());
    }

    @Override
    public Ast visitExprCast(JavaliParser.ExprCastContext ctx) {
        return new Ast.Cast((Ast.Expr) visit(ctx.expr()), ctx.referenceType().getText());
    }

    @Override
    public Ast visitExprInBrackets(JavaliParser.ExprInBracketsContext ctx) {
        return visit(ctx.expr());
    }

    @Override
    public Ast visitExprLiteral(JavaliParser.ExprLiteralContext ctx) {
        return visit(ctx.literal());
    }

    @Override
    public Ast visitExprUnaryOp(JavaliParser.ExprUnaryOpContext ctx) {
        return new Ast.UnaryOp(getUOpFromRepr(ctx.op.getText()), (Ast.Expr) visit(ctx.expr()));
    }

    @Override
    public Ast visitAssignmentStmt(JavaliParser.AssignmentStmtContext ctx) {
        Ast.Expr expr = null;
        if (ctx.expr() != null) {
            expr = (Ast.Expr) visit(ctx.expr());
        } else if (ctx.newExpr() != null) {
            expr = (Ast.Expr) visit(ctx.newExpr());
        } else if (ctx.readExpr() != null) {
            expr = (Ast.Expr) visit(ctx.readExpr());
        }
        return new Ast.Assign((Ast.Expr) visit(ctx.identifierAccess()), expr);
    }

    @Override
    public Ast visitReadExpr(JavaliParser.ReadExprContext ctx) {
        return new Ast.BuiltInRead();
    }

    @Override
    public Ast visitNewExpr(JavaliParser.NewExprContext ctx) {
	    if(ctx.expr() != null){
	        String name ;
            if (ctx.Identifier() != null){
                name = ctx.Identifier().getText();
            } else {
                name = ctx.PrimitiveType().getText();
            }

            return new Ast.NewArray(name + "[]", (Ast.Expr) visit(ctx.expr()));

        } else {

            return new Ast.NewObject(ctx.Identifier().getText());
        }
    }


    @Override
    public Ast visitVarAccess(JavaliParser.VarAccessContext ctx) {
        return new Ast.Var(ctx.Identifier().getText());
    }

    @Override
    public Ast visitThisAccess(JavaliParser.ThisAccessContext ctx) {
        return new Ast.ThisRef();
    }

    @Override
    public Ast visitFieldAccess(JavaliParser.FieldAccessContext ctx) {
        Ast.Expr expr = (Ast.Expr) visit(ctx.identifierAccess());
        return new Ast.Field(expr, ctx.Identifier().getText());
    }

    @Override
    public Ast visitArrayAccess(JavaliParser.ArrayAccessContext ctx) {
        Ast.Expr expr = (Ast.Expr) visit(ctx.expr());
        Ast.Expr array = (Ast.Expr) visit(ctx.identifierAccess());
        return new Ast.Index(array, expr);
    }

    @Override
    public Ast visitLocalMethodCall(JavaliParser.LocalMethodCallContext ctx) {
        return createMethodCallExpr(new Ast.ThisRef(),  ctx.Identifier().getText(), ctx.actualParamList());
    }

    @Override
    public Ast visitRemoteMethodCall(JavaliParser.RemoteMethodCallContext ctx) {
        Ast.Expr receiver = (Ast.Expr) visit(ctx.identifierAccess());
        return createMethodCallExpr(receiver, ctx.Identifier().getText(), ctx.actualParamList());
    }

    @Override
    public Ast visitMethodCallExpr(JavaliParser.MethodCallExprContext ctx) {
        Ast.Expr receiver = new Ast.ThisRef();
        if(ctx.identifierAccess() != null){
            receiver = (Ast.Expr) visit(ctx.identifierAccess());
        }
        String methodName = ctx.Identifier().getText();
        return createMethodCallExpr(receiver, methodName, ctx.actualParamList());
    }

    @Override
    public Ast visitLiteral(JavaliParser.LiteralContext ctx) {
        if(ctx.Boolean() != null){
            return new Ast.BooleanConst(Boolean.parseBoolean(ctx.Boolean().getText()));
        } else if (ctx.Integer() != null){
            try {
                return new Ast.IntConst(Integer.decode(ctx.Integer().getText()));
            }catch (NumberFormatException ex){
                throw new ParseFailure(ctx.Integer().getSymbol().getLine(),"Failed to parses int");
            }
        } else {
            return new Ast.NullConst();
        }
    }

// ------------ Helper Functions -----------------

    private Ast.MethodCallExpr createMethodCallExpr(Ast.Expr receiver, String methodName, JavaliParser.ActualParamListContext args){
        List<Ast.Expr> arg = new ArrayList<>();
        if(args != null) {
            args.expr().forEach(exprContext -> arg.add((Ast.Expr) visit(exprContext)));
        }
        return new Ast.MethodCallExpr(receiver, methodName, arg);
    }

    private Ast.BinaryOp.BOp getBOpFromRepr(String repr){
	    for(Ast.BinaryOp.BOp op: Ast.BinaryOp.BOp.values()){
	        if(op.repr.equals(repr)) return op;
        }
        return null;
    }

    private Ast.UnaryOp.UOp getUOpFromRepr(String repr){
        for(Ast.UnaryOp.UOp op: Ast.UnaryOp.UOp.values()){
            if(op.repr.equals(repr)) return op;
        }
        return null;
    }
}