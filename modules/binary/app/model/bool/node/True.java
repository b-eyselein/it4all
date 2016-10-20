package model.bool.node;

import java.util.Set;
import java.util.TreeSet;

import model.bool.tree.Assignment;

public class True implements Node {
  
  @Override
  public boolean evaluate(Assignment assignment) {
    return true;
  }
  
  @Override
  public String getAsString(boolean needsParanthesis) {
    return "1";
  }
  
  @Override
  public Set<Character> getUsedVariables() {
    return new TreeSet<>();
  }
  
  @Override
  public String toString() {
    return "1";
  }
  
}
