package model.querycorrectors;

import model.matching.Match;
import model.matching.MatchType;
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
    return sampleArg.getRest();
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
    // TODO Auto-generated method stub
    return null;
  }
  
}
