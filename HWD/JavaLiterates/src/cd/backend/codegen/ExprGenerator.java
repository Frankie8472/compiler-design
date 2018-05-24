package cd.backend.codegen;

import static cd.Config.SCANF;
import static cd.backend.codegen.AssemblyEmitter.constant;
import static cd.backend.codegen.AssemblyEmitter.TRUE;
import static cd.backend.codegen.AssemblyEmitter.FALSE;
import static cd.backend.codegen.RegisterManager.*;

import java.util.Arrays;
import java.util.List;

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
import cd.util.Pair;
import cd.util.debug.AstOneLine;

/**
 * Generates code to evaluate expressions. After emitting the code, returns a
 * String which indicates the register where the result can be found.
 */
class ExprGenerator extends ExprVisitor<Register, Pair<String>> {
	protected final AstCodeGenerator cg;

	ExprGenerator(AstCodeGenerator astCodeGenerator) {
		cg = astCodeGenerator;
	}

	public Register gen(Expr ast, Pair<String> arg) {
		return visit(ast, arg);
	}

	@Override
	public Register visit(Expr ast, Pair<String> arg) {
		try {
			cg.emit.increaseIndent("Emitting " + AstOneLine.toString(ast));
			return super.visit(ast, arg);
		} finally {
			cg.emit.decreaseIndent();
		}

	}

	@Override
	public Register binaryOp(BinaryOp ast, Pair<String> arg) {

		// Simplistic HW1 implementation that does
		// not care if it runs out of registers, and
		// supports only a limited range of operations:

		int leftRN = cg.rnv.calc(ast.left());
		int rightRN = cg.rnv.calc(ast.right());

		Register leftReg, rightReg;
		if (leftRN > rightRN) {
			leftReg = gen(ast.left(), arg);
			rightReg = gen(ast.right(), arg);
		} else {
			rightReg = gen(ast.right(), arg);
			leftReg = gen(ast.left(), arg);
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
			cg.emit.raw("cltd"); // sign-extend %eax into %edx
			cg.emit.emit("idivl", "%ebx"); // division, result into edx:eax

			// Move the result into the LHS, and pop off anything we saved
			cg.emit.emit("movl", Register.EAX, leftReg);
			for (int i = affected.length - 1; i >= 0; i--) {
				Register s = affected[i];
				if (!dontBother.contains(s) && cg.rm.isInUse(s))
					cg.emit.emit("popl", s);
			}
			break;
		case B_AND:
			cg.emit.and(rightReg, leftReg);
			break;
		case B_OR:
			cg.emit.or(rightReg, leftReg);
			break;
		case B_LESS_THAN: // l < r
			cg.emit.sub(rightReg, leftReg);
			break;
		case B_LESS_OR_EQUAL: // l <= r
			String endLE = cg.emit.uniqueLabel();
			String labelLE = cg.emit.uniqueLabel();
			cg.emit.cmp(rightReg, leftReg); // l - r
			cg.emit.jle(labelLE);
			// code for r < l :: false
			cg.emit.mov(FALSE, leftReg);
			cg.emit.jmp(endLE);
			cg.emit.label(labelLE);
			// code for l <= r :: true
			cg.emit.mov(TRUE, leftReg);
			cg.emit.label(endLE);
			break;
		case B_GREATER_THAN:
			cg.emit.sub(leftReg, rightReg); // r < l
			cg.emit.mov(rightReg, leftReg); // r => l
			break;
		case B_GREATER_OR_EQUAL: // l >= r
			String endGE = cg.emit.uniqueLabel();
			String labelGE = cg.emit.uniqueLabel();
			cg.emit.cmp(rightReg, leftReg);
			cg.emit.jge(labelGE);
			// code for r < l :: false
			cg.emit.mov(FALSE, leftReg);
			cg.emit.jmp(endGE);
			cg.emit.label(labelGE);
			// code for l <= r :: true
			cg.emit.mov(TRUE, leftReg);
			cg.emit.label(endGE);
			break;
		case B_EQUAL:
			String endE = cg.emit.uniqueLabel();
			String labelE = cg.emit.uniqueLabel();
			cg.emit.cmp(leftReg, rightReg);
			cg.emit.je(labelE);
			// If not Equal
			cg.emit.mov(FALSE, leftReg);
			cg.emit.jmp(endE);
			// If Equal
			cg.emit.label(labelE);
			cg.emit.mov(TRUE, leftReg);
			// END
			cg.emit.label(endE);

			break;
		case B_NOT_EQUAL:
			String endNE = cg.emit.uniqueLabel();
			String labelNE = cg.emit.uniqueLabel();
			cg.emit.cmp(leftReg, rightReg);
			cg.emit.jne(labelNE);
			// If Equal
			cg.emit.mov(FALSE, leftReg);
			cg.emit.jmp(endNE);
			// If not Equal
			cg.emit.label(labelNE);
			cg.emit.mov(TRUE, leftReg);
			// END
			cg.emit.label(endNE);

			break;
		default:
			throw new ToDoException();
		}

		cg.rm.releaseRegister(rightReg);

		return leftReg;

	}

