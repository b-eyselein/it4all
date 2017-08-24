package model.conditioncorrector;

import model.matching.ScalaMatch;
import model.matching.MatchType;
import net.sf.jsqlparser.expression.Expression;

class ExpressionMatch(arg1: Option[Expression], arg2: Option[Expression]) extends ScalaMatch[Expression](arg1, arg2) {

  // FIXME: can only be successful match (see @ExpressionBiPredicate...)
  override def analyze(userArg: Expression, sampleArg: Expression) = MatchType.SUCCESSFUL_MATCH;

}
