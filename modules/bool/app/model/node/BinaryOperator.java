package model.node;

import java.util.Set;

public abstract class BinaryOperator implements BoolNode {

  protected BoolNode leftNode;
  protected BoolNode rightNode;

  protected String operator;

  public BinaryOperator(BoolNode theLeftNode, BoolNode theRightNode, String theOperator) {
    leftNode = theLeftNode;
    rightNode = theRightNode;
    operator = theOperator;
  }

  @Override
  public String getAsString(boolean needsParanthesis) {
    String s = leftNode.getAsString(true) + " " + operator + " " + rightNode.getAsString(true);
    return needsParanthesis ? "(" + s + ")" : s;
  }

  @Override
  public Set<Character> getUsedVariables() {
    Set<Character> ret = leftNode.getUsedVariables();
    ret.addAll(rightNode.getUsedVariables());
    return ret;
  }

}
