package model.tree;

import java.util.Set;

import model.node.BoolNode;

public class BoolFormula {
  
  private BoolNode rootNode;
  
  public BoolFormula(BoolNode theRootNode) {
    rootNode = theRootNode;
  }
  
  public boolean evaluate(Assignment assignment) {
    return rootNode.evaluate(assignment);
  }
  
  public int getAnzahlVariablen() {
    return getVariables().size();
  }
  
  public Set<Character> getVariables() {
    return rootNode.getUsedVariables();
  }
  
  @Override
  public String toString() {
    return rootNode.getAsString(false);
  }
  
}
