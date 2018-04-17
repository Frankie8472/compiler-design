

package cd.backend.codegen;

import java.util.HashMap;

import cd.Config;
import cd.ToDoException;
import cd.backend.codegen.RegisterManager.Register;
import cd.ir.Ast;
import cd.ir.Ast.Assign;
import cd.ir.Ast.BuiltInWrite;
import cd.ir.Ast.BuiltInWriteln;
import cd.ir.Ast.Expr;
import cd.ir.Ast.IfElse;
import cd.ir.Ast.MethodCall;
import cd.ir.Ast.MethodDecl;
import cd.ir.Ast.Var;
import cd.ir.Ast.WhileLoop;
import cd.ir.AstVisitor;
import cd.util.debug.AstOneLine;

/**
 * Generates code to process statements and declarations.
 */
class StmtGenerator extends AstVisitor<Register, Void> {
    protected final AstCodeGenerator cg;
    //public static HashMap<Ast.Var, Integer> VarSet = ExprGenerator.VarSet;
    
    //Register Variables
	Register basereg = Register.EBP;
	Register stackreg = Register.ESP;

    StmtGenerator(AstCodeGenerator astCodeGenerator) {
        cg = astCodeGenerator;
    }

    public void gen(Ast ast) {
        visit(ast, null);
    }

    @Override
    public Register visit(Ast ast, Void arg) {
        try {
            cg.emit.increaseIndent("Emitting " + AstOneLine.toString(ast));
            return super.visit(ast, arg);
        } finally {
            cg.emit.decreaseIndent();
        }
    }

    @Override
    public Register methodCall(MethodCall ast, Void dummy) {
        {
            throw new RuntimeException("Not required");
        }
    }

    @Override
    public Register methodDecl(MethodDecl ast, Void arg) {
        //TODO methodDecl

    	
    	//method prep
    	cg.emit.emitLabel(ast.name);
    	cg.emit.emit("pushl", basereg);
    	cg.emit.emitMove(stackreg, basereg);
    	//check all the children
        cg.sg.visitChildren(ast.body(), arg);
        
        //restore bp and stuff
        cg.emit.emitMove(basereg, stackreg);
        cg.emit.emit("popl", basereg);
        cg.emit.emitMove("$0", RegisterManager.Register.EAX);
        cg.emit.emitRaw("ret");
        
        //Epilogue

        return null;
    }

    @Override
    public Register ifElse(IfElse ast, Void arg) {
        {
            throw new RuntimeException("Not required");
        }
    }

    @Override
    public Register whileLoop(WhileLoop ast, Void arg) {
        {
            throw new RuntimeException("Not required");
        }
    }
    


    @Override
    public Register assign(Assign ast, Void arg) {
        Register right = cg.eg.visit(ast.right(), arg);

        Ast.Var variable = (Ast.Var) ast.left();       
        if(!ExprGenerator.VarSet.containsKey(variable.name)) {
        	cg.eg.addToVarSet(variable.name);
        	cg.emit.emitComment("now in assign" + ExprGenerator.VarSet.containsKey(variable.name) + "name: " + variable.name);
        } 
        cg.emit.emitStore(right, ExprGenerator.VarSet.get(variable.name), basereg);
        cg.rm.releaseRegister(right);
        return null;
    }

    @Override
    public Register builtInWrite(BuiltInWrite ast, Void arg) {
        //TODO builtInWrite
    	Ast toWrite = ast.arg();
    	String out;
    	if(toWrite instanceof Ast.IntConst) {
    		out = "$" + ((Ast.IntConst) toWrite).value;
    	} else {
    		out = (cg.eg.visit((Ast.Expr) toWrite, arg)).toString();
    	} 
    	cg.emit.emit("subl", "$0", stackreg);
    	cg.emit.emit("pushl", out);
    	cg.emit.emit("pushl", "$STRING");
    	cg.emit.emit("call", Config.PRINTF);
    	cg.emit.emit("addl", "$4", stackreg);
        return null;
    }
    

    @Override
    public Register builtInWriteln(BuiltInWriteln ast, Void arg) {
        //TODO enough space on stack, Data segment
    	cg.emit.emit("subl", "$4", stackreg);
    	cg.emit.emit("pushl", "$STR_LN");
    	cg.emit.emit("call", Config.PRINTF);
    	cg.emit.emit("addl", "$4", stackreg);
        return null;
    }

}

