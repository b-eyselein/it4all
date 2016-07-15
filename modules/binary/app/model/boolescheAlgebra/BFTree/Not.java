package model.boolescheAlgebra.BFTree;

public class Not implements Node {

  private Node node;

  public Not(Node k) {
    this.node = k;
  }

  @Override
  public boolean evaluate() {
    return !(node.evaluate());
  }

  @Override
  public String getAsString(boolean needsParanthesis) {
    return "NOT " + node.getAsString(true);
  }

}
