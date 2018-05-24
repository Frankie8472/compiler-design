package cd.backend.codegen;

import static cd.Config.SCANF;
import static cd.backend.codegen.AssemblyEmitter.constant;
import static cd.backend.codegen.RegisterManager.STACK_REG;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.text.ElementIterator;

import cd.Config;
import cd.ToDoException;
import cd.backend.codegen.RegisterManager.Register;
import cd.ir.Ast.BinaryOp;
import cd.ir.Ast.BinaryOp.BOp;
import cd.ir.Symbol.MethodSymbol;
import cd.ir.Symbol.VariableSymbol;
import cd.ir.Ast.BooleanConst;
import cd.ir.Ast.BuiltInRead;
import cd.ir.Ast.Cast;
import cd.ir.Ast.Expr;
import cd.ir.Ast.Field;
import cd.ir.Ast.Index;
import cd.ir.Ast.IntConst;
import cd.ir.Ast.MethodCallExpr;
import cd.ir.Ast.NewArray;
import cd.ir.Ast.NewObject;
import cd.ir.Ast.NullConst;
import cd.ir.Ast.ThisRef;
import cd.ir.Ast.UnaryOp;
import cd.ir.Ast.Var;
import cd.ir.ExprVisitor;
import cd.util.debug.AstOneLine;

/**
 * Generates code to evaluate expressions. After emitting the code, returns a
 * String which indicates the register where the result can be found.
 */
class AddressExprGenerator extends ExprGenerator {
	protected final AstCodeGenerator cg;

	AddressExprGenerator(AstCodeGenerator astCodeGenerator) {
		super(astCodeGenerator);
		cg = astCodeGenerator;
	}

	public Register gen(Expr ast) {
		return visit(ast, null);
	}

	@Override
	public Register index(Index ast, Void arg) {
		
		//Save registers on stack
				Register savedReg1 = cg.pushRegister(Register.EAX);
				Register savedReg2 = cg.pushRegister(Register.EBX);
				Register savedReg3 = cg.pushRegister(Register.ECX);
				Register savedReg4 = cg.pushRegister(Register.EDX);
				Register savedReg5 = cg.pushRegister(Register.ESI);
				Register savedReg6 = cg.pushRegister(Register.EDI);
				
		/* Changed this line */  Register array = cg.eg.gen(ast.left());	//address of array
		/* Changed this line */  Register index = cg.eg.gen(ast.right());
		
		//check whether array is null pointer
				String label = cg.emit.uniqueLabel();
				cg.emit.emit("cmpl", constant(0), array);
				cg.emit.emit("jne", label);
				//null pointer: exit with code 4
				cg.emit.emit("and", -16, STACK_REG);
				cg.emit.emit("subl", constant(12), RegisterManager.STACK_REG);
				cg.emit.emit("pushl", constant(4));
				cg.emit.emit("call", Config.EXIT);

				cg.emit.emitLabel(label);
				
				//check whether index is larger than array size or negative
				String exitLabel = cg.emit.uniqueLabel();
				String okayLabel = cg.emit.uniqueLabel();
				Register arraySize = cg.rm.getRegister();
				cg.emit.emit("movl", AssemblyEmitter.registerOffset(Config.SIZEOF_PTR, array), arraySize);
				cg.emit.emit("cmpl", arraySize, index);	// index >= arraySize
				cg.rm.releaseRegister(arraySize);
				cg.emit.emit("jge", exitLabel);
				cg.emit.emit("cmpl", constant(0), index);	// index < 0
				cg.emit.emit("jl", exitLabel);
				cg.emit.emit("jmp", okayLabel);
		
				//index not in bounds: exit with code 3
				cg.emit.emitLabel(exitLabel);
				cg.emit.emit("and", -16, STACK_REG);
				cg.emit.emit("subl", constant(12), RegisterManager.STACK_REG);
				cg.emit.emit("pushl", constant(3));
				cg.emit.emit("call", Config.EXIT);
				
				//index is in bounds: continue
				cg.emit.emitLabel(okayLabel);
				//access array element
				cg.emit.emit("addl", constant(2), index);
				cg.emit.emit("imul", constant(Config.SIZEOF_PTR), index);	// could be done by left-shifting
				cg.emit.emit("addl", array, index);		// address of element is in index
				
		/* Changed this line */  cg.emit.emitMove(index, array);
		cg.rm.releaseRegister(index);
		
		cg.emit.emitMove(array, "tempValue1");
		cg.rm.releaseRegister(array);

		cg.popRegister(savedReg6);
		cg.popRegister(savedReg5);
		cg.popRegister(savedReg4);
		cg.popRegister(savedReg3);
		cg.popRegister(savedReg2);
		cg.popRegister(savedReg1);
		
		Register returnRegister = cg.rm.getRegister();
		cg.emit.emitMove("tempValue1", returnRegister);
		
		return returnRegister;
		
	}
	
	/*@Override
	public void emitLoadFromAddressToRegister(int offset, Register address, Register register) {
		cg.emit.emitMove(address, register);
	}*/

	@Override
	public Register field(Field ast, Void arg) {
		int offset = ast.sym.offset;
		
		/* Changed this line */  Register register = cg.eg.visit(ast.arg(), arg);
		
		//check whether object is null pointer
		String label = cg.emit.uniqueLabel();
		cg.emit.emit("cmpl", constant(0), register);
		cg.emit.emit("jne", label);
		//null pointer: exit with code 4
		cg.emit.emit("and", -16, STACK_REG);
		cg.emit.emit("subl", constant(12), RegisterManager.STACK_REG);
		cg.emit.emit("pushl", constant(4));
		cg.emit.emit("call", Config.EXIT);
		cg.emit.emitLabel(label);
		
		/* Changed this line */ cg.emit.emit("leal", AssemblyEmitter.registerOffset(offset, register), register);

		return register;
	}

	
	@Override
	public Register var(Var ast, Void arg) {
		{
			Register register = cg.rm.getRegister();

			if(ast.sym.kind == VariableSymbol.Kind.FIELD)
			{
				cg.emit.emitMove("this_ref", register);
				/* Changed this line */  cg.emit.emit("leal", AssemblyEmitter.registerOffset(ast.sym.offset, register), register);
			}
			else
			{
				/* Changed this line */  cg.emit.emit("leal", AssemblyEmitter.registerOffset(ast.sym.offset, RegisterManager.BASE_REG), register);
			}
			
			return register;
		}
	}

}
