package model.conditioncorrector;

import model.matching.Match;
import model.matching.MatchType;
import net.sf.jsqlparser.expression.Expression;

public class ExpressionMatch extends Match<Expression> {

  public ExpressionMatch(Expression theArg1, Expression theArg2) {
    super(theArg1, theArg2);
  }

  @Override
  protected MatchType analyze(Expression theArg1, Expression theArg2) {
    // FIXME: can only be successful match (see @ExpressionBiPredicate...)
    return MatchType.SUCCESSFUL_MATCH;
  }

}
