package model;

import static model.ScalaNode.*;

public enum NodeType {
  
  NOT("nicht", 1), AND("und", 2), OR("oder", 3), IMPL("impl", 5), NAND("nund", 5), NOR("noder", 5), XOR("xoder",
      5), EQUIV("equiv", 5);
  
  private String germanOperator;
  
  private int precedence;
  
  private NodeType(String theGermanOperator, int thePrecedence) {
    germanOperator = theGermanOperator;
    precedence = thePrecedence;
  }
  
  public String getEnglishOperator() {
    return name().toLowerCase();
  }
  
  public String getGermanOperator() {
    return germanOperator;
  }
  
  public int getPrecende() {
    return precedence;
  }
  
  public ScalaNode instantiate(ScalaNode... formulas) {
    switch(this) {
    case NOT:
      return not(formulas[0]);
    case AND:
      return and(formulas[0], formulas[1]);
    case OR:
      return or(formulas[0], formulas[1]);
    case NAND:
      return nand(formulas[0], formulas[1]);
    case NOR:
      return nor(formulas[0], formulas[1]);
    case XOR:
      return xor(formulas[0], formulas[1]);
    case IMPL:
      return impl(formulas[0], formulas[1]);
    case EQUIV:
      return equiv(formulas[0], formulas[1]);
    default:
      throw new IllegalArgumentException("No Node instanstiated!");
    }
  }
  
}