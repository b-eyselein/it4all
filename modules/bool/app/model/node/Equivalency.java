package model.node;

import model.tree.Assignment;

public class Equivalency extends BinaryOperator {

  public Equivalency(BoolNode l, BoolNode r) {
    super(l, r, "EQUIV");
  }

  @Override
  public boolean evaluate(Assignment assignment) {
    return leftNode.evaluate(assignment) == rightNode.evaluate(assignment);
  }
  
}