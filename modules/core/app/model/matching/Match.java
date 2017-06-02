package model.matching;

public abstract class Match<T> {

  protected T arg1;
  protected T arg2;
  private boolean successful;

  public Match(T theArg1, T theArg2) {
    arg1 = theArg1;
    arg2 = theArg2;

    successful = analyze(theArg1, theArg2);
  }

  public T getFirstArg() {
    return arg1;
  }

  public T getSecondArg() {
    return arg2;
  }

  public boolean isSuccessful() {
    return successful;
  }

  protected abstract boolean analyze(T theArg1, T theArg2);

}
