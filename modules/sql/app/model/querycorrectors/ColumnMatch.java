package model.querycorrectors;

import model.matching.Match;
import model.matching.MatchType;
import play.twirl.api.Html;

public abstract class ColumnMatch<C> extends Match<C> {
  
  public ColumnMatch(C theArg1, C theArg2) {
    super(theArg1, theArg2);
  }
  
  public abstract boolean restMatched();
  
  public boolean colNamesMatched() {
    return matchType == MatchType.SUCCESSFUL_MATCH || matchType == MatchType.UNSUCCESSFUL_MATCH;
  }
  
  @Override
  public Html describe() {
    return views.html.resultTemplates.columnResult.render(this);
  }
  
  public abstract String getFirstRest();
  
  public abstract String getFirstColName();
  
  public abstract String getSecondRest();
  
  public abstract String getSecondColName();
  
  public abstract boolean hasAlias();
  
}
