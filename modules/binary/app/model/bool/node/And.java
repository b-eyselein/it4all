package model.bool.node;

import model.bool.tree.Assignment;

public class And extends BinaryOperator {
  public And(Node l, Node r) {
    super(l, r, "AND");
  }

  @Override
  public boolean evaluate(Assignment assignment) {
    return leftNode.evaluate(assignment) && rightNode.evaluate(assignment);
  }
}