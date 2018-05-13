package cd.transform;

import cd.ir.*;
import cd.ir.Ast.IfElse;
import cd.ir.Ast.MethodDecl;
import cd.ir.Ast.Seq;
import cd.ir.Ast.Stmt;
import cd.ir.Ast.WhileLoop;

import java.util.ArrayList;

public class CfgBuilder {
	
	ControlFlowGraph cfg;
	private Integer definition_counter = 0;

	public void build(MethodDecl mdecl) {
		cfg = mdecl.cfg = new ControlFlowGraph();
		cfg.start = cfg.newBlock(); // Note: Use newBlock() to create new basic blocks
		cfg.end = cfg.newBlock(); // unique exit block to which all blocks that end with a return stmt. lead

		// made by me
		for(Ast decl : mdecl.decls().rwChildren()){
			Ast.VarDecl varDecl =  (Ast.VarDecl) decl;
			if (varDecl.sym.type instanceof Symbol.PrimitiveTypeSymbol.ArrayTypeSymbol){
				cfg.definition_set.put(varDecl.name + "_0", new ArrayList<>());
			} else {
				cfg.definition_set.put(varDecl.name, new ArrayList<>());
			}
		}

		{
			BasicBlock lastInBody = new Visitor().visit(mdecl.body(), cfg.start);
			if (lastInBody != null) cfg.connect(lastInBody, cfg.end);
		}
		
		// CFG and AST are not synchronized, only use CFG from now on
		mdecl.setBody(null);
	}
	
	protected class Visitor extends AstVisitor<BasicBlock, BasicBlock> {

		@Override
		protected BasicBlock dfltStmt(Stmt ast, BasicBlock arg) {
			if (arg == null) return null; // dead code, no need to generate anything

			// my doing
			if (ast instanceof Ast.Assign){
				String definition_name = "d_" + definition_counter.toString();
				definition_counter++;

				if (((Ast.Assign) ast).left() instanceof Ast.Var){
					String varName = ((Ast.Var) ((Ast.Assign) ast).left()).sym.name;
					cfg.definition_set.get(varName).add(definition_name);
					arg.definition_set.add(definition_name);
					cfg.definition_map.put(definition_name, varName);
				} else {
					// todo: array
					// problem, getting array index and what if it is an input? runtime???
                    String varName = ((Ast.Var) ((Ast.Index) ((Ast.Assign) ast).left()).left()).sym.name + "_0";
                    cfg.definition_set.get(varName).add(definition_name);
                    arg.definition_set.add(definition_name);
                    cfg.definition_map.put(definition_name, varName);
				}
			}
			// -----

			arg.stmts.add(ast);
			return arg;
		}
		
		@Override
		public BasicBlock ifElse(IfElse ast, BasicBlock arg) {
			if (arg == null) return null; // dead code, no need to generate anything
			cfg.terminateInCondition(arg, ast.condition());			
			BasicBlock then = visit(ast.then(), arg.trueSuccessor());
			BasicBlock otherwise = visit(ast.otherwise(), arg.falseSuccessor());
			if (then != null && otherwise != null) { 
				return cfg.join(then, otherwise);
			} else if (then != null) {
				BasicBlock newBlock = cfg.newBlock();
				cfg.connect(then, newBlock);
				return newBlock;
			} else if (otherwise != null) {
				BasicBlock newBlock = cfg.newBlock();
				cfg.connect(otherwise, newBlock);
				return newBlock;
			} else {
				return null;
			}
		}

		@Override
		public BasicBlock seq(Seq ast, BasicBlock arg_) {
			BasicBlock arg = arg_;
			for (Ast child : ast.children())
				arg = this.visit(child, arg);
			return arg;
		}

		@Override
		public BasicBlock whileLoop(WhileLoop ast, BasicBlock arg) {
			if (arg == null) return null; // dead code, no need to generate anything
			BasicBlock cond = cfg.join(arg);
			cfg.terminateInCondition(cond, ast.condition());
			BasicBlock body = visit(ast.body(), cond.trueSuccessor());
			if (body != null) cfg.connect(body, cond);
			return cond.falseSuccessor();		
		}
		
		@Override
		public BasicBlock returnStmt(Ast.ReturnStmt ast, BasicBlock arg) {
			if (arg == null) return null; // dead code, no need to generate anything
			arg.stmts.add(ast);
			cfg.connect(arg, cfg.end);
			return null; // null means that this block leads nowhere else 
		}
		
	}

}
