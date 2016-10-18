package model.bool.node;

import java.util.Set;
import java.util.TreeSet;

import model.bool.tree.Assignment;

public class Variable implements Node {

  private char name;

  public Variable(char theName) {
    this.name = theName;
  }

  @Override
  public boolean evaluate(Assignment assignment) {
    return assignment.getAssignment(name);
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
  public String toString() {
    return Character.toString(name);
  }
}
