package model.boolescheAlgebra.BFTree;

public class BF_AND extends BinaryOperator {

  public BF_AND(Node l, Node r) {
    super(l, r, "AND");
  }

  @Override
  public boolean evaluate() {
    return (leftNode.evaluate() && rightNode.evaluate());
  }

}
