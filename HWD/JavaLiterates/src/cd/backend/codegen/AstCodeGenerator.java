package cd.backend.codegen;

import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import cd.Config;
import cd.Main;
import cd.backend.codegen.RegisterManager.Register;
import cd.frontend.semantic.SymTable;
import cd.ir.Ast.ClassDecl;
import cd.ir.Ast.Expr;
import cd.ir.Ast.MethodCallExpr;
import cd.ir.Ast.NewObject;
import cd.ir.Symbol.TypeSymbol;
import cd.ir.Symbol.ClassSymbol;

public class AstCodeGenerator {

	protected RegsNeededVisitor rnv;

	protected ExprGenerator eg;
	protected StmtGenerator sg;

	protected final Main main;

	protected final AssemblyEmitter emit;
	protected final RegisterManager rm = new RegisterManager();
	protected final ObjectTables ot;

	public SymTable<TypeSymbol> typeSymbols;

	AstCodeGenerator(Main main, Writer out, SymTable<TypeSymbol> symTable) {
		{
			initMethodData();
		}
		this.typeSymbols = symTable;
		this.ot = new ObjectTables(this);
		this.emit = new AssemblyEmitter(out, this);
		this.main = main;
		this.rnv = new RegsNeededVisitor();

		// Build object tables, emit vtables
		ot.constructVtables();

		this.eg = new ExprGenerator(this);
		this.sg = new StmtGenerator(this);
	}

	protected void debug(String format, Object... args) {
		this.main.debug(format, args);
	}

	public static AstCodeGenerator createCodeGenerator(Main main, Writer out, SymTable<TypeSymbol> typeSymTable) {
		return new AstCodeGenerator(main, out, typeSymTable);
	}

	protected static final String VAR_PREFIX = "var_";
	protected static final String VTABLE_PREFIX = "vtable_";

	/**
	 * Main method. Causes us to emit x86 assembly corresponding to {@code ast} into
	 * {@code file}. Throws a {@link RuntimeException} should any I/O error occur.
	 * 
	 * <p>
	 * The generated file will be divided into two sections:
	 * <ol>
	 * <li>Prologue: Generated by {@link #emitPrefix()}. This contains any
	 * introductory declarations and the like.
	 * <li>Body: Generated by {@link ExprGenerator}. This contains the main method
	 * definitions.
	 * </ol>
	 */
	public void go(List<? extends ClassDecl> astRoots) {
		emitBootstrap(astRoots);
		for (ClassDecl ast : astRoots) {
			sg.gen(ast, null);
		}
		emit.toFile();
	}

	protected void initMethodData() {
		{
			rm.initRegisters();
		}
	}

	protected void emitMethodSuffix(boolean returnNull) {
		if (returnNull)
			emit.emit("movl", "$0", Register.EAX);
		emit.raw("leave");
		emit.raw("ret");
	}

	protected void emitBootstrap(List<? extends ClassDecl> astRoots) {

		// Instantiate Main
		emit.raw(".globl " + Config.MAIN);
		emit.label(Config.MAIN);
		emit.raw("enter $0, $0");
		// Create new Main object
		NewObject miref = new NewObject("Main");
		for (ClassDecl ast : astRoots) {
			if (ast.name.equals("Main"))
				miref.type = ast.sym;
		}
		// Call Main_main
		MethodCallExpr methCallExpr = new MethodCallExpr(miref, "main", new ArrayList<Expr>());
		methCallExpr.sym = ((ClassSymbol) typeSymbols.get("Main")).methods.get("main");
		eg.methodCall(methCallExpr, null);
		emit.mov(0, Register.EAX);

		emit.leave();
		emit.ret();
		// emit.raw(Config.EXIT);

		/*
		 * emit.raw(".globl " + Config.MAIN); emit.label(Config.MAIN);
		 * emit.jmp("Main_main");
		 */
	}
}