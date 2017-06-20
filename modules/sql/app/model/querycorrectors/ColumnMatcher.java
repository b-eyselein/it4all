package model.querycorrectors;

import java.util.function.BiPredicate;

import model.matching.Matcher;

public abstract class ColumnMatcher<C> extends Matcher<C, ColumnMatch<C>> {
  
  public ColumnMatcher(BiPredicate<C, C> theEqualsTest) {
    super(theEqualsTest);
  }
  
  @Override
  protected abstract ColumnMatch<C> instantiateMatch(C arg1, C arg2);
  
}
