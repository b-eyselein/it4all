package model.boolescheAlgebra.BFTree;

public class BF_NAND extends BinaryOperator {

  public BF_NAND(Node l, Node r) {
    super(l, r, "NAND");
  }

  @Override
  public boolean evaluate() {
    return !(leftNode.evaluate() && rightNode.evaluate());
  }

}
