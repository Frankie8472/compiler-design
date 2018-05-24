package cd.backend.codegen;

import static cd.Config.SCANF;
import static cd.backend.codegen.AssemblyEmitter.constant;
import static cd.backend.codegen.RegisterManager.STACK_REG;

import java.util.Arrays;
import java.util.List;

import cd.Config;
import cd.ToDoException;
import cd.backend.codegen.RegisterManager.Register;
import cd.ir.Ast.BinaryOp;
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
import cd.ir.Symbol.ArrayTypeSymbol;
import cd.ir.Symbol.ClassSymbol;
import cd.ir.Symbol.MethodSymbol;
import cd.ir.Symbol.VariableSymbol;
import cd.ir.Symbol.VariableSymbol.Kind;
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
			// Simplistic HW1 implementation that does
			// not care if it runs out of registers, and
			// supports only a limited range of operations:

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
				// Save EAX, EBX, and EDX to the stack if they are not used
				// in this subtree (but are used elsewhere). We will be
				// changing them.
				
				//Check if rhs is 0
				
				cg.emit.emit("cmp", AssemblyEmitter.constant(0),rightReg);
				cg.emit.emit("je", "_exit7");
				
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
				cg.emit.emit("movl", Register.EAX, leftReg);
				for (int i = affected.length - 1; i >= 0; i--) {
					Register s = affected[i];
					if (!dontBother.contains(s) && cg.rm.isInUse(s))
						cg.emit.emit("popl", s);
				}
				break;
				
			case B_MOD:
				
				dontBother = Arrays.asList(rightReg, leftReg);
				Register[] affected2 = { Register.EAX, Register.EBX, Register.EDX };

				for (Register s : affected2)
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
				cg.emit.emit("movl", Register.EDX, leftReg);
				for (int i = affected2.length - 1; i >= 0; i--) {
					Register s = affected2[i];
					if (!dontBother.contains(s) && cg.rm.isInUse(s))
						cg.emit.emit("popl", s);
				}
				break;
				
			case B_AND:
				cg.emit.emit("and", rightReg,leftReg);
				break;
			case B_OR:
				cg.emit.emit("or", rightReg,leftReg);
				break;
			case B_EQUAL:
				cg.emit.emit("cmp",rightReg,leftReg);
				cg.emit.emit("je", "1f");
				cg.emit.emitMove(AssemblyEmitter.constant(0),leftReg);
				cg.emit.emit("jmp", "2f");
				cg.emit.emitLabel("1");
				cg.emit.emitMove(AssemblyEmitter.constant(1),leftReg);
				cg.emit.emitLabel("2");

				break;
			case B_NOT_EQUAL:
				cg.emit.emit("cmp",rightReg,leftReg);
				cg.emit.emit("jne", "1f");
				cg.emit.emitMove(AssemblyEmitter.constant(0),leftReg);
				cg.emit.emit("jmp", "2f");
				cg.emit.emitLabel("1");
				cg.emit.emitMove(AssemblyEmitter.constant(1),leftReg);
				cg.emit.emitLabel("2");
				break;
			case B_LESS_THAN:
				cg.emit.emit("cmp",rightReg,leftReg);
				cg.emit.emit("jl", "1f");
				cg.emit.emitMove(AssemblyEmitter.constant(0),leftReg);
				cg.emit.emit("jmp", "2f");
				cg.emit.emitLabel("1");
				cg.emit.emitMove(AssemblyEmitter.constant(1),leftReg);
				cg.emit.emitLabel("2");
				break;
			case B_LESS_OR_EQUAL:
				cg.emit.emit("cmp",rightReg,leftReg);
				cg.emit.emit("jle", "1f");
				cg.emit.emitMove(AssemblyEmitter.constant(0),leftReg);
				cg.emit.emit("jmp", "2f");
				cg.emit.emitLabel("1");
				cg.emit.emitMove(AssemblyEmitter.constant(1),leftReg);
				cg.emit.emitLabel("2");
				break;
			case B_GREATER_THAN:
				cg.emit.emit("cmp",rightReg,leftReg);
				cg.emit.emit("jg", "1f");
				cg.emit.emitMove(AssemblyEmitter.constant(0),leftReg);
				cg.emit.emit("jmp", "2f");
				cg.emit.emitLabel("1");
				cg.emit.emitMove(AssemblyEmitter.constant(1),leftReg);
				cg.emit.emitLabel("2");
				break;
			case B_GREATER_OR_EQUAL:
				cg.emit.emit("cmp",rightReg,leftReg);
				cg.emit.emit("jge", "1f");
				cg.emit.emitMove(AssemblyEmitter.constant(0),leftReg);
				cg.emit.emit("jmp", "2f");
				cg.emit.emitLabel("1");
				cg.emit.emitMove(AssemblyEmitter.constant(1),leftReg);
				cg.emit.emitLabel("2");
				break;
			default:
				{
					throw new ToDoException();
				}
			}

			cg.rm.releaseRegister(rightReg);

			return leftReg;
		}
	}

	@Override
	public Register booleanConst(BooleanConst ast, Void arg) {
		Register reg=cg.rm.getRegister();
		if(ast.value) {
			cg.emit.emitMove(AssemblyEmitter.constant(1),reg);
		}else {
			cg.emit.emitMove(AssemblyEmitter.constant(0),reg);
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
		Register reg=cg.eg.gen(ast.arg());
		VTable vtable=cg.vtables.get(ast.typeName);
		cg.emit.emit("pushl", reg);
		cg.emit.emitLoad(0, reg, reg);
		
		if(vtable.isArray) {
			//arg has to have type object
			int objectId=cg.vtables.get(ClassSymbol.objectType.name).id;
			
			
			cg.emit.emitLoad(0, reg, reg);
			cg.emit.emit("cmp",AssemblyEmitter.constant(objectId),reg);
			cg.emit.emit("jne", "_exit1");
			
		}else {
			for(int i=0;i<RegisterManager.CALLER_SAVE.length;i++) {
				cg.emit.emit("pushl", RegisterManager.CALLER_SAVE[i]);
			}
			int id = vtable.id;
			cg.emit.emit("pushl", reg);

			
			cg.emit.emit("push", AssemblyEmitter.constant(id));
			cg.emit.emit("call", "_isSubtype");
			cg.emit.emit("addl",AssemblyEmitter.constant(8),Register.ESP);
			
			cg.emit.emit("cmp",AssemblyEmitter.constant(0),Register.EAX);
			cg.emit.emit("je","_exit1");
			
			for(int i=RegisterManager.CALLER_SAVE.length-1;i>=0;i--) {
				cg.emit.emit("popl", RegisterManager.CALLER_SAVE[i]);
			}
			
		}
		return reg;
	}

	@Override
	public Register index(Index ast, Void arg) {
		Register array=gen(ast.left());
		cg.emit.emit("cmp", AssemblyEmitter.constant(0),array);
		cg.emit.emit("je", "_exit4");
		
		Register index=gen(ast.right());
		
		cg.emit.emit("cmp",AssemblyEmitter.constant(0),index );
		cg.emit.emit("jl", "_exit3");
		cg.emit.emit("cmp",AssemblyEmitter.registerOffset(Config.SIZEOF_PTR, array),index);
		cg.emit.emit("jge", "_exit3");
		cg.emit.emitMove(AssemblyEmitter.arrayAddress(array, index), array);
		cg.rm.releaseRegister(index);
		return array;
	}

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
		Register obj=gen(ast.arg());
		cg.emit.emit("cmp", AssemblyEmitter.constant(0),obj);
		cg.emit.emit("je", "_exit4");
		
		int offset=cg.vtables.get(ast.arg().type.name).getOffset(ast.sym);
		cg.emit.emitLoad(offset, obj, obj);
		return obj;
	}

	@Override
	public Register newArray(NewArray ast, Void arg) {
		Register capacity=gen(ast.arg());
		cg.emit.emit("cmp", AssemblyEmitter.constant(0),capacity);
		cg.emit.emit("jl", "_exit5");
		ArrayTypeSymbol symbol=(ArrayTypeSymbol) ast.type;
		int size=cg.vtables.get(symbol.elementType.name).instanceSize();
		
		for(int i=0;i<RegisterManager.CALLER_SAVE.length;i++) {
			if(capacity!=RegisterManager.CALLER_SAVE[i])
				cg.emit.emit("pushl", RegisterManager.CALLER_SAVE[i]);
		}
		cg.emit.emit("push", capacity);
		
		cg.emit.emit("imul",AssemblyEmitter.constant(size),capacity);
		cg.emit.emit("addl",AssemblyEmitter.constant(2*Config.SIZEOF_PTR),capacity);
		
		cg.emit.emit("pushl",AssemblyEmitter.constant(1));
		cg.emit.emit("pushl",capacity);
		cg.emit.emit("call",Config.CALLOC);
		cg.emit.emit("add",AssemblyEmitter.constant(2*Config.SIZEOF_PTR), Register.ESP);
		cg.emit.emitMove(Register.EAX, capacity);
		
		Register r=cg.rm.getRegister();
		cg.emit.emitMove(AstCodeGenerator.VTABLE_PREFIX+symbol.elementType.name+"_array", r);
		cg.emit.emitStore(r, 0, capacity);
		cg.emit.emit("popl", r);
		cg.emit.emitStore(r, Config.SIZEOF_PTR, capacity);
		cg.rm.releaseRegister(r);
		cg.emit.emit("addl",AssemblyEmitter.constant(Config.SIZEOF_PTR),Register.ESP);
		
		for(int i=RegisterManager.CALLER_SAVE.length-1;i>=0;i--) {
			if(capacity!=RegisterManager.CALLER_SAVE[i])
				cg.emit.emit("popl", RegisterManager.CALLER_SAVE[i]);
		}
		
		
		return capacity;
		
	}

	@Override
	public Register newObject(NewObject ast, Void arg) {
		Register reg= cg.rm.getRegister();
		int size = cg.vtables.get(ast.typeName).instanceSize();
		
		for(int i=0;i<RegisterManager.CALLER_SAVE.length;i++) {
			if(reg!=RegisterManager.CALLER_SAVE[i])
				cg.emit.emit("pushl", RegisterManager.CALLER_SAVE[i]);
		}
		
		cg.emit.emit("pushl", AssemblyEmitter.constant(size));
		cg.emit.emit("pushl", AssemblyEmitter.constant(1));
		cg.emit.emit("call", Config.CALLOC);
		cg.emit.emit("addl", AssemblyEmitter.constant(2*Config.SIZEOF_PTR),Register.ESP);
		cg.emit.emitMove(Register.EAX, reg);
		
		for(int i=RegisterManager.CALLER_SAVE.length-1;i>=0;i--) {
			if(reg!=RegisterManager.CALLER_SAVE[i])
				cg.emit.emit("popl", RegisterManager.CALLER_SAVE[i]);
		}
		Register r=cg.rm.getRegister();
		cg.emit.emit("movl", AstCodeGenerator.VTABLE_PREFIX+ast.typeName,r);
		cg.emit.emitStore(r, 0, reg);
		cg.rm.releaseRegister(r);
		return reg;
	}

	@Override
	public Register nullConst(NullConst ast, Void arg) {
		Register reg = cg.rm.getRegister();
		cg.emit.emitMove(AssemblyEmitter.constant(0), reg);
		return reg;
	}

	@Override
	public Register thisRef(ThisRef ast, Void arg) {
		Register reg=cg.rm.getRegister();
		cg.emit.emitLoad(2*Config.SIZEOF_PTR, Register.EBP,reg);
		return reg;
	}

	@Override
	public Register methodCall(MethodCallExpr ast, Void arg) {
		Register reg=cg.rm.getRegister();
		Register argument;
		
		
		for(int i=0;i<RegisterManager.CALLER_SAVE.length;i++) {
			if(reg!=RegisterManager.CALLER_SAVE[i])
				cg.emit.emit("pushl", RegisterManager.CALLER_SAVE[i]);
		}
		cg.emit.emit("subl",AssemblyEmitter.constant(ast.allArguments().size()*Config.SIZEOF_PTR),Register.ESP);
		
		for(int i=0;i<ast.allArguments().size();i++) {
			argument=gen(ast.allArguments().get(i));
			cg.emit.emitStore(argument, i*Config.SIZEOF_PTR, Register.ESP);
			if(i==0) {
				cg.emit.emitMove(argument, reg);
				cg.emit.emit("cmp", AssemblyEmitter.constant(0),reg);
				cg.emit.emit("je", "_exit4");
			}
			cg.rm.releaseRegister(argument);

		}
		int offset=cg.vtables.get(ast.receiver().type.name).getOffset(ast.sym);

		
		cg.emit.emitLoad(0, reg, reg);
		cg.emit.emitLoad(-offset, reg, reg);
		
		
		cg.emit.emit("call", reg);
		cg.emit.emitMove(Register.EAX, reg);
		cg.emit.emit("addl",AssemblyEmitter.constant(ast.allArguments().size()*4),Register.ESP);
		
		
		for(int i=RegisterManager.CALLER_SAVE.length-1;i>=0;i--) {
			if(reg!=RegisterManager.CALLER_SAVE[i])
				cg.emit.emit("popl", RegisterManager.CALLER_SAVE[i]);
		}
		
		return reg;
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
			Register reg = cg.rm.getRegister();
			int offset;
			switch(ast.sym.kind) {
			case LOCAL:
				List<VariableSymbol> table = cg.localTables.get(cg.currentLabel);
				offset=Config.SIZEOF_PTR*(table.indexOf(ast.sym)+1);
				cg.emit.emitLoad(-offset, Register.EBP, reg);
				break;
			
			case PARAM:
				MethodSymbol symbol=((MethodSymbol) cg.currentSymbol);
				offset=symbol.parameters.indexOf(ast.sym);
				offset=Config.SIZEOF_PTR*offset+3*Config.SIZEOF_PTR;
				cg.emit.emitLoad(offset, Register.EBP, reg);
				break;
				
			case FIELD:
				cg.emit.emitLoad(2*Config.SIZEOF_PTR, Register.EBP, reg);
				offset=cg.vtables.get(cg.currentClass.name).getOffset(ast.sym);
				cg.emit.emitLoad(offset, reg, reg);

				
				break;
				
			}
			
			return reg;
		}
	}

}
