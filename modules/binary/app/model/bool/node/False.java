package model.bool.node;

import model.bool.tree.Assignment;

public class False implements Node {
  
  @Override
  public boolean evaluate(Assignment assignment) {
    return false;
  }
  
  @Override
  public String getAsString(boolean needsParanthesis) {
    return "0";
  }
  
  @Override
  public String toString() {
    return "0";
  }
}
