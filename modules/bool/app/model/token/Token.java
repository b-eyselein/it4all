package model.token;

public interface Token {

  public static Token toToken(String token) {
    if(")".equals(token))
      return new ParanthesisToken(true);
    else if("(".equals(token))
      return new ParanthesisToken(false);
    else if(token.length() == 1)
      return new VariableToken(token.charAt(0));
    else
      return new OperatorToken(token);
  }

}
