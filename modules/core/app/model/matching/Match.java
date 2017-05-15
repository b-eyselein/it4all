package model.matching;

public class Match<T> {
  
  protected T arg1;
  protected T arg2;
  
  public Match(T theArg1, T theArg2) {
    arg1 = theArg1;
    arg2 = theArg2;
  }
  
  public T getFirstArg() {
    return arg1;
  }
  
  public T getSecondArg() {
    return arg2;
  }
  
  public boolean isSuccessful() {
    return arg1.equals(arg2);
  }
  
}
