package cd.frontend.parser;

import java.util.ArrayList;
import java.util.List;

import cd.ToDoException;
import cd.frontend.parser.JavaliParser.ClassDeclContext;
import cd.ir.Ast;
import cd.ir.Ast.ClassDecl;

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
        for(JavaliParser.VarDeclContext context : ctx.memberList().varDecl()){
            children.add(visit(context));
        }

	    for(JavaliParser.MethodDeclContext context : ctx.memberList().methodDecl()){
	        children.add(visit(context));
        }

		ClassDecl decl = new ClassDecl(name, parent, children);

		classDecls.add(decl);
		return decl;
	}

    @Override
    public Ast visitMethodDecl(JavaliParser.MethodDeclContext ctx) {
        String type = "void";
        if (ctx.type() != null){
            if(ctx.type().PrimitiveType() != null){
                type = ctx.type().PrimitiveType().getText();
            } else if(ctx.type().referenceType().Identifier() != null){
                type = ctx.type().referenceType().Identifier().getText();
            }
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
            decls.add(visit(context));
        }

        List<Ast> body = new ArrayList<>();
        for(JavaliParser.StmtContext context : ctx.stmt()){
            body.add(visit(context));
        }


        Ast.MethodDecl decl = new Ast.MethodDecl(type, name, paramTypes, paramNames,new Ast.Seq(decls), new Ast.Seq(body));

        return decl;
    }
}