	@Override
	public Register booleanConst(BooleanConst ast, Pair<String> arg) {
		Register reg = cg.rm.getRegister();
		// MSB indicates True/False
		if (ast.value)
			cg.emit.emit("movl", TRUE, reg);
		else
			cg.emit.emit("movl", FALSE, reg);
		return reg;
	}

	@Override
	public Register builtInRead(BuiltInRead ast, Pair<String> arg) {

		Register reg = cg.rm.getRegister();
		cg.emit.emit("sub", constant(16), STACK_REG);
		cg.emit.emit("leal", AssemblyEmitter.deref(8, STACK_REG), reg);
		cg.emit.store(reg, 4, STACK_REG);
		cg.emit.store("$STR_D", 0, STACK_REG);
		cg.emit.emit("call", SCANF);
		cg.emit.load(8, STACK_REG, reg);
		cg.emit.emit("add", constant(16), STACK_REG);
		return reg;

	}

	@Override
	public Register cast(Cast ast, Pair<String> arg) {
		// TODO Finish this, it there's time left
		Register reg = gen(ast.arg(), arg);
		return reg;
	}

	@Override
	public Register index(Index ast, Pair<String> arg) {
		Register lhs = gen(ast.left(), arg);
		Register rhs = gen(ast.right(), arg);
		int size = cg.ot.ot.get(ast.type.name).objSize();
		String address = AssemblyEmitter.arrayAddress(lhs, rhs, size);
		cg.emit.mov(address, lhs);
		;
		cg.rm.releaseRegister(rhs);
		return lhs;
	}

	@Override
	public Register intConst(IntConst ast, Pair<String> arg) {
		Register reg = cg.rm.getRegister();
		cg.emit.emit("movl", "$" + ast.value, reg);
		return reg;

	}

	@Override
	public Register field(Field ast, Pair<String> arg) {
		Expr rcvr = ast.arg();
		Register rcvrReg = cg.eg.gen(rcvr, arg);
		int fieldOffset = cg.ot.ot.get(rcvr.type.name).fieldOffsets.get(ast.fieldName);
		cg.emit.load(fieldOffset, rcvrReg, rcvrReg);
		return rcvrReg;
	}

	@Override
	public Register newArray(NewArray ast, Pair<String> arg) {

		Register nReg = gen(ast.arg(), arg);
		// TODO Check Bounds
		// TODO Check if size > 0
		int size = cg.ot.ot.get(ast.typeName).objSize();
		cg.emit.add(2, nReg);
		Register reg = cg.emit.calloc(nReg, size);

		// Put vtable in appropriate object table
		String vtable = AstCodeGenerator.VTABLE_PREFIX
				+ ((ArrayTypeSymbol) cg.typeSymbols.get(ast.typeName)).elementType.name;
		cg.emit.store(AssemblyEmitter.labelAddress(vtable), 0, reg);
		cg.emit.store(nReg, -4, reg);

		cg.rm.releaseRegister(nReg);
		return reg;
	}

	@Override
	public Register newObject(NewObject ast, Pair<String> arg) {
		// Size of object table; 1 for pointer to vtable
		int n = 1;
		ClassSymbol symb = (ClassSymbol) cg.typeSymbols.get(ast.typeName);

		// Increase object table size by 1 per field directly in the class
		n = n + symb.fields.keySet().size();

		/* Increase object table size for each field inherited */
		ClassSymbol currentSym = symb.superClass;

		// Iterate until at Object
		while (currentSym != null && !currentSym.name.equals("Object")) {
			n = n + currentSym.fields.keySet().size();
			currentSym = currentSym.superClass;
		}

		// Allocate memory for object

		// Save EAX to stack
		if (cg.rm.isInUse(Register.EAX)) {
			cg.emit.push(Register.EAX);
		}
		// Call calloc
		cg.emit.calloc(n, 4);
		// Memory location is now in EAX, move to new register
		Register reg = cg.rm.getRegister();
		cg.emit.mov(Register.EAX, reg);
		// Restore EAX from stack
		if (cg.rm.isInUse(Register.EAX)) {
			cg.emit.pop(Register.EAX);
		}

		// Put vtable in appropriate object table
		String vtable = AstCodeGenerator.VTABLE_PREFIX + symb.name;
		cg.emit.store(AssemblyEmitter.labelAddress(vtable), 0, reg);

		return reg;
	}

	@Override
	public Register nullConst(NullConst ast, Pair<String> arg) {
		Register reg = cg.rm.getRegister();
		// Create pointer to null
		cg.emit.xor(reg, reg);
		return reg;
	}

	@Override
	public Register thisRef(ThisRef ast, Pair<String> arg) {
		Register reg = cg.rm.getRegister();
		// Reference to caller is 8 above EBP
		cg.emit.mov(AssemblyEmitter.deref(8, BASE_REG), reg);
		return reg;
	}

	/*
	 * Our stack frame setup:
	 * 
	 * 	  24	...
	 * 	  20	Parameter3
	 *    16	Parameter2
	 *    12	Parameter1
	 *    8		This reference
	 *    4		Return address
	 *    0		Old EBP             <-- EBP
	 *    -4	LocalVar1
	 *    -8	LocalVar2
	 *    -12	LocalVar3
	 *    -16	...
	 */
	@Override
	public Register methodCall(MethodCallExpr ast, Pair<String> arg) {

		ClassSymbol classSym = ast.sym.context;
		String methodName = ast.methodName;

		// Save CALLER_SAVE reg: Register.EAX
		// cg.rm.save_caller();
		if (cg.rm.isInUse(Register.EAX)) {
			cg.emit.push(Register.EAX);
		}

		// We push all args on stack, in the correct order (which is reversed)
		List<Expr> args = ast.argumentsWithoutReceiver();
		for (int i = args.size() - 1; i >= 0; i--) {
			Expr a = args.get(i);
			cg.emit.push(gen(a, arg));
		}
		Register objRef = gen(ast.receiver(), arg);
		cg.emit.push(objRef);
		cg.rm.releaseRegister(objRef);
		cg.emit.call(cg.ot.getMethodLabel(classSym, methodName));

		Register reg = cg.rm.getRegister();
		cg.emit.mov(Register.EAX, reg);

		// restore caller saved reg
		if (cg.rm.isInUse(Register.EAX)) {
			cg.emit.pop(Register.EAX);
		}
		// cg.rm.restore_caller();
		return reg;
	}

	@Override
	public Register unaryOp(UnaryOp ast, Pair<String> arg) {

		Register argReg = gen(ast.arg(), arg);
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

	@Override
	public Register var(Var ast, Pair<String> arg) {

		Register reg = cg.rm.getRegister();
		switch (ast.sym.kind) {
		case PARAM:
		case LOCAL:
			int varOffset = ((ClassSymbol) cg.typeSymbols.get(arg.a)).methods.get(arg.b).locAndParaOffsets
					.get(ast.name);
			cg.emit.load(varOffset, Register.EBP, reg);
			break;
		case FIELD:
			int fieldOffset = cg.ot.ot.get(arg.a).fieldOffsets.get(ast.name);
			Register register = cg.rm.getRegister();
			cg.emit.load(8, Register.EBP, register);
			cg.emit.load(fieldOffset, register, reg);
			cg.rm.releaseRegister(register);
			break;
		}

		return reg;

	}

}