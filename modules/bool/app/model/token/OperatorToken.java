package model.token;

public class OperatorToken implements Token {

  private String operator;

  public OperatorToken(String theOperator) {
    operator = theOperator;
  }

  public String getOperator() {
    return operator;
  }

}
