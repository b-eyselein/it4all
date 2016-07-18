package model.bool.node;

public enum NodeType {
  
  AND("und"), OR("oder"), NOT("nicht"), NAND("nund"), NOR("noder"), XOR("xoder"), IMPL("impl"), EQUIV("equiv");
  
  public static NodeType get(String highestOperator) {
    for(NodeType type: values())
      if(highestOperator.equals(type.getEnglishOperator()))
        return type;
    return null;
  }
  
  private String germanOperator;
  
  private NodeType(String theGermanOperator) {
    germanOperator = theGermanOperator;
  }
  
  public String getEnglishOperator() {
    return name().toLowerCase();
  }
  
  public String getGermanOperator() {
    return germanOperator;
  }
  
  public Node instantiate(Node... formulas) {
    switch(this) {
    case NOT:
      return new Not(formulas[0]);
    case AND:
      return new And(formulas[0], formulas[1]);
    case OR:
      return new Or(formulas[0], formulas[1]);
    case NAND:
      return new Nand(formulas[0], formulas[1]);
    case NOR:
      return new Nor(formulas[0], formulas[1]);
    case XOR:
      return new Xor(formulas[0], formulas[1]);
    case IMPL:
      return new Implication(formulas[0], formulas[1]);
    case EQUIV:
      return new Equivalency(formulas[0], formulas[1]);
    default:
      throw new IllegalArgumentException("No Node instanstiated!");
    }
  }
  
}
