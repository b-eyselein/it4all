package model.boolescheAlgebra.BFTree;

public class BF_Variable implements Node {
  
  private String name;
  private boolean wert;
  
  public BF_Variable(String s) {
    this.name = s;
  }
  
  @Override
  public boolean evaluate() {
    return this.wert;
  }
  
  @Override
  public String getAsString(boolean needsParanthesis) {
    return name;
  }
  
  public void setWert(boolean newValue) {
    wert = newValue;
  }
  
  @Override
  public String toString() {
    return name;
  }
}
