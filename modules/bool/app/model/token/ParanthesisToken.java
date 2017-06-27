package model.token;

public class ParanthesisToken implements Token {
  
  private boolean isOpening;
  
  public ParanthesisToken(boolean openingParan) {
    isOpening = openingParan;
  }
  
  public boolean isOpening() {
    return isOpening;
  }

}
