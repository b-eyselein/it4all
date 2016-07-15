package model.boolescheAlgebra.BFTree;

public class BF_OR extends BinaryOperator {

  public BF_OR(Node l, Node r) {
    super(l, r, "OR");
  }

  @Override
  public boolean evaluate() {
    return (this.leftNode.evaluate() || this.rightNode.evaluate());
  }

}
