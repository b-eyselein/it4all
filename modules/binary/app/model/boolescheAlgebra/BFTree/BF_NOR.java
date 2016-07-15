package model.boolescheAlgebra.BFTree;

public class BF_NOR extends BinaryOperator {

  public BF_NOR(Node l, Node r) {
    super(l, r, "NOR");
  }

  @Override
  public boolean evaluate() {
    return !(leftNode.evaluate() || rightNode.evaluate());
  }

}
