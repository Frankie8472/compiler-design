

package cd.backend.codegen;
 
import cd.Config;
import cd.ToDoException;
import cd.backend.codegen.RegisterManager.Register;
import cd.ir.Ast;
import cd.ir.Ast.BinaryOp;
import cd.ir.Ast.BooleanConst;
import cd.ir.Ast.BuiltInRead;
import cd.ir.Ast.Cast;
import cd.ir.Ast.Expr;
import cd.ir.Ast.Field;
import cd.ir.Ast.Index;
import cd.ir.Ast.IntConst;
import cd.ir.Ast.NewArray;
import cd.ir.Ast.NewObject;
import cd.ir.Ast.NullConst;
import cd.ir.Ast.ThisRef;
import cd.ir.Ast.UnaryOp;
import cd.ir.Ast.Var;
import cd.ir.ExprVisitor;
import cd.util.debug.AstOneLine;

import java.util.Base64;
import java.util.HashMap;
 
/**
 * Generates code to evaluate expressions. After emitting the code, returns a
 * String which indicates the register where the result can be found.
 */
class ExprGenerator extends ExprVisitor<Register, Void> {
    protected final AstCodeGenerator cg;

    boolean test = ToDoException.test;
    //Hashmap from Var-Name to its Offset on stack
    public static HashMap<String, Integer> VarSet = new HashMap<>();
    //offset for stack
    public static int offset = -4;
    Register basereg = Register.EBP;
	Register stackreg = Register.ESP;
 
    ExprGenerator(AstCodeGenerator astCodeGenerator) {
        cg = astCodeGenerator;
    }
 
    public Register gen(Expr ast) {
        return visit(ast, null);
    }
 
    @Override
    public Register visit(Expr ast, Void arg) {
        try {
            cg.emit.increaseIndent("Emitting " + AstOneLine.toString(ast));
            return super.visit(ast, null);
        } finally {
            cg.emit.decreaseIndent();
        }
 
    }
    
    public void addToVarSet(String name) {
    	VarSet.put(name, offset);
    	cg.emit.emit("subl", "$4", stackreg);
    	offset-=4;
    }
 
    @Override
    public Register binaryOp(BinaryOp ast, Void arg) {
    	
        if(test) cg.emit.emitComment("start of BinaryOp");
        
        //lets have a look which side uses less registers
        Integer needR = cg.rc.visit(ast.right(), arg);
        Integer needL = cg.rc.visit(ast.left(), arg);
        Register first, second;
        //first: to return --> uses more regs
        //second to release --> uses less regs
        if (needR < needL) {
        	cg.emit.emitComment("available regs: " + cg.rm.availableRegisters());
        	first = cg.eg.visit(ast.left(), arg);
        	cg.emit.emitComment("available regs: " + cg.rm.availableRegisters());
            second = cg.eg.visit(ast.right(), arg);
        } else {
	        first= cg.eg.visit(ast.right(), arg);
	        second= cg.eg.visit(ast.left(), arg);
        }
       
 
        String Op;
        if(test) {
	        cg.emit.emitComment(ast.right().toString());
	        cg.emit.emitComment(ast.left().toString());
        }
        switch(ast.operator) {
        case B_TIMES:  	Op = "imul";
                        break;
        case B_DIV:     Op = "idiv";
                        break;
        case B_PLUS:    Op = "addl";
                        break;
        case B_MINUS:   Op = "subl";
                        break;
        default:        Op = "Invalid Operation";
                        break;
        }
        cg.emit.emit(Op, second, first); //so we save everything in the right register
        cg.rm.releaseRegister(second);
        return first;
        
    }
 
    @Override
    public Register booleanConst(BooleanConst ast, Void arg) {
        {
            throw new RuntimeException("Not required");
        }
    }
 
    @Override
    public Register builtInRead(BuiltInRead ast, Void arg) {
        Register temp = cg.rm.getRegister();
        cg.emit.emit("subl", "$4", stackreg);
        cg.emit.emit("pushl", stackreg);

        cg.emit.emit("pushl", "$STRING");
        cg.emit.emit("call", Config.SCANF);
        cg.emit.emit("addl", "$8", stackreg);
        cg.emit.emit("popl", temp);
        return temp;
    }
 
    @Override
    public Register cast(Cast ast, Void arg) {
        {
            throw new RuntimeException("Not required");
        }
    }
 
    @Override
    public Register index(Index ast, Void arg) {
        {
            throw new RuntimeException("Not required");
        }
    }
 
    @Override
    public Register intConst(IntConst ast, Void arg) {
        String value = "$" + (ast.value);
        Register temp = cg.rm.getRegister();

        //set temp to 0
        cg.emit.emit("xorl", temp, temp);
        cg.emit.emit("addl", value, temp);
        return temp;
    }
 
    @Override
    public Register field(Field ast, Void arg) {
        {
            throw new RuntimeException("Not required");
        }
    }
 
    @Override
    public Register newArray(NewArray ast, Void arg) {
        {
            throw new RuntimeException("Not required");
        }
    }
 
    @Override
    public Register newObject(NewObject ast, Void arg) {
        {
            throw new RuntimeException("Not required");
        }
    }
 
    @Override
    public Register nullConst(NullConst ast, Void arg) {
        {
            throw new RuntimeException("Not required");
        }
    }
 
    @Override
    public Register thisRef(ThisRef ast, Void arg) {
        {
            throw new RuntimeException("Not required");
        }
    }
 
    @Override
    public Register unaryOp(UnaryOp ast, Void arg) {
        if(test) cg.emit.emitComment("start of UnaryOp");
        Register expr = cg.eg.visit(ast.arg(), arg);
        //TODO is switch working?
        switch(ast.operator) {
        case U_BOOL_NOT:cg.emit.emit("notl", expr);                
                        break;
        case U_MINUS:   cg.emit.emit("negl", expr);
                        break;
        }
        return expr;
        
    }
    
    @Override
    public Register var(Var ast, Void arg) {
        Register temp = cg.rm.getRegister();
        if (test) cg.emit.emitComment("" + VarSet.containsKey(ast.name));
        cg.emit.emitLoad(VarSet.get(ast.name), basereg, temp);
        return temp;
    }
 
}

