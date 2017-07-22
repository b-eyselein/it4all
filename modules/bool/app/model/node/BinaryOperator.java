package model.node;

import java.util.Set;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import model.Assignment;

public abstract class BinaryOperator implements BoolNode {
  
  public static class And extends BinaryOperator {
    protected And(BoolNode l, BoolNode r) {
      super(l, r, "AND", (ln, rn) -> ln && rn, n -> new Nand(n.leftNode, n.rightNode));
    }
  }
  
  public static class Equivalency extends BinaryOperator {
    protected Equivalency(BoolNode l, BoolNode r) {
      super(l, r, "EQUIV", (ln, rn) -> ln == rn, n -> new Xor(n.leftNode, n.rightNode));
    }
  }
  
  public static class Implication extends BinaryOperator {
    protected Implication(BoolNode l, BoolNode r) {
      super(l, r, "IMPL", (ln, rn) -> !ln || rn, n -> new And(n.leftNode, n.rightNode.negate()));
    }
  }
  
  public static class Nand extends BinaryOperator {
    protected Nand(BoolNode l, BoolNode r) {
      super(l, r, "NAND", (ln, rn) -> !(ln && rn), n -> new And(n.leftNode, n.rightNode));
    }
  }
  
  public static class Nor extends BinaryOperator {
    protected Nor(BoolNode l, BoolNode r) {
      super(l, r, "NOR", (ln, rn) -> !(ln || rn), n -> new Or(n.leftNode, n.rightNode));
    }
  }
  
  public static class Or extends BinaryOperator {
    protected Or(BoolNode l, BoolNode r) {
      super(l, r, "OR", (ln, rn) -> ln || rn, n -> new Nor(n.leftNode, n.rightNode));
    }
  }
  
  public static class Xor extends BinaryOperator {
    protected Xor(BoolNode l, BoolNode r) {
      super(l, r, "XOR", (ln, rn) -> ln ^ rn, n -> new Equivalency(n.leftNode, n.rightNode));
    }
  }
  
  private final BoolNode leftNode;
  private final BoolNode rightNode;
  
  private final String operator;
  
  private final BiPredicate<Boolean, Boolean> evaluation;
  private final Function<BinaryOperator, BoolNode> negation;
  
  protected BinaryOperator(BoolNode theLeftNode, BoolNode theRightNode, String theOperator,
      BiPredicate<Boolean, Boolean> theEvaluation, Function<BinaryOperator, BoolNode> theNegation) {
    leftNode = theLeftNode;
    rightNode = theRightNode;
    operator = theOperator;
    evaluation = theEvaluation;
    negation = theNegation;
  }
  
  @Override
  public boolean evaluate(Assignment assignment) {
    return evaluation.test(leftNode.evaluate(assignment), rightNode.evaluate(assignment));
  }
  
  @Override
  public String getAsString(boolean needsParanthesis) {
    String s = leftNode.getAsString(true) + " " + operator + " " + rightNode.getAsString(true);
    return needsParanthesis ? "(" + s + ")" : s;
  }
  
  @Override
  public Set<Character> getUsedVariables() {
    return Stream.concat(leftNode.getUsedVariables().stream(), rightNode.getUsedVariables().stream())
        .collect(Collectors.toSet());
  }
  
  @Override
  public BoolNode negate() {
    return negation.apply(this);
  }
  
  @Override
  public String toString() {
    return getAsString(false);
  }
  
}
