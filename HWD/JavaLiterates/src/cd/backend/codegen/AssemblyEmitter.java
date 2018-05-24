package cd.backend.codegen;

import java.io.IOException;
import java.io.Writer;

import cd.Config;
import cd.backend.codegen.RegisterManager.Register;

public class AssemblyEmitter {
	public Writer out;
	public StringBuilder indent = new StringBuilder();
	public StringBuilder dataStr = new StringBuilder();
	public StringBuilder dataInt = new StringBuilder();
	public StringBuilder text = new StringBuilder();
	public int counter = 0;
	public static final String TRUE = "$-1";
	public static final String FALSE = "$1";
	public AstCodeGenerator cg;

	public AssemblyEmitter(Writer out, AstCodeGenerator acg) {
		this.out = out;
		this.cg = acg;
		// Initialize sections
		dataStr.append(Config.DATA_STR_SECTION + "\n");
		dataInt.append(Config.DATA_INT_SECTION + "\n");
		text.append(Config.TEXT_SECTION + "\n");

		// Emit some useful string constants:
		labelDS("STR_NL");
		rawDS(Config.DOT_STRING + " \"\\n\"");
		labelDS("STR_D");
		rawDS(Config.DOT_STRING + " \"%d\"");
	}

	/** Writes data and text segments to file */
	public void toFile() {
		try {
			out.write(dataStr.toString() + "\n");
			out.write(dataInt.toString() + "\n");
			out.write(text.toString());
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	/** Creates an constant operand. */
	static String constant(int i) {
		return "$" + i;
	}

	/** Creates an constant operand with the address of a label. */
	static String labelAddress(String lbl) {
		return "$" + lbl;
	}

	/** Creates an operand relative to another operand. */
	static String deref(int offset, Register reg) {
		return String.format("%d(%s)", offset, reg);
	}

	/** Creates an operand addressing an item in an array */
	static String arrayAddress(Register arrReg, Register idxReg, int size) {
		final int offset = size * 2; // one size each in front for
										// vptr and length
		final int mul = size;
		return String.format("%d(%s,%s,%d)", offset, arrReg, idxReg, mul);
	}

	void increaseIndent(String comment) {
		indent.append("  ");
		if (comment != null)
			comment(comment);
	}

	void decreaseIndent() {
		// indent.setLength(indent.length() - 2);
	}

	void commentSection(String name) {
		int indentLen = indent.length();
		int breakLen = 68 - indentLen - name.length();
		StringBuffer sb = new StringBuffer();
		sb.append(Config.COMMENT_SEP).append(" ");
		for (int i = 0; i < indentLen; i++)
			sb.append("_");
		sb.append(name);
		for (int i = 0; i < breakLen; i++)
			sb.append("_");

		text.append(sb.toString());
		text.append("\n");
	}

	void comment(String comment) {
		raw(Config.COMMENT_SEP + " " + comment);
	}

	void emit(String op, Register src, String dest) {
		emit(op, src.repr, dest);
	}

	void emit(String op, String src, Register dest) {
		emit(op, src, dest.repr);
	}

	void emit(String op, Register src, Register dest) {
		emit(op, src.repr, dest.repr);
	}

	void emit(String op, String src, String dest) {
		raw(String.format("%s %s, %s", op, src, dest));
	}

	void emit(String op, int src, Register dest) {
		emit(op, constant(src), dest);
	}

	void emit(String op, String dest) {
		raw(op + " " + dest);
	}

	void emit(String op, Register reg) {
		emit(op, reg.repr);
	}

	void emit(String op, int dest) {
		emit(op, constant(dest));
	}

	void mov(Register src, String dest) {
		mov(src.repr, dest);
	}

	void mov(Register src, Register dest) {
		mov(src.repr, dest.repr);
	}

	void mov(String src, Register dest) {
		mov(src, dest.repr);
	}

	void mov(int val, Register dest) {
		mov(constant(val), dest.repr);
	}

	void mov(String src, String dest) {
		if (!src.equals(dest))
			emit("movl", src, dest);
	}

	void add(String src, String dest) {
		emit("addl", src, dest);
	}

	void add(int val, String dest) {
		emit("addl", "$" + Integer.toString(val), dest);
	}

	void add(int val, Register dest) {
		emit("addl", "$" + Integer.toString(val), dest);
	}

	void sub(Register src, Register dest) {
		emit("subl", src, dest);
	}

	void neg(Register dest) {
		emit("negl", dest);
	}

	void inc(Register dest) {
		emit("incl", dest);
	}

	void and(Register src, Register dest) {
		emit("andl", src, dest);
	}

	void or(Register src, Register dest) {
		emit("orl", src, dest);
	}

	void xor(Register src, Register dest) {
		emit("xorl", src, dest);
	}

	void xor(String src, String dest) {
		emit("xorl", src, dest);
	}

	void jmp(String dest) {
		emit("jmp", dest);
	}

	void jne(String dest) {
		emit("jne", dest);
	}

	void je(String dest) {
		emit("je", dest);
	}

	void jl(String dest) {
		emit("jl", dest);
	}

	void jle(String dest) {
		emit("jle", dest);
	}

	void jg(String dest) {
		emit("jg", dest);
	}

	void jge(String dest) {
		emit("jge", dest);
	}

	void lea(String src, String dest) {
		emit("leal", src, dest);
	}

	void test(String src, String dest) {
		emit("test", src, dest);
	}

	void cmp(Register src, Register dest) {
		emit("cmpl", src, dest);
	}

	void cmp(int i, Register dest) {
		emit("cmpl", "$" + Integer.toString(i), dest);
	}

	void leave() {
		raw("leave");
	}

	void load(int srcOffset, Register src, Register dest) {
		mov(deref(srcOffset, src), dest.repr);
	}

	void store(Register src, int destOffset, Register dest) {
		store(src.repr, destOffset, dest);
	}

	void store(String src, int destOffset, Register dest) {
		mov(src, deref(destOffset, dest));
	}

	Register calloc(int n, int size) {
		/*
		 * // Save EDI, ESI if necessary if(cg.rm.isInUse(Register.EDI)) {
		 * push(Register.EDI); } if(cg.rm.isInUse(Register.ESI)) { push(Register.ESI); }
		 */
		// Put n in EDI, size in EDI
		// cg.emit.mov(n, Register.ESI);
		// cg.emit.mov(size, Register.EDI);
		push(n);
		push(size);

		call(Config.CALLOC);

		add(8, Register.ESP.repr);
		// Memory location is now in EAX

		/*
		 * // Restore EDI, ESI if necessary if(cg.rm.isInUse(Register.ESI)) {
		 * pop(Register.ESI); } if(cg.rm.isInUse(Register.EDI)) { pop(Register.EDI); }
		 */
		return Register.EAX;
	}

	Register calloc(Register n, int size) {
		/*
		 * // Save EDI, ESI if necessary if(cg.rm.isInUse(Register.EDI)) {
		 * push(Register.EDI); } if(cg.rm.isInUse(Register.ESI)) { push(Register.ESI); }
		 */
		// Put n in EDI, size in EDI
		// cg.emit.mov(n, Register.ESI);
		// cg.emit.mov(size, Register.EDI);
		push(n);
		push(size);

		call(Config.CALLOC);

		add(8, Register.ESP.repr);
		// Memory location is now in EAX

		/*
		 * // Restore EDI, ESI if necessary if(cg.rm.isInUse(Register.ESI)) {
		 * pop(Register.ESI); } if(cg.rm.isInUse(Register.EDI)) { pop(Register.EDI); }
		 */
		return Register.EAX;
	}

	/*
	 * Stack frame for function calls: we use push and pop to let the gcc compiler
	 * handle it for every function call.
	 * 
	 * EAX is also caller saved, but we need let it be overwritten as it contains
	 * the return value. It gets saved on a case to case basis.
	 */
	void call(String function) {
		raw("call " + function);
	}

	void ret() {
		raw("ret");
	}

	void push(Register reg) {
		raw("pushl " + reg.repr);
	}

	void push(int val) {
		raw("pushl " + "$" + Integer.toString(val));
	}

	void pop(Register reg) {
		raw("popl " + reg.repr);
	}

	void constantDataDS(String data) {
		rawDI(String.format("%s %s", Config.DOT_STRING, data));
	}

	void constantDataDI(int data) {
		rawDI(String.format("%s %d", Config.DOT_INT, data));
	}

	String uniqueLabel() {
		String labelName = "label" + counter++;
		return labelName;
	}

	void label(String label) {
		text.append(label + ":" + "\n");
	}

	void labelDS(String label) {
		dataStr.append(label + ":" + "\n");
	}

	void labelDI(String label) {
		dataInt.append(label + ":" + "\n");
	}

	/** Adds op to the .text segment */
	void raw(String op) {
		text.append(indent.toString());
		text.append(op);
		text.append("\n");
	}

	/** Adds op to the .data string segment */
	void rawDS(String op) {
		dataStr.append(op);
		dataStr.append("\n");
	}

	/** Adds op to the .data int segment */
	void rawDI(String op) {
		dataInt.append(op);
		dataInt.append("\n");
	}
}