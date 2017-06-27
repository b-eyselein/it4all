package model.querycorrectors.select;

import model.matching.Match;
import model.matching.MatchType;
import net.sf.jsqlparser.expression.Expression;

public class GroupByMatch extends Match<Expression> {
  
  public GroupByMatch(Expression theUserArg, Expression theSampleArg) {
    super(theUserArg, theSampleArg);
  }

  @Override
  protected MatchType analyze(Expression theArg1, Expression theArg2) {
    return MatchType.SUCCESSFUL_MATCH;
  }

}
