package model.boolescheAlgebra.BFTree;

public class BF_NOT implements Node {

  private Node knoten;

  public BF_NOT(Node k) {
    this.knoten = k;
  }

  @Override
  public boolean evaluate() {
    return !(this.knoten.evaluate());
  }

  @Override
  public String getAsString(boolean needsParanthesis) {
    return "NOT " + knoten.getAsString(true);
  }

}
