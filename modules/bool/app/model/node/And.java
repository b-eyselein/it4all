package model.node;

import model.tree.Assignment;

public class And extends BinaryOperator {

  public And(BoolNode l, BoolNode r) {
    super(l, r, "AND");
  }

  @Override
  public boolean evaluate(Assignment assignment) {
    return leftNode.evaluate(assignment) && rightNode.evaluate(assignment);
  }
  
}