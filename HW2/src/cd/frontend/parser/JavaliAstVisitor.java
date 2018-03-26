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
        for(JavaliParser.VarDeclContext context : ctx.varDecl()){
            Ast.Seq varDecls = (Ast.Seq) visit(context);
            decls.addAll(varDecls.rwChildren());
        }

        List<Ast> body = new ArrayList<>();
        ctx.stmt().forEach(terminalNode -> body.add(visit(terminalNode)));

        Ast.MethodDecl decl = new Ast.MethodDecl(type, name, paramTypes, paramNames, new Ast.Seq(decls), new Ast.Seq(body));

        return decl;
    }

    @Override
    public Ast visitVarDecl(JavaliParser.VarDeclContext ctx) {
	    String type = ctx.type().getText();
	    List<Ast> delcls = new ArrayList<>();
	    for (TerminalNode node: ctx.Identifier()){
            delcls.add(new Ast.VarDecl(type,node.getText()));
        }
        Ast.Seq seq = new Ast.Seq(delcls);
        return seq;
    }
}
