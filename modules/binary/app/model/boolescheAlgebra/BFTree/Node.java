package model.boolescheAlgebra.BFTree;

public interface Node {

  /**
   * Evaluate this node recursively
   *
   * @return
   */
  public boolean evaluate();

  public String getAsString(boolean needsParanthesis);

}
