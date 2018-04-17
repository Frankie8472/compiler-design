package cd.backend.codegen;

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

public class RegisterCounter extends ExprVisitor<Integer,Void>{
	
	RegisterCounter(){
		//default Constructor
	}
	
	public Integer gen(Expr ast) {
		return visit(ast,  null);
	}
	
	@Override
	public Integer visit(Expr ast, Void arg) {
		return super.visit(ast, null);
	}
	
    @Override
    public Integer binaryOp(BinaryOp ast, Void arg) {
    	Integer needR = visit(ast.right(), arg);
    	Integer needL = visit(ast.left(), arg);
    	if(needR > needL) {
    		return needR;
    	}
    	else if (needR < needL) {
    		return needL;
    	}
    	else {
    		return needR + 1;
    	}
        
    }
 
    @Override
    public Integer booleanConst(BooleanConst ast, Void arg) {
        {
            throw new RuntimeException("Not required");
        }
    }
 
    @Override
    public Integer builtInRead(BuiltInRead ast, Void arg) {
    	
        return 1;
    }
 
    @Override
    public Integer cast(Cast ast, Void arg) {
        {
            throw new RuntimeException("Not required");
        }
    }
 
    @Override
    public Integer index(Index ast, Void arg) {
        {
            throw new RuntimeException("Not required");
        }
    }
 
    @Override
    public Integer intConst(IntConst ast, Void arg) {

        return 1;
    }
 
    @Override
    public Integer field(Field ast, Void arg) {
        {
            throw new RuntimeException("Not required");
        }
    }
 
    @Override
    public Integer newArray(NewArray ast, Void arg) {
        {
            throw new RuntimeException("Not required");
        }
    }
 
    @Override
    public Integer newObject(NewObject ast, Void arg) {
        {
            throw new RuntimeException("Not required");
        }
    }
 
    @Override
    public Integer nullConst(NullConst ast, Void arg) {
        {
            throw new RuntimeException("Not required");
        }
    }
 
    @Override
    public Integer thisRef(ThisRef ast, Void arg) {
        {
            throw new RuntimeException("Not required");
        }
    }
 
    @Override
    public Integer unaryOp(UnaryOp ast, Void arg) {
        
        return visitChildren(ast,arg);
        
    }
    
    @Override
    public Integer var(Var ast, Void arg) {

        return 1;
    }

}
