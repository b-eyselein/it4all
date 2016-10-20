package model.bool.node;

import model.bool.tree.Assignment;

public class Xor extends BinaryOperator {

  public Xor(Node l, Node r) {
    super(l, r, "XOR");
  }
  
  @Override
  public boolean evaluate(Assignment assignment) {
    return leftNode.evaluate(assignment) ^ rightNode.evaluate(assignment);
  }

}