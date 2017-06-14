package model.querycorrectors.columnMatch;

import java.util.function.BiPredicate;

import model.matching.Matcher;

public abstract class ColumnMatcher<ColumnType> extends Matcher<ColumnType, ColumnMatch<ColumnType>> {
  
  public ColumnMatcher(BiPredicate<ColumnType, ColumnType> theEqualsTest) {
    super(theEqualsTest);
  }
  
  @Override
  protected abstract ColumnMatch<ColumnType> instantiateMatch(ColumnType arg1, ColumnType arg2);
  
}
