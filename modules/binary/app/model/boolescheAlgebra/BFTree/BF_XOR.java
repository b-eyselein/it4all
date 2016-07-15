package model.boolescheAlgebra.BFTree;

public class BF_XOR extends BinaryOperator {

  public BF_XOR(Node l, Node r) {
    super(l, r, "XOR");
  }

  @Override
  public boolean evaluate() {
    return (this.leftNode.evaluate() ^ this.rightNode.evaluate());
  }

}
