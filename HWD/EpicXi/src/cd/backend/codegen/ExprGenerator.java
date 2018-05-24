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
class ExprGenerator extends ExprVisitor<Register, Void> {
	protected final AstCodeGenerator cg;

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

	@Override
	public Register binaryOp(BinaryOp ast, Void arg) {
		{
			//Save registers on stack
			Register savedReg1 = cg.pushRegister(Register.EAX);
			Register savedReg2 = cg.pushRegister(Register.EBX);
			Register savedReg3 = cg.pushRegister(Register.ECX);
			Register savedReg4 = cg.pushRegister(Register.EDX);
			Register savedReg5 = cg.pushRegister(Register.ESI);
			Register savedReg6 = cg.pushRegister(Register.EDI);
			
			cg.rm.getRegister(Register.EAX);

			int leftRN = cg.rnv.calc(ast.left());
			int rightRN = cg.rnv.calc(ast.right());

			Register leftReg, rightReg;
			if (leftRN > rightRN) {
				leftReg = gen(ast.left());
				rightReg = gen(ast.right());
			} else {
				rightReg = gen(ast.right());
				leftReg = gen(ast.left());
			}

			cg.debug("Binary Op: %s (%s,%s)", ast, leftReg, rightReg);

			switch (ast.operator) {
			case B_TIMES:
				cg.emit.emit("imul", rightReg, leftReg);
				break;
			case B_PLUS:
				cg.emit.emit("add", rightReg, leftReg);
				break;
			case B_MINUS:
				cg.emit.emit("sub", rightReg, leftReg);
				break;
			case B_DIV:
			case B_MOD:
				
				//Division by zero
				String okLabel = cg.emit.uniqueLabel();
				
				cg.emit.emit("cmpl", constant(0), rightReg);
				cg.emit.emit("jne", okLabel);
				
				cg.emit.emit("and", -16, STACK_REG);
				cg.emit.emit("subl", constant(12), RegisterManager.STACK_REG);
				cg.emit.emit("pushl", constant(7));
				cg.emit.emit("call", Config.EXIT);
				cg.emit.emitLabel(okLabel);
				
				// Save EAX, EBX, and EDX to the stack if they are not used
				// in this subtree (but are used elsewhere). We will be
				// changing them.
				List<Register> dontBother = Arrays.asList(rightReg, leftReg);
				Register[] affected = { Register.EAX, Register.EBX, Register.EDX };

				for (Register s : affected)
					if (!dontBother.contains(s) && cg.rm.isInUse(s))
						cg.emit.emit("pushl", s);

				// Move the LHS (numerator) into eax
				// Move the RHS (denominator) into ebx
				cg.emit.emit("pushl", rightReg);
				cg.emit.emit("pushl", leftReg);
				cg.emit.emit("popl", Register.EAX);
				cg.emit.emit("popl", "%ebx");
				cg.emit.emitRaw("cltd"); // sign-extend %eax into %edx
				cg.emit.emit("idivl", "%ebx"); // division, result into edx:eax

				// Move the result into the LHS, and pop off anything we saved
				if(ast.operator == BOp.B_DIV)
					cg.emit.emit("movl", Register.EAX, leftReg);
				else
					cg.emit.emit("movl", Register.EDX, leftReg);
				
				for (int i = affected.length - 1; i >= 0; i--) {
					Register s = affected[i];
					if (!dontBother.contains(s) && cg.rm.isInUse(s))
						cg.emit.emit("popl", s);
				}
				break;
			case B_AND:
				cg.emit.emit("andl", rightReg, leftReg);
				break;
			case B_OR:
				cg.emit.emit("orl", rightReg, leftReg);
				break;
			case B_EQUAL:
				cg.emit.emit("pushl", Register.EAX);
				cg.emit.emit("cmpl", leftReg, rightReg);
				cg.emit.emit("sete", Register.EAX.lowByteVersion().repr);
				cg.emit.emit("movzbl", Register.EAX.lowByteVersion().repr, leftReg);
				cg.emit.emit("popl", Register.EAX);
				break;
			case B_NOT_EQUAL:
				cg.emit.emit("pushl", Register.EAX);
				cg.emit.emit("cmpl", leftReg, rightReg);
				cg.emit.emit("setne", Register.EAX.lowByteVersion().repr);
				cg.emit.emit("movzbl", Register.EAX.lowByteVersion().repr, leftReg);
				cg.emit.emit("popl", Register.EAX);
				break;
			case B_LESS_THAN:
				cg.emit.emit("pushl", Register.EAX);
				cg.emit.emit("cmpl", rightReg, leftReg);
				cg.emit.emit("setl", Register.EAX.lowByteVersion().repr);
				cg.emit.emit("movzbl", Register.EAX.lowByteVersion().repr, leftReg);
				cg.emit.emit("popl", Register.EAX);
				break;
			case B_LESS_OR_EQUAL:
				cg.emit.emit("pushl", Register.EAX);
				cg.emit.emit("cmpl", rightReg, leftReg);
				cg.emit.emit("setle", Register.EAX.lowByteVersion().repr);
				cg.emit.emit("movzbl", Register.EAX.lowByteVersion().repr, leftReg);
				cg.emit.emit("popl", Register.EAX);
				break;
			case B_GREATER_THAN:
				cg.emit.emit("pushl", Register.EAX);
				cg.emit.emit("cmpl", rightReg, leftReg);
				cg.emit.emit("setg", Register.EAX.lowByteVersion().repr);
				cg.emit.emit("movzbl", Register.EAX.lowByteVersion().repr, leftReg);
				cg.emit.emit("popl", Register.EAX);
				break;
			case B_GREATER_OR_EQUAL:
				cg.emit.emit("pushl", Register.EAX);
				cg.emit.emit("cmpl", rightReg, leftReg);
				cg.emit.emit("setge", Register.EAX.lowByteVersion().repr);
				cg.emit.emit("movzbl", Register.EAX.lowByteVersion().repr, leftReg);
				cg.emit.emit("popl", Register.EAX);
				break;
			default:
				{
					throw new ToDoException();
				}
			}

			cg.rm.releaseRegister(rightReg);
			
			cg.emit.emitMove(leftReg, "tempValue1");
			cg.rm.releaseRegister(leftReg);
			
			cg.rm.releaseRegister(Register.EAX);

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
	}

	@Override
	public Register booleanConst(BooleanConst ast, Void arg) {		
		Register reg = cg.rm.getRegister();
		if(ast.value) {
			cg.emit.emit("movl", "$1", reg);
		} else {
			cg.emit.emit("movl", "$0", reg);
		}
		return reg;
	}

	@Override
	public Register builtInRead(BuiltInRead ast, Void arg) {
		{
			Register reg = cg.rm.getRegister();
			cg.emit.emit("sub", constant(16), STACK_REG);
			cg.emit.emit("leal", AssemblyEmitter.registerOffset(8, STACK_REG), reg);
			cg.emit.emitStore(reg, 4, STACK_REG);
			cg.emit.emitStore("$STR_D", 0, STACK_REG);
			cg.emit.emit("call", SCANF);
			cg.emit.emitLoad(8, STACK_REG, reg);
			cg.emit.emit("add", constant(16), STACK_REG);
			return reg;
		}
	}

	@Override
	public Register cast(Cast ast, Void arg) {
		
		Register register = gen(ast.arg());
		
		Register classNumberRegister = cg.rm.getRegister();
		cg.emit.emitLoad(0, register, classNumberRegister);
		checkForCorrectCast(classNumberRegister, ast.typeName);
		cg.rm.releaseRegister(classNumberRegister);

		return register;
	}
	
	public void checkForCorrectCast(Register classNumberRegister, String typeName)
	{	
		String okLabel = cg.emit.uniqueLabel();
		String exitLabel = cg.emit.uniqueLabel();
		String repeatLabel = cg.emit.uniqueLabel();

		if (typeName.equals("Object")) return; //Cast to Object always succeeds
		int superClassNumber;
		//Array
		if (typeName.contains("[]"))
		{
			String baseTypeName = typeName.replace("[]", "");
			if (baseTypeName.equals("int")) superClassNumber = 1;
			else if (baseTypeName.equals("boolean")) superClassNumber = 2;
			else if (baseTypeName.equals("Object")) superClassNumber = 3;
			else {
				VTable vTable = cg.vTableMap.get(baseTypeName);
				superClassNumber = vTable.classNumber + 1;
			}
		}
		//Non-array
		else
		{
			VTable vTable = cg.vTableMap.get(typeName);
			superClassNumber = vTable.classNumber;
		}
		
		
		Register classTableRegister = cg.rm.getRegister();
		Register superClassObjectNumberRegister = cg.rm.getRegister();
		
		//Class table address
		cg.emit.emitMove("classTable", classTableRegister);
		
		cg.emit.emitMove(constant(superClassNumber), superClassObjectNumberRegister);
		
		//Object address in class table
		cg.emit.emit("imul", constant(Config.SIZEOF_PTR), classNumberRegister);
		cg.emit.emit("addl", classTableRegister, classNumberRegister);
		
		//Superclass object address in class table
		cg.emit.emit("imul", constant(Config.SIZEOF_PTR), superClassObjectNumberRegister);
		cg.emit.emit("addl", classTableRegister, superClassObjectNumberRegister);
				
		cg.emit.emitLabel(repeatLabel);
		
		//class inherits from superclass
		cg.emit.emit("cmpl", classNumberRegister, superClassObjectNumberRegister);
		cg.emit.emit("je", okLabel);
		
		//class is object
		cg.emit.emit("cmpl", classTableRegister, classNumberRegister);
		cg.emit.emit("je", exitLabel);
		
		cg.emit.emitLoad(0, classNumberRegister, classNumberRegister);
		
		cg.emit.emit("jmp", repeatLabel);
		
		//exit
		cg.emit.emitLabel(exitLabel);
		cg.emit.emit("and", -16, STACK_REG);
		cg.emit.emit("subl", constant(12), RegisterManager.STACK_REG);
		cg.emit.emit("pushl", constant(1));
		cg.emit.emit("call", Config.EXIT);
		
		cg.emit.emitLabel(okLabel);
		
		
		cg.rm.releaseRegister(classTableRegister);
		cg.rm.releaseRegister(superClassObjectNumberRegister);
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
		
		Register array = gen(ast.left());	//address of array
		Register index = gen(ast.right());
		
		//cg.emitDebug(array);
		
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
		
		//cg.emitDebug(index);
				
		cg.emit.emit("movl", "(" + index + ")", array);
		//emitLoadFromAddressToRegister(0, index, array);
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
	
	/*public void emitLoadFromAddressToRegister(int offset, Register address, Register register)
	{
		cg.emit.emit("movl", AssemblyEmitter.registerOffset(offset, address), register);
	}*/

	@Override
	public Register intConst(IntConst ast, Void arg) {
		{
			Register reg = cg.rm.getRegister();
			cg.emit.emit("movl", "$" + ast.value, reg);
			return reg;
		}
	}

	@Override
	public Register field(Field ast, Void arg) {
		
		int offset = ast.sym.offset;
		
		Register register = visit(ast.arg(), arg);
		
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
		
		cg.emit.emitLoad(offset, register, register);

		return register;
	}

	@Override
	public Register newArray(NewArray ast, Void arg) {
		
		Register savedReg1 = cg.pushRegister(Register.EAX);
		
		cg.rm.getRegister(Register.EAX);

		//get array size and multiply by 4 to get allocation size
		Register arraySize = gen(ast.arg());
		
		//check on negative size
		String label = cg.emit.uniqueLabel();
		cg.emit.emit("cmpl", constant(0), arraySize);
		cg.emit.emit("jge", label);
		//handle error if array size is negative: error code 5
		cg.emit.emit("and", -16, STACK_REG);
		cg.emit.emit("subl", constant(12), RegisterManager.STACK_REG);
		cg.emit.emit("pushl", constant(5));
		cg.emit.emit("call", Config.EXIT);
		cg.emit.emitLabel(label);
				
		Register allocSize = cg.rm.getRegister();
		cg.emit.emitMove(arraySize, allocSize);
		cg.emit.emit("addl", constant(2), allocSize);	// add extra space for bookkeeping: 1 classNumber, 2 size
		
		//malloc
		cg.emit.emit("and", -16, RegisterManager.STACK_REG);
		cg.emit.emit("subl", constant(2 * Config.SIZEOF_PTR), Register.ESP);
		cg.emit.emit("pushl", constant(Config.SIZEOF_PTR));
		cg.emit.emit("pushl", allocSize);
		cg.emit.emitRaw("call " + Config.CALLOC);
		cg.emit.emit("addl", constant(16), Register.ESP);
		
		//move the address to register allocSize
		cg.emit.emitMove(Register.EAX, allocSize);
		
		cg.rm.releaseRegister(Register.EAX);
		
		//move size of array to 4 + address of array for out-of-bounds checking
		cg.emit.emitMove(arraySize, AssemblyEmitter.registerOffset(Config.SIZEOF_PTR, allocSize));
		
		
		//move classNumber of array to address of array
		String typeName = ast.typeName.replace("[]", "");
		if (typeName.equals("int")) cg.emit.emitMove(constant(1), arraySize);
		else if (typeName.equals("boolean")) cg.emit.emitMove(constant(2), arraySize);
		else if (typeName.equals("Object")) cg.emit.emitMove(constant(3), arraySize);
		else {
			VTable vTable = cg.vTableMap.get(typeName);
			cg.emit.emitMove(constant(vTable.classNumber+1), arraySize);
		}
		cg.emit.emit("imul", constant(Config.SIZEOF_PTR), arraySize);
		cg.emit.emit("addl", "classTable", arraySize);
		cg.emit.emitMove(arraySize, AssemblyEmitter.registerOffset(0, allocSize));
		
		cg.rm.releaseRegister(arraySize);
		
		cg.emit.emitMove(allocSize, "tempValue1");
		cg.rm.releaseRegister(allocSize);
		
		cg.popRegister(savedReg1);
		
		Register returnRegister = cg.rm.getRegister();
		cg.emit.emitMove("tempValue1", returnRegister);
		
		return returnRegister;
	}

	@Override
	public Register newObject(NewObject ast, Void arg) {
		VTable vTable = cg.vTableMap.get(ast.typeName);
		if (vTable == null)
		{
			throw new ToDoException("Unknown type " + ast.typeName);
		}
		
		int objectSize = (1 + vTable.fields.size() + vTable.methodSymbols.size());
		
		Register savedReg1 = cg.pushRegister(Register.EAX);
		
		cg.rm.getRegister(Register.EAX);
		
		//malloc
		cg.emit.emit("and", -16, RegisterManager.STACK_REG);
		cg.emit.emit("subl", constant(2 * Config.SIZEOF_PTR), Register.ESP);
		cg.emit.emit("pushl", constant(Config.SIZEOF_PTR));
		cg.emit.emit("pushl", constant(objectSize));
		cg.emit.emitRaw("call " + Config.CALLOC);
		cg.emit.emit("addl", constant(16), Register.ESP);
		
		Register register = cg.rm.getRegister();
		cg.emit.emitMove(Register.EAX, register);
		
		cg.rm.releaseRegister(Register.EAX);
		
		cg.popRegister(savedReg1);
		
		//store classNumber in first 4 bytes
		cg.emit.emitStore(constant(vTable.classNumber), 0, register);
		
		//Create vTable on heap
		for (MethodSymbol symbol : vTable.methodSymbols.values())
		{
			String label = symbol.methodLabel;
			int offset = symbol.offset;
			Register labelRegister = cg.rm.getRegister();
			cg.emit.emit("leal", label, labelRegister);
			cg.emit.emitStore(labelRegister, offset, register);
			cg.rm.releaseRegister(labelRegister);
		}
		
		return register;
	}

	@Override
	public Register nullConst(NullConst ast, Void arg) {
		Register reg = cg.rm.getRegister();
		cg.emit.emitMove("$0", reg);
		return reg;
	}

	@Override
	public Register thisRef(ThisRef ast, Void arg) {
		Register register = cg.rm.getRegister();
		cg.emit.emit("movl", "this_ref", register);
		return register;
	}

	@Override
	public Register methodCall(MethodCallExpr ast, Void arg) {				
		
		cg.emit.emit("pushl", "this_ref");
		
		boolean eaxInUse = cg.rm.isInUse(Register.EAX);
		if (eaxInUse)
		{
			cg.emit.emitMove(Register.EAX, "tempValue1");
		}
		else
		{
			cg.rm.getRegister(Register.EAX);
		}
		

		//Push arguments on stack
		int argumentCount = ast.argumentsWithoutReceiver().size();
		if (argumentCount > 0)
		{
			cg.emit.emit("subl", constant(argumentCount * Config.SIZEOF_PTR), RegisterManager.STACK_REG);
			//cg.emit.emit("and", -16, STACK_REG);
			for(int i = 0; i < argumentCount; i++) {
				Expr expr = ast.argumentsWithoutReceiver().get(i);
				Register reg = visit(expr, arg);
				
				cg.emit.emit("movl",reg, AssemblyEmitter.registerOffset(Config.SIZEOF_PTR * i, RegisterManager.STACK_REG));
				cg.rm.releaseRegister(reg);
				
			}
		}
				
		//Get receiver
		Register receiver = visit(ast.receiver(), arg);
		
		//check whether object is null pointer
		String label = cg.emit.uniqueLabel();
		cg.emit.emit("cmpl", constant(0), receiver);
		cg.emit.emit("jne", label);
		//null pointer: exit with code 4
		cg.emit.emit("and", -16, STACK_REG);
		cg.emit.emit("subl", constant(12), RegisterManager.STACK_REG);
		cg.emit.emit("pushl", constant(4));
		cg.emit.emit("call", Config.EXIT);
		cg.emit.emitLabel(label);
		
		//Set this_ref
		cg.emit.emit("movl", receiver, "this_ref");
		
		//Release receiver
		cg.rm.releaseRegister(receiver);
				
		//Call method
		Register labelRegister = cg.rm.getRegister();
		int offset = ast.sym.offset;
		cg.emit.emitMove("this_ref", labelRegister);
		cg.emit.emitMove(AssemblyEmitter.registerOffset(offset, labelRegister), labelRegister);
		cg.emit.emit("call", "*" + labelRegister);
		cg.rm.releaseRegister(labelRegister);
		
		Register returnRegister = cg.rm.getRegister();
		cg.emit.emitMove(Register.EAX, returnRegister);
		
		if (eaxInUse)
		{
			cg.emit.emitMove("tempValue1", Register.EAX);
		}
		else
		{
			cg.rm.releaseRegister(Register.EAX);
		}
		
		//restore stack
		cg.emit.emit("addl", constant(argumentCount * Config.SIZEOF_PTR), RegisterManager.STACK_REG);
		
		cg.emit.emit("popl", "this_ref");
		
		return returnRegister;
	}

	@Override
	public Register unaryOp(UnaryOp ast, Void arg) {
		{
			Register argReg = gen(ast.arg());
			switch (ast.operator) {
			case U_PLUS:
				break;

			case U_MINUS:
				cg.emit.emit("negl", argReg);
				break;

			case U_BOOL_NOT:
				cg.emit.emit("negl", argReg);
				cg.emit.emit("incl", argReg);
				break;
			}
			return argReg;
		}
	}
	
	@Override
	public Register var(Var ast, Void arg) {
		{	
			Register register = cg.rm.getRegister();

			if(ast.sym.kind == VariableSymbol.Kind.FIELD)
			{
				cg.emit.emitMove("this_ref", register);
				cg.emit.emitLoad(ast.sym.offset, register, register);
			}
			else
			{
				cg.emit.emitLoad(ast.sym.offset, RegisterManager.BASE_REG, register);
			}
			
			return register;
		}
	}

}
