package model.node;

import java.util.Set;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import model.Assignment;

public abstract class BinaryOperator implements BoolNode {

  public static class And extends BinaryOperator {
    protected And(BoolNode l, BoolNode r) {
      super(l, r, "AND", (ln, rn) -> ln && rn);
    }

    @Override
    public BoolNode negate() {
      return new Nand(leftNode, rightNode);
    }
  }

  public static class Equivalency extends BinaryOperator {
    protected Equivalency(BoolNode l, BoolNode r) {
      super(l, r, "EQUIV", (ln, rn) -> ln == rn);
    }

    @Override
    public BoolNode negate() {
      return new Xor(leftNode, rightNode);
    }
  }

  public static class Implication extends BinaryOperator {
    protected Implication(BoolNode l, BoolNode r) {
      super(l, r, "IMPL", (ln, rn) -> !ln || rn);
    }

    @Override
    public BoolNode negate() {
      return new And(leftNode, rightNode.negate());
    }
  }

  public static class Nand extends BinaryOperator {
    protected Nand(BoolNode l, BoolNode r) {
      super(l, r, "NAND", (ln, rn) -> !(ln && rn));
    }

    @Override
    public BoolNode negate() {
      return new And(leftNode, rightNode);
    }
  }

  public static class Nor extends BinaryOperator {
    protected Nor(BoolNode l, BoolNode r) {
      super(l, r, "NOR", (ln, rn) -> !(ln || rn));
    }

    @Override
    public BoolNode negate() {
      return new Or(leftNode, rightNode);
    }
  }

  public static class Or extends BinaryOperator {
    protected Or(BoolNode l, BoolNode r) {
      super(l, r, "OR", (ln, rn) -> ln || rn);
    }

    @Override
    public BoolNode negate() {
      return new Nor(leftNode, rightNode);
    }
  }

  public static class Xor extends BinaryOperator {
    protected Xor(BoolNode l, BoolNode r) {
      super(l, r, "XOR", (ln, rn) -> ln ^ rn);
    }

    @Override
    public BoolNode negate() {
      return new Equivalency(leftNode, rightNode);
    }
  }

  protected final BoolNode leftNode;

  protected final BoolNode rightNode;

  protected final String operator;

  private final BiPredicate<Boolean, Boolean> evaluation;

  protected BinaryOperator(BoolNode theLeftNode, BoolNode theRightNode, String theOperator,
      BiPredicate<Boolean, Boolean> theEvaluation) {
    leftNode = theLeftNode;
    rightNode = theRightNode;
    operator = theOperator;
    evaluation = theEvaluation;
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
  public String toString() {
    return getAsString(false);
  }

}
