package model.boolescheAlgebra.BFTree;

public abstract class BinaryOperator implements Node {

  public static class And extends BinaryOperator {
    public And(Node l, Node r) {
      super(l, r, "AND");
    }

    @Override
    public boolean evaluate() {
      return leftNode.evaluate() && rightNode.evaluate();
    }
  }

  public static class Equivalent extends BinaryOperator {
    public Equivalent(Node l, Node r) {
      super(l, r, "EQUIV");
    }

    @Override
    public boolean evaluate() {
      return leftNode.evaluate() == rightNode.evaluate();
    }
  }

  public static class Implication extends BinaryOperator {
    public Implication(Node l, Node r) {
      super(l, r, "IMPL");
    }

    @Override
    public boolean evaluate() {
      return (!leftNode.evaluate()) || rightNode.evaluate();
    }
  }

  public static class NAnd extends BinaryOperator {
    public NAnd(Node l, Node r) {
      super(l, r, "NAND");
    }

    @Override
    public boolean evaluate() {
      return !(leftNode.evaluate() && rightNode.evaluate());
    }
  }

  public static class NOr extends BinaryOperator {
    public NOr(Node l, Node r) {
      super(l, r, "NOR");
    }

    @Override
    public boolean evaluate() {
      return !(leftNode.evaluate() || rightNode.evaluate());
    }
  }

  public static class Or extends BinaryOperator {
    public Or(Node l, Node r) {
      super(l, r, "OR");
    }

    @Override
    public boolean evaluate() {
      return leftNode.evaluate() || rightNode.evaluate();
    }
  }

  public static class Xor extends BinaryOperator {
    public Xor(Node l, Node r) {
      super(l, r, "XOR");
    }

    @Override
    public boolean evaluate() {
      return leftNode.evaluate() ^ rightNode.evaluate();
    }
  }

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
