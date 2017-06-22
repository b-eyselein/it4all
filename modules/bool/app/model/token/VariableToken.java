package model.token;

public class VariableToken implements Token {

  private char variable;

  public VariableToken(char theVariable) {
    variable = theVariable;
  }

  public char getVariable() {
    return variable;
  }

}
