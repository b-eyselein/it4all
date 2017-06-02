package model.querycorrectors;

import model.matching.Match;

public class ColumnDefinitionMatch<T> extends Match<T> {

  public ColumnDefinitionMatch(T theArg1, T theArg2) {
    super(theArg1, theArg2);
  }

  @Override
  protected boolean analyze(T theArg1, T theArg2) {
    return false;
  }

}
