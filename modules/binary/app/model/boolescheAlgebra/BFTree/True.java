package model.boolescheAlgebra.BFTree;

public class True implements Node {
  
  @Override
  public boolean evaluate(Assignment assignment) {
    return true;
  }
  
  @Override
  public String getAsString(boolean needsParanthesis) {
    return "1";
  }
  
  @Override
  public String toString() {
    return "1";
  }
  
}
