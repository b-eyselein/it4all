package model.node;

import java.util.Set;

import model.Assignment;

public class Not implements BoolNode {
  
  private final BoolNode childNode;
  
  public Not(BoolNode theChildNode) {
    childNode = theChildNode;
  }
  
  @Override
  public boolean evaluate(Assignment assignment) {
    return !childNode.evaluate(assignment);
  }
  
  @Override
  public String getAsString(boolean needsParanthesis) {
    String withoutParans = "NOT " + childNode.getAsString(needsParanthesis);
    return needsParanthesis ? "(" + withoutParans + ")" : withoutParans;
  }
  
  @Override
  public Set<Character> getUsedVariables() {
    return childNode.getUsedVariables();
  }
  
  @Override
  public BoolNode negate() {
    return childNode;
  }
  
}
