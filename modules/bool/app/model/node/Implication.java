package model.node;

import model.tree.Assignment;

public class Implication extends BinaryOperator {

  public Implication(Node l, Node r) {
    super(l, r, "IMPL");
  }

  @Override
  public boolean evaluate(Assignment assignment) {
    return (!leftNode.evaluate(assignment)) || rightNode.evaluate(assignment);
  }
  
}