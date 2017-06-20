package model.querycorrectors;

import model.matching.Match;
import play.twirl.api.Html;

public abstract class ColumnMatch<C> extends Match<C> {

  public ColumnMatch(C theArg1, C theArg2) {
    super(theArg1, theArg2);
  }

  public abstract boolean aliasesMatched();

  public abstract boolean colNamesMatched();

  @Override
  public Html describe() {
    return views.html.resultTemplates.columnResult.render(this);
  }
  
  public abstract String getFirstColAlias();

  public abstract String getFirstColName();

  public abstract String getSecondColAlias();

  public abstract String getSecondColName();

  public abstract boolean hasAlias();

}
