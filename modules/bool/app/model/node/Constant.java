package model.node;

import java.util.Set;
import java.util.TreeSet;

import model.Assignment;

public abstract class Constant implements BoolNode {
  
  public static final Constant FALSE = new Constant(false) {
    @Override
    public BoolNode negate() {
      return TRUE;
    }
  };
  
  public static final Constant TRUE = new Constant(true) {
    @Override
    public BoolNode negate() {
      return FALSE;
    }
  };
  
  private boolean value;
  
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
  
  public Constant getInstance(boolean value) {
    return value ? TRUE : FALSE;
  }
  
  @Override
  public Set<Character> getUsedVariables() {
    return new TreeSet<>();
  }
  
  @Override
  public String toString() {
    return getAsString(false);
  }
  
}
