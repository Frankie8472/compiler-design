package cd.backend.codegen;

import java.io.Writer;
import java.util.List;

import cd.Main;
import cd.ir.Ast.ClassDecl;

public class AstCodeGenerator {

	protected ExprGenerator eg;
	protected StmtGenerator sg;
	
	protected final Main main;
	
	protected final AssemblyEmitter emit;
	protected final RegisterManager rm = new RegisterManager();

	AstCodeGenerator(Main main, Writer out) {
		this.emit = new AssemblyEmitter(out);
		this.main = main;
		this.eg = new ExprGenerator(this);
		this.sg = new StmtGenerator(this);
	}

	protected void debug(String format, Object... args) {
		this.main.debug(format, args);
	}

	public static AstCodeGenerator createCodeGenerator(Main main, Writer out) {
		return new AstCodeGenerator(main, out);
	}
	
	
	/**
	 * Main method. Causes us to emit x86 assembly corresponding to {@code ast}
	 * into {@code file}. Throws a {@link RuntimeException} should any I/O error
	 * occur.
	 * 
	 * <p>
	 * The generated file will be divided into two sections:
	 * <ol>
	 * <li>Prologue: Generated by {@link #emitPrefix()}. This contains any
	 * introductory declarations and the like.
	 * <li>Body: Generated by {@link ExprGenerator}. This contains the main
	 * method definitions.
	 * </ol>
	 */
	public void go(List<? extends ClassDecl> astRoots) {
		
		//Initialize Registers
		rm.initRegisters();
		
		for (ClassDecl ast : astRoots) {
			sg.gen(ast);
		}
	}


	protected void initMethodData() {
	}
}