package model.mindmap.evaluation.enums;

public enum DifferenceResult {

	/**
	 * The Text of the user node and the solution text are the same
	 */
	CORRECT,
	
	/**
	 * The user solution misses the node
	 */
	MISSING, 
	
	/**
	 * The user solution differs in a node from the solution
	 */
	WRONG, 
	
	/**
	 * The user solution is almost correct in terms of evaluation definition
	 */
	PARTIALLY_CORRECT;
}
