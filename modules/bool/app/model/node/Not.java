package model.node;

import java.util.Set;

import model.tree.Assignment;

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
  
  @Override
  public Set<Character> getUsedVariables() {
    return node.getUsedVariables();
  }

}
