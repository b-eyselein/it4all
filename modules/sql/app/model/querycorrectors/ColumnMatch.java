package model.querycorrectors;

import model.CorrectionException;
import model.matching.Match;
import model.matching.MatchType;
import play.Logger;
import play.twirl.api.Html;

public class ColumnMatch extends Match<ColumnWrapper> {

  protected boolean hasAlias;

  protected boolean restMatched;

  public ColumnMatch(ColumnWrapper theArg1, ColumnWrapper theArg2) {
    super(theArg1, theArg2);
  }

  public boolean colNamesMatched() {
    return matchType == MatchType.SUCCESSFUL_MATCH || matchType == MatchType.UNSUCCESSFUL_MATCH;
  }

  @Override
  public Html describe() {
    return views.html.resultTemplates.columnResult.render(this);
  }

  public String getFirstColName() {
    return userArg.getColName();
  }

  public String getFirstRest() {
    return userArg.getRest();
  }

  public String getSecondColName() {
    return sampleArg.getColName();
  }

  public String getSecondRest() {
    return sampleArg.getRest();
  }

  public boolean hasAlias() {
    return hasAlias;
  }

  public boolean restMatched() {
    return restMatched;
  }

  @Override
  protected MatchType analyze(ColumnWrapper theArg1, ColumnWrapper theArg2) {
    try {
      return theArg1.match(theArg2);
    } catch (CorrectionException e) {
      Logger.error("There has been an error:", e);
      return MatchType.UNSUCCESSFUL_MATCH;
    }
  }

}
