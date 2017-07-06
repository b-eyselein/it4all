package model.node;

import java.util.Set;
import java.util.TreeSet;

import model.Assignment;

public final class Constant implements BoolNode {
  
  public static final Constant FALSE = new Constant(false);
  public static final Constant TRUE = new Constant(true);
  
  private final boolean value;
  
  private Constant(boolean theValue) {
    value = theValue;
  }
  
  @Override
  public boolean evaluate(Assignment assignment) {
    return value;
  }
  
  @Override
  public String getAsString(boolean needsParanthesis) {
    return value ? "1" : "0";
  }
  
  @Override
  public Set<Character> getUsedVariables() {
    return new TreeSet<>();
  }
  
  @Override
  public BoolNode negate() {
    return value ? FALSE : TRUE;
  }
  
  @Override
  public String toString() {
    return getAsString(false);
  }
  
}
