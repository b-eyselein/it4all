package model.token;

import java.util.List;

public class TokenGroup implements Token {

  private List<Token> tokens;

  public TokenGroup(List<Token> theTokens) {
    tokens = theTokens;
  }

  public List<Token> getTokens() {
    return tokens;
  }

}