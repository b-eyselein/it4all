package model.boolescheAlgebra.BFTree;

public class BF_IMPL extends BinaryOperator {

  public BF_IMPL(Node l, Node r) {
    super(l, r, "IMPL");
  }

  @Override
  public boolean evaluate() {
    return (!leftNode.evaluate()) || rightNode.evaluate();
  }

}
