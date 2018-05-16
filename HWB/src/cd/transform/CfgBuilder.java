package cd.transform;

import cd.ir.*;
import cd.ir.Ast.IfElse;
import cd.ir.Ast.MethodDecl;
import cd.ir.Ast.Seq;
import cd.ir.Ast.Stmt;
import cd.ir.Ast.WhileLoop;
import org.antlr.v4.runtime.misc.Triple;

import java.util.ArrayList;

public class CfgBuilder {
	
	ControlFlowGraph cfg;

	public void build(MethodDecl mdecl) {
		cfg = mdecl.cfg = new ControlFlowGraph();
		cfg.start = cfg.newBlock(); // Note: Use newBlock() to create new basic blocks
		cfg.end = cfg.newBlock(); // unique exit block to which all blocks that end with a return stmt. lead

		/* Create the definition var set, where var i is defined in stmt d_1 and d_3
		 * i: d_1, d_3
		 * j: d_2, d_4, ...
		 *
		 * Create the used var set, where var i is used in stmt d_5, d_6
		 * i: d_5, d_6
		 * j: d_7, d_8, ...
		 */
		for(Ast decl : mdecl.decls().rwChildren()){
			Ast.VarDecl varDecl =  (Ast.VarDecl) decl;
			if (varDecl.sym.type instanceof Symbol.PrimitiveTypeSymbol){
				cfg.graphVarDefinitionSet.put(varDecl.name, new ArrayList<>());
				cfg.graphVarUseSet.put(varDecl.name, new ArrayList<>());
			}
		}
		for(int i = 0; i < mdecl.argumentNames.size(); i++){
			String name = mdecl.argumentNames.get(i);
			String type = mdecl.argumentTypes.get(i);
			if(type.equals("int") || type.equals("boolean")){
				// todo: not sure if you can assume the var (param) is set...
				cfg.graphVarDefinitionSet.put(name, new ArrayList<>());
				cfg.graphVarDefinitionSet.get(name).add(0);	// Marks the variable as defined from the beginning
				cfg.graphVarUseSet.put(name, new ArrayList<>());
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

		/** Save the current stmt "d_x" in here*/
		private Integer currentStmtLabel = 0; // 0 is for parameters and before use it will be updated

		@Override
		protected BasicBlock dfltStmt(Stmt ast, BasicBlock arg) {
			if (arg == null) return null; // dead code, no need to generate anything
			arg.stmts.add(ast);
			cfg.definitionBlockMap.put(currentStmtLabel, arg.index);
			return arg;
		}

		@Override
		public BasicBlock assign(Ast.Assign ast, BasicBlock arg) {
			if (arg == null) return null; // dead code, no need to generate anything

			updateStmtLabel();

			// If assigned to a local var, no objects and arrays -> only primitive type
			if (ast.left() instanceof Ast.Var
					&& ((Ast.Var) ast.left()).sym.kind.equals(Symbol.VariableSymbol.Kind.LOCAL)
					&& ((Ast.Var) ast.left()).sym.type instanceof Symbol.PrimitiveTypeSymbol){

				// Get variable name
				String varName = ((Ast.Var) ast.left()).sym.name;

				// Add to block definition set (List<String>)
				arg.blockDefinitionSet.add(currentStmtLabel);

				// Add to graphVarDefinitionSet (hashMap<varName, arrayList<definition_label>>)
				cfg.graphVarDefinitionSet.get(varName).add(currentStmtLabel);

				// Add to definitionVarMap (hashMap<definition_label, varName>)
				cfg.definitionVarMap.put(currentStmtLabel, varName);

				if(!arg.use.contains(varName) && !arg.def.contains(varName)){
					arg.def.add(varName);
				}
			}

			// Visit to add vars to global use set
			visit(ast.right(), arg);

			return super.assign(ast, arg);
		}

		@Override
		public BasicBlock builtInWrite(Ast.BuiltInWrite ast, BasicBlock arg) {
			if (arg == null) return null; // dead code, no need to generate anything

			updateStmtLabel();

			// Visit to add vars to global use set
			if(ast.arg() != null) {
				visit(ast.arg(), arg);
			}

			return super.builtInWrite(ast, arg);
		}

		@Override
		public BasicBlock builtInWriteln(Ast.BuiltInWriteln ast, BasicBlock arg) {
			if (arg == null) return null; // dead code, no need to generate anything

			updateStmtLabel(); // needed for index consistency of arg.stmts

			return super.builtInWriteln(ast, arg);
		}

		@Override
		public BasicBlock methodCall(Ast.MethodCall ast, BasicBlock arg) {
			if (arg == null) return null; // dead code, no need to generate anything

			updateStmtLabel();

			// Visit to add vars to global use set
			ast.getMethodCallExpr().argumentsWithoutReceiver().forEach(parameter -> visit(parameter, arg));

			return super.methodCall(ast, arg);
		}

		@Override
		public BasicBlock methodCall(Ast.MethodCallExpr ast, BasicBlock arg) {
			// no updateStmtLabel because it is no stmt it is an expression and part of a stmt!

			// Visit to add vars to global use set
			ast.argumentsWithoutReceiver().forEach(parameter -> visit(parameter, arg));

			return super.methodCall(ast, arg);
		}

		// Create the use set (only the right side of an assign statement will reach var) and add to graphVarUseSet
        @Override
        public BasicBlock var(Ast.Var ast, BasicBlock arg) {
			if (arg == null) return null; // dead code, no need to generate anything

			if(ast.sym.type instanceof Symbol.PrimitiveTypeSymbol
					&& !(ast.sym.kind.equals(Symbol.VariableSymbol.Kind.FIELD))){
            	if(!arg.def.contains(ast.sym.name) && !arg.use.contains(ast.sym.name)){
					arg.use.add(ast.sym.name);
				}
				cfg.graphVarUseSet.get(ast.sym.name).add(currentStmtLabel);
			}
		    return null;
        }

        @Override
		public BasicBlock ifElse(IfElse ast, BasicBlock arg) {
			if (arg == null) return null; // dead code, no need to generate anything

			updateStmtLabel();

			// Visit to add vars to global use set
			visit(ast.condition(), arg);
			cfg.definitionBlockMap.put(currentStmtLabel, arg.index);

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

			updateStmtLabel();

			// Visit to add vars to global use set
			visit(ast.condition(), arg);
			cfg.definitionBlockMap.put(currentStmtLabel, arg.index);

			BasicBlock cond = cfg.join(arg);
			cfg.terminateInCondition(cond, ast.condition());
			BasicBlock body = visit(ast.body(), cond.trueSuccessor());
			if (body != null) cfg.connect(body, cond);
			return cond.falseSuccessor();		
		}
		
		@Override
		public BasicBlock returnStmt(Ast.ReturnStmt ast, BasicBlock arg) {
			if (arg == null) return null; // dead code, no need to generate anything

			updateStmtLabel();

			// Visit to add vars to global use set
			if(ast.arg() != null){
				visit(ast.arg(), arg);
			}

			arg.stmts.add(ast);
			cfg.definitionBlockMap.put(currentStmtLabel, arg.index);
			cfg.connect(arg, cfg.end);
			return null; // null means that this block leads nowhere else 
		}

		/**
		 * Method which updates the definition label with the correct number
		 * @return nothing
		 */
		private void updateStmtLabel(){
			currentStmtLabel++;
		}



	}

}
