package model.matching;

public class GenericMatch<T> extends Match<T> {

  public GenericMatch(T theArg1, T theArg2) {
    super(theArg1, theArg2);
  }

  @Override
  protected boolean analyze(T theArg1, T theArg2) {
    return theArg1.equals(theArg2);
  }

}
