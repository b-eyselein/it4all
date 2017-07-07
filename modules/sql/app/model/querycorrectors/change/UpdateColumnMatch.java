package model.querycorrectors.change;

import model.matching.MatchType;
import model.querycorrectors.ColumnMatch;
import net.sf.jsqlparser.schema.Column;

public class UpdateColumnMatch extends ColumnMatch<Column> {

  public UpdateColumnMatch(Column theArg1, Column theArg2) {
    super(theArg1, theArg2);
  }

  @Override
  public boolean restMatched() {
    return false;
  }

  @Override
  public String getFirstRest() {
    return "";
  }

  @Override
  public String getFirstColName() {
    return userArg == null ? "--" : userArg.getColumnName();
  }

  @Override
  public String getSecondRest() {
    return "";
  }

  @Override
  public String getSecondColName() {
    return sampleArg == null ? "--" : sampleArg.getColumnName();
  }

  @Override
  public boolean hasAlias() {
    return false;
  }

  @Override
  protected MatchType analyze(Column theArg1, Column theArg2) {
    return MatchType.SUCCESSFUL_MATCH;
  }

}
