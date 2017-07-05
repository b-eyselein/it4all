package model.node;

import model.tree.Assignment;

public class Nand extends BinaryOperator {
  
  public Nand(BoolNode l, BoolNode r) {
    super(l, r, "NAND");
  }

  @Override
  public boolean evaluate(Assignment assignment) {
    return !(leftNode.evaluate(assignment) && rightNode.evaluate(assignment));
  }
  
}