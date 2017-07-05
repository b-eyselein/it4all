package model.node;

import java.util.Set;

import model.Assignment;
import model.node.BinaryOperator.And;
import model.node.BinaryOperator.Or;

public interface BoolNode {
  
  public static BoolNode and(BoolNode left, BoolNode right) {
    return new And(left, right);
  }
  
  public static BoolNode or(BoolNode left, BoolNode right) {
    return new Or(left, right);
  }
  
  public abstract boolean evaluate(Assignment assignment);
  
  public abstract String getAsString(boolean needsParanthesis);
  
  public abstract Set<Character> getUsedVariables();
  
  public abstract BoolNode negate();
  
}
