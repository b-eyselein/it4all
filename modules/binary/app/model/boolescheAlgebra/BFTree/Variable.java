package model.boolescheAlgebra.BFTree;

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
    return name + "";
  }

  public char getVariable() {
    return name;
  }

  @Override
  public String toString() {
    return name + "";
  }
}
