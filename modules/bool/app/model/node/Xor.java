package model.node;

import model.tree.Assignment;

public class Xor extends BinaryOperator {
  
  public Xor(BoolNode l, BoolNode r) {
    super(l, r, "XOR");
  }

  @Override
  public boolean evaluate(Assignment assignment) {
    return leftNode.evaluate(assignment) ^ rightNode.evaluate(assignment);
  }
  
}