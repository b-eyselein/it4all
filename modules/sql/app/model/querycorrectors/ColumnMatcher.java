package model.querycorrectors;

import model.CorrectionException;
import model.matching.Matcher;
import play.Logger;

public class ColumnMatcher extends Matcher<ColumnWrapper, ColumnMatch> {

  public ColumnMatcher() {
    super((colWrapper1, colWrapper2) -> {
      try {
        return colWrapper1.canMatchOther(colWrapper2);
      } catch (CorrectionException e) {
        Logger.error("There has been an error", e);
        return false;
      }
    });
  }

  @Override
  protected ColumnMatch instantiateMatch(ColumnWrapper arg1, ColumnWrapper arg2) {
    return new ColumnMatch(arg1, arg2);
  }

}
