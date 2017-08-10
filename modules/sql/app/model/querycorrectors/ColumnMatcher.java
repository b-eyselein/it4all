package model.querycorrectors;

import model.matching.Matcher;

public class ColumnMatcher extends Matcher<ColumnWrapper, ColumnMatch> {
  
  public ColumnMatcher() {
    super((colWrapper1, colWrapper2) -> colWrapper1.canMatchOther(colWrapper2));
  }
  
  @Override
  protected ColumnMatch instantiateMatch(ColumnWrapper arg1, ColumnWrapper arg2) {
    return new ColumnMatch(arg1, arg2);
  }
  
}
