package model.boolescheAlgebra.BFTree;

public class Not implements Node {
  
  private Node node;
  
  public Not(Node k) {
    node = k;
  }
  
  @Override
  public boolean evaluate(Assignment assignment) {
    return !node.evaluate(assignment);
  }
  
  @Override
  public String getAsString(boolean needsParanthesis) {
    return "NOT " + node.getAsString(true);
  }
  
}
