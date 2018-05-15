package cd.ir;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cd.ir.Ast.Expr;

/** Represents the control flow graph of a single method. */
public class ControlFlowGraph {
	/** Hashmap that maps all local vars in method to definition_label */
	public Map<String, List<String>> graphDefinitionVarSet = new HashMap<>();

	/** Hashmap that maps all used local vars in method to definition_label */
	public Map<String, List<String>> graphUseVarSet = new HashMap<>();

	/** Hashmap that maps all definition_labels to the respective variable */
	public Map<String, String> definitionVarMap = new HashMap<>();

	/**
	 * A Use-Definition Chain which consists of
	 * a use, U, of a variable,
	 * and all the definitions, D, of that variable that can reach that use without any other intervening definitions.
	 *
	 * To find out all def-use-chains for variable d, do the following steps:
	 * 1.Search for the first time, the variable is defined (write access).
	 * In this case it is "d=b" (l.3)
	 * 2.Search for the first time, the variable is read.
	 * In this case it is "return d"
	 * 3.Write down this information in the following style:
	 * [name of the variable you are creating a def-use-chain for, the concrete write access, the concrete read access]
	 * In this case it is: [d, d=b, return d]
	 * Repeat this steps in the following style: combine each write access with each read access (but NOT the other way round).
	 */
	public Map<String, String> defUseChain = new HashMap<>();

	/**
	 * Definition-Use Chain which consists
	 * of a definition, d_8, of a variable
	 * and all the uses, U (x, y, z), reachable from that definition without any other intervening definitions.
	 *
	 * 1. Set definitions in statement s(0)
	 * 2. For each i in [1,n], find live definitions that have use in statement s(i)
	 * 3. Make a link among definitions and uses
	 * 4. Set the statement s(i), as definition statement
	 * 5. Kill previous definitions
	 */
	public Map<String, String> useDefChain = new HashMap<>();

	public BasicBlock start, end;
	public final List<BasicBlock> allBlocks = new ArrayList<>();
	
	public int count() {
		return allBlocks.size();
	}
	
	public BasicBlock newBlock() {
		BasicBlock blk = new BasicBlock(count());
		allBlocks.add(blk);
		return blk;
	}
	
	/**
	 * Given a list of basic blocks that do not yet have successors,
	 * merges their control flows into a single successor and returns
	 * the new successor.
	 */
	public BasicBlock join(BasicBlock... pred) { //... means zero or more BasicBlock objects can be passed
		BasicBlock result = newBlock();
		for (BasicBlock p : pred) {
			assert p.condition == null;
			assert p.successors.size() == 0;
			p.successors.add(result);
			result.predecessors.add(p);
		}
		return result;
	}
	
	/** 
	 * Terminates {@code blk} so that it evaluates {@code cond},
	 * and creates two new basic blocks, one for the case where
	 * the result is true, and one for the case where the result is
	 * false.
	 */
	public void terminateInCondition(BasicBlock blk, Expr cond) {
		assert blk.condition == null;
		assert blk.successors.size() == 0;
		blk.condition = cond;
		blk.successors.add(newBlock());
		blk.successors.add(newBlock());
		blk.trueSuccessor().predecessors.add(blk);
		blk.falseSuccessor().predecessors.add(blk);
	}

	public void connect(BasicBlock from, BasicBlock to) {
		to.predecessors.add(from);
		from.successors.add(to);
	}
}
