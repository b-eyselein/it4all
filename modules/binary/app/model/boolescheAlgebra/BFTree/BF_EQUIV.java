package model.boolescheAlgebra.BFTree;

public class BF_EQUIV extends BinaryOperator {
  
  public BF_EQUIV(Node l, Node r) {
    super(l, r, "EQUIV");
  }
  
  @Override
  public boolean evaluate() {
    return leftNode.evaluate() == rightNode.evaluate();
  }
  
}
