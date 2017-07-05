package model.tree;

import java.util.Set;

import model.node.Node;

public class BoolFormula {

  private Node rootNode;

  private Set<Character> variables;

  public BoolFormula(Node theRootNode) {
    rootNode = theRootNode;
    variables = theRootNode.getUsedVariables();
  }

  public boolean evaluate(Assignment assignment) {
    return rootNode.evaluate(assignment);
  }

  public int getAnzahlVariablen() {
    return variables.size();
  }

  public Set<Character> getVariables() {
    return variables;
  }

  @Override
  public String toString() {
    return rootNode.getAsString(false);
  }

}
