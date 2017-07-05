package model.node;

import model.node.BinaryOperator.And;
import model.node.BinaryOperator.Equivalency;
import model.node.BinaryOperator.Implication;
import model.node.BinaryOperator.Nand;
import model.node.BinaryOperator.Nor;
import model.node.BinaryOperator.Or;
import model.node.BinaryOperator.Xor;

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

  public BoolNode instantiate(BoolNode... formulas) {
    switch(this) {
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
