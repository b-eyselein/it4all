package model.node;

import java.util.Set;
import java.util.TreeSet;

import model.tree.Assignment;

public class False implements BoolNode {
  
  @Override
  public boolean evaluate(Assignment assignment) {
    return false;
  }
  
  @Override
  public String getAsString(boolean needsParanthesis) {
    return "0";
  }
  
  @Override
  public Set<Character> getUsedVariables() {
    return new TreeSet<>();
  }

  @Override
  public String toString() {
    return "0";
  }

}
