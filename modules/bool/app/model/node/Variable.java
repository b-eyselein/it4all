package model.node;

import java.util.Set;
import java.util.TreeSet;

import model.Assignment;

public class Variable implements BoolNode {

  private final boolean negated;
  private final char name;

  public Variable(boolean isNegated, char theName) {
    negated = isNegated;
    name = theName;
  }

  public Variable(char theName) {
    this(false, theName);
  }

  @Override
  public boolean evaluate(Assignment assignment) {
    return negated ^ assignment.get(name);
  }

  @Override
  public String getAsString(boolean needsParanthesis) {
    return Character.toString(name);
  }

  @Override
  public Set<Character> getUsedVariables() {
    TreeSet<Character> ret = new TreeSet<>();
    ret.add(name);
    return ret;
  }

  public char getVariable() {
    return name;
  }

  @Override
  public BoolNode negate() {
    return new Variable(!negated, name);
  }

  @Override
  public String toString() {
    return (negated ? "NOT " : "") + Character.toString(name);
  }

}
