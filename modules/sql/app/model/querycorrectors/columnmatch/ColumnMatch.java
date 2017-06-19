package model.querycorrectors.columnmatch;

import model.matching.Match;

public abstract class ColumnMatch<C> extends Match<C> {
  
  public ColumnMatch(C theArg1, C theArg2) {
    super(theArg1, theArg2);
  }
  
  public abstract String describe();

}
