package model.bool.node;

import model.bool.tree.Assignment;

public class Nor extends BinaryOperator {
  public Nor(Node l, Node r) {
    super(l, r, "NOR");
  }

  @Override
  public boolean evaluate(Assignment assignment) {
    return !(leftNode.evaluate(assignment) || rightNode.evaluate(assignment));
  }
}