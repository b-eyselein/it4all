package model.bool.node;

public abstract class BinaryOperator implements Node {
  
  protected Node leftNode;
  protected Node rightNode;
  
  protected String operator;
  
  public BinaryOperator(Node theLeftNode, Node theRightNode, String theOperator) {
    leftNode = theLeftNode;
    rightNode = theRightNode;
    operator = theOperator;
  }
  
  @Override
  public String getAsString(boolean needsParanthesis) {
    String s = leftNode.getAsString(true) + " " + operator + " " + rightNode.getAsString(true);
    if(needsParanthesis)
      return "(" + s + ")";
    else
      return s;
  }
}
