package model.querycorrectors;

import java.util.function.BiPredicate;

import model.CorrectionException;
import model.StringConsts;
import model.matching.Matcher;
import play.Logger;

public class ColumnMatcher extends Matcher<ColumnWrapper, ColumnMatch> {
  
  private static final BiPredicate<ColumnWrapper, ColumnWrapper> COL_WRAPPER_TEST = (colWrapper1, colWrapper2) -> {
    try {
      return colWrapper1.canMatchOther(colWrapper2);
    } catch (CorrectionException e) {
      Logger.error("There has been an error", e);
      return false;
    }
  };
  
  public ColumnMatcher() {
    super(StringConsts.COLUMNS_NAME, COL_WRAPPER_TEST, ColumnMatch::new);
  }
  
}
