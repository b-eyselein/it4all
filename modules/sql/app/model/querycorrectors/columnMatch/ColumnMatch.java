package model.querycorrectors.columnMatch;

import model.matching.Match;

public abstract class ColumnMatch<ColumnType> extends Match<ColumnType> {
  
  public ColumnMatch(ColumnType theArg1, ColumnType theArg2) {
    super(theArg1, theArg2);
  }
  
  public abstract String describe();

}
