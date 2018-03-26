package cd.frontend.parser;

import java.util.ArrayList;
import java.util.List;

import cd.frontend.parser.JavaliParser.ClassDeclContext;
import cd.ir.Ast;
import cd.ir.Ast.ClassDecl;
import org.antlr.v4.runtime.tree.TerminalNode;

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
        ctx.memberList().varDecl().forEach(terminalNode -> children.addAll(visit(terminalNode).rwChildren));
        ctx.memberList().methodDecl().forEach(terminalNode -> children.add(visit(terminalNode)));

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

        Ast.MethodDecl decl = new Ast.MethodDecl(type, name, paramTypes, paramNames, new Ast.Seq(decls), new Ast.Seq(body));

        return decl;
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
        return new Ast.ReturnStmt((Ast.Expr) visit(ctx.expr()));
    }

    @Override
    public Ast visitWhileStmt(JavaliParser.WhileStmtContext ctx) {
        return new Ast.WhileLoop((Ast.Expr) visit(ctx.expr()), visit(ctx.stmtBlock()));
    }

    @Override
    public Ast visitIfStmt(JavaliParser.IfStmtContext ctx) {
        return new Ast.IfElse((Ast.Expr) visit(ctx.expr()), visit(ctx.stmtBlock(0)), visit(ctx.stmtBlock(1)));
    }

    @Override
    public Ast visitMethodCallStmt(JavaliParser.MethodCallStmtContext ctx) {
        return visit(ctx.methodCallExpr());
    }

    @Override
    public Ast visitExpr(JavaliParser.ExprContext ctx) {
        System.out.println(ctx.start.getText());

	    //return new Ast.Expr();
        //return new Ast.ArgExpr();
        //return new Ast.LeftRightExpr();
        return null;
    }

}
