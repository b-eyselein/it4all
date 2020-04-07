package model.tools.sql.matcher

import model.core.matching.{Match, MatchType, Matcher}
import net.sf.jsqlparser.expression.operators.relational.ExpressionList

final case class ExpressionListMatch(
  matchType: MatchType,
  userArg: Option[ExpressionList],
  sampleArg: Option[ExpressionList]
) extends Match[ExpressionList]

object ExpressionListMatcher extends Matcher[ExpressionList, ExpressionListMatch] {

  override protected def canMatch(el1: ExpressionList, el2: ExpressionList): Boolean = el1.toString == el2.toString

  override protected def instantiateOnlySampleMatch(sa: ExpressionList): ExpressionListMatch =
    ExpressionListMatch(MatchType.ONLY_SAMPLE, None, Some(sa))

  override protected def instantiateOnlyUserMatch(ua: ExpressionList): ExpressionListMatch =
    ExpressionListMatch(MatchType.ONLY_USER, Some(ua), None)

  override protected def instantiateCompleteMatch(ua: ExpressionList, sa: ExpressionList): ExpressionListMatch =
    ExpressionListMatch(MatchType.SUCCESSFUL_MATCH, Some(ua), Some(sa))

}
