package model.node;

import model.tree.Assignment;

public class Or extends BinaryOperator {

  public Or(Node l, Node r) {
    super(l, r, "OR");
  }

  @Override
  public boolean evaluate(Assignment assignment) {
    return leftNode.evaluate(assignment) || rightNode.evaluate(assignment);
  }
  
}