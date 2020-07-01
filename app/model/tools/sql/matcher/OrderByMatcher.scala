package model.tools.sql.matcher

import model.matching._
import model.points._
import net.sf.jsqlparser.statement.select.OrderByElement

final case class OrderByMatch(
  matchType: MatchType,
  userArg: Option[OrderByElement],
  sampleArg: Option[OrderByElement]
) extends Match[OrderByElement] {

  override def points: Points = if (matchType == MatchType.SUCCESSFUL_MATCH) singleHalfPoint else zeroPoints

  override def maxPoints: Points = sampleArg match {
    case None    => zeroPoints
    case Some(_) => singleHalfPoint
  }

}

object OrderByMatcher extends Matcher[OrderByElement, OrderByMatch] {

  override protected def canMatch(o1: OrderByElement, o2: OrderByElement): Boolean =
    o1.getExpression.toString == o2.getExpression.toString

  override protected def instantiateOnlySampleMatch(sa: OrderByElement): OrderByMatch =
    OrderByMatch(MatchType.ONLY_SAMPLE, None, Some(sa))

  override protected def instantiateOnlyUserMatch(ua: OrderByElement): OrderByMatch =
    OrderByMatch(MatchType.ONLY_USER, Some(ua), None)

  override protected def instantiateCompleteMatch(ua: OrderByElement, sa: OrderByElement): OrderByMatch =
    OrderByMatch(MatchType.SUCCESSFUL_MATCH, Some(ua), Some(sa))

}
