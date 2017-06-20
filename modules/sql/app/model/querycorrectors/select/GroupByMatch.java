package model.querycorrectors.select;

import model.matching.Match;
import model.matching.MatchType;
import net.sf.jsqlparser.expression.Expression;
import play.twirl.api.Html;

public class GroupByMatch extends Match<Expression> {
  
  public GroupByMatch(Expression theUserArg, Expression theSampleArg) {
    super(theUserArg, theSampleArg);
  }

  @Override
  public Html describe() {
    return views.html.matchResult.render(this);
  }

  @Override
  protected MatchType analyze(Expression theArg1, Expression theArg2) {
    // TODO Auto-generated method stub
    return null;
  }

}
