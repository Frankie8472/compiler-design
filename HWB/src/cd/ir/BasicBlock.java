package cd.ir;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cd.ir.Ast.Expr;
import cd.ir.Ast.Stmt;

/** 
 * Node in a control flow graph.  New instances should be created
 * via the methods in {@link ControlFlowGraph}.  
 * Basic blocks consist of a list of statements ({@link #stmts}) which are
 * executed at runtime.  When the basic block ends, control flows into its 
 * {@link #successors}.  If the block has more than one successor, it must also
 * have a non-{@code null} value for {@link #condition}, which describes an expression
 * that will determine which successor to take.  Basic blocks also have fields 
 * for storing the parent and children in the dominator tree.  These are generally computed
 * in a second pass once the graph is fully built.
 * 
 * Your team will have to write code that builds the control flow graph and computes the
 * relevant dominator information. */
public class BasicBlock {
	
	/** 
	 * Unique numerical index assigned by CFG builder between 0 and the total number of
	 * basic blocks.  Useful for indexing into arrays and the like. 
	 */
	public final int index;

	/** 
	 * List of predecessor blocks in the flow graph (i.e., blocks for 
	 * which {@code this} is a successor). 
	 */
	public final List<BasicBlock> predecessors = new ArrayList<BasicBlock>();
	
	/** 
	 * List of successor blocks in the flow graph (those that come after the
	 * current block).  This list is always either of size 0, 1 or 2: 1 indicates
	 * that control flow continues directly into the next block, and 2 indicates
	 * that control flow goes in one of two directions, depending on the
	 * value that results when {@link #condition} is evaluated at runtime.
	 * If there are two successors, then the 0th entry is taken when {@code condition}
	 * evaluates to {@code true}.
	 * @see #trueSuccessor()
	 * @see #falseSuccessor()
	 */
	public final List<BasicBlock> successors = new ArrayList<BasicBlock>();
	
	/**
	 * List of statements in this basic block.
	 */
	public final List<Stmt> stmts = new ArrayList<>();
	
	/** 
	 * If non-null, indicates that this basic block should have
	 * two successors.  Control flows to the first successor if
	 * this condition evaluates at runtime to true, otherwise to
	 * the second successor.  If null, the basic block should have
	 * only one successor. 
	 */
	public Expr condition;
	
	/**
	 * Parent of this basic block in the dominator tree (initially null until computed). 
	 * Otherwise known as the immediate dominator.
	 */
	public BasicBlock dominatorTreeParent = null;
	
	/**
	 * Children of this basic block in the dominator tree (initially empty until
	 * computed).
	 */
	public final List<BasicBlock> dominatorTreeChildren = new ArrayList<BasicBlock>();
	
	/**
	 * Contains the dominance frontier of this block.  A block b is in the dominance
	 * frontier of another block c if c does not dominate b, but c DOES dominate a 
	 * predecessor of b.  
	 */
	public final Set<BasicBlock> dominanceFrontier = new HashSet<BasicBlock>();
	
	public BasicBlock(int index) {
		this.index = index;
	}
	
	public BasicBlock trueSuccessor() {
		assert this.condition != null;
		return this.successors.get(0);
	}

	public BasicBlock falseSuccessor() {
		assert this.condition != null;
		return this.successors.get(1);
	}
	
	@Override
	public String toString() {
		return "BB"+index;
	}

	/**
	 * graphDefinitionVarSet, all d_X in this block <br>
	 * Index is equal to <code>stmts</code> index
	 **/
	public List<String> blockDefinitionSet = new ArrayList<>();

	/**
	 * kill B = { d | d is killed in B } <br>
	 * Method-local
	 */
	public List<String> kill = new ArrayList<>();

	/**
	 * gen B ={ d | d appears in B and no subsequent statement in B kills d } <br>
	 * Method-local
	 */
	public List<String> gen = new ArrayList<>();

	/**
	 * use B = { var | var is used in B prior to any definition of var in B } <br>
	 * In use if there is a chance that the value is used
	 **/
	public List<String> use = new ArrayList<>();

	/**
	 * def B = { var | var is defined in B prior to any use of var in B } <br>
	 * In def only if we are sure the variable is set
	 */
	public List<String> def = new ArrayList<>();

	/* todo: The set of alive definitions at statement i is denoted as A(i) and the number of alive definitions as |A(i)|.
	 * (A(i) is a simple but powerful concept: theoretical and practical results in space complexity theory,
	 * access complexity(I/O complexity), register allocation and cache locality exploitation are based on A(i).)
	 */
}
