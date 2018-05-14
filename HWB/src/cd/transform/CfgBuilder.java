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

		/* Create the definition set
		 * i: d_1, d_3
		 * j: d_2, d_4, ...
		 */
		for(Ast decl : mdecl.decls().rwChildren()){
			Ast.VarDecl varDecl =  (Ast.VarDecl) decl;
			if (varDecl.sym.type instanceof Symbol.PrimitiveTypeSymbol){
				cfg.definition_set.put(varDecl.name, new ArrayList<>());
			}
		}

		// Visit the body of the method for block creation
		BasicBlock lastInBody = new Visitor().visit(mdecl.body(), cfg.start);
		if (lastInBody != null) {cfg.connect(lastInBody, cfg.end);}

		// CFG and AST are not synchronized, only use CFG from now on
		mdecl.setBody(null);
	}

	// Visitor for block- and set creation (gen, kill, use, def)
	protected class Visitor extends AstVisitor<BasicBlock, BasicBlock> {

		/**
		 * dfltStmt is a function, only called if the assign-method is called
		 * @param ast - The ast with the assign stmt on it
		 * @param arg - The basic block you are in right now
		 * @return A basic block or null, not sure what to return right now todo
		 */
		@Override
		protected BasicBlock dfltStmt(Stmt ast, BasicBlock arg) {
			if (arg == null) return null; // dead code, no need to generate anything

			// ast is already proved to be an AssignStmt
			Ast.Assign assignStmt = (Ast.Assign) ast;

			// Get the definition label with the correct number
			String definition_label = "d_" + definition_counter.toString();
			definition_counter++;

			// If assigned to a local var, no objects and arrays -> only primitive type
			if (assignStmt.left() instanceof Ast.Var
					&& ((Ast.Var) assignStmt.left()).sym.kind.equals(Symbol.VariableSymbol.Kind.LOCAL)
					&& ((Ast.Var) assignStmt.left()).sym.type instanceof Symbol.PrimitiveTypeSymbol){

				// Get variable name
				String varName = ((Ast.Var) assignStmt.left()).sym.name;

				// Add to block definition set (List<String>)
				arg.definition_set.add(definition_label);

				// Add to definition_set (hashmap<varName, List<definition_label>>)
				cfg.definition_set.get(varName).add(definition_label);

				// Add to definition_map (hashmap<definition_label, varName>)
				cfg.definition_map.put(definition_label, varName);


				if(!arg.use.contains(varName) && !arg.def.contains(varName)){
					arg.def.add(varName);
				}
			}
			visit(((Ast.Assign) ast).right(), arg);

			arg.stmts.add(ast);
			return arg;
		}

		// Create the use set (only the right side of an assign statement will reach var)
        @Override
        public BasicBlock var(Ast.Var ast, BasicBlock arg) {
            if(!(ast.sym.type instanceof Symbol.ArrayTypeSymbol)
					&& !arg.def.contains(ast.sym.name)
					&& !arg.use.contains(ast.sym.name)){
                arg.use.add(ast.sym.name);
            }
		    return null;
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
