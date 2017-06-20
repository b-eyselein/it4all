package model.conditioncorrector;

import model.matching.Match;
import model.matching.MatchType;
import net.sf.jsqlparser.expression.BinaryExpression;
import play.twirl.api.Html;

public class BinaryExpressionMatch extends Match<BinaryExpression> {

  public BinaryExpressionMatch(BinaryExpression theArg1, BinaryExpression theArg2) {
    super(theArg1, theArg2);
  }

  @Override
  public Html describe() {
    return views.html.matchResult.render(this);
  }

  @Override
  protected MatchType analyze(BinaryExpression theArg1, BinaryExpression theArg2) {
    // FIXME: can only be successful match (see @BinaryExpressionBiPredicate...)
    return MatchType.SUCCESSFUL_MATCH;
  }

}
