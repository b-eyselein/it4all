package model.node;

import model.node.BinaryOperator.And;
import model.node.BinaryOperator.Equivalency;
import model.node.BinaryOperator.Implication;
import model.node.BinaryOperator.Nand;
import model.node.BinaryOperator.Nor;
import model.node.BinaryOperator.Or;
import model.node.BinaryOperator.Xor;

public enum NodeType {
  
  NOT("nicht", 1) {
    @Override
    public BoolNode instantiate(BoolNode... formulas) {
      return new Not(formulas[0]);
    }
  },

  AND("und", 2) {
    @Override
    public BoolNode instantiate(BoolNode... formulas) {
      return new And(formulas[0], formulas[1]);
    }
  },

  OR("oder", 3) {
    @Override
    public BoolNode instantiate(BoolNode... formulas) {
      return new Or(formulas[0], formulas[1]);
    }
  },

  IMPL("impl", 5) {
    @Override
    public BoolNode instantiate(BoolNode... formulas) {
      return new Implication(formulas[0], formulas[1]);
    }
  },
  
  NAND("nund", 5) {
    @Override
    public BoolNode instantiate(BoolNode... formulas) {
      return new Nand(formulas[0], formulas[1]);
    }
  },
  
  NOR("noder", 5) {
    @Override
    public BoolNode instantiate(BoolNode... formulas) {
      return new Nor(formulas[0], formulas[1]);
    }
  },
  
  XOR("xoder", 5) {
    @Override
    public BoolNode instantiate(BoolNode... formulas) {
      return new Xor(formulas[0], formulas[1]);
    }
  },
  
  EQUIV("equiv", 5) {
    @Override
    public BoolNode instantiate(BoolNode... formulas) {
      return new Equivalency(formulas[0], formulas[1]);
    }
  };
  
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
  
  public abstract BoolNode instantiate(BoolNode... formulas);
  
}
