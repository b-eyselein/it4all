package model.boolescheAlgebra.BFTree;

public interface Node {
  
  /**
   * Evaluate this node recursively
   * 
   * @param assignment
   *
   * @return
   */
  public boolean evaluate(Assignment assignment);
  
  /**
   * Returns the formula represented by this node as a string with optional
   * parantheses
   *
   * @param needsParanthesis
   *          - true, if there is a parent node
   * @return Representation of this formula as String
   */
  public String getAsString(boolean needsParanthesis);
  
}
