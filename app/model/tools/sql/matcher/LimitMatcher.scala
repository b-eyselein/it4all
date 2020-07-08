package model.tools.sql.matcher

import model.matching.{Match, MatchType, Matcher}
import model.points._
import net.sf.jsqlparser.statement.select.Limit

final case class LimitMatch(
  matchType: MatchType,
  userArg: Option[Limit],
  sampleArg: Option[Limit]
) extends Match[Limit] {

  override def points: Points = if (matchType == MatchType.SUCCESSFUL_MATCH) singleHalfPoint else zeroPoints

  override def maxPoints: Points =
    sampleArg match {
      case None    => zeroPoints
      case Some(_) => singleHalfPoint
    }

}

object LimitMatcher extends Matcher[Limit, LimitMatch] {

  override protected def canMatch(l1: Limit, l2: Limit): Boolean = true

  override protected def instantiateOnlySampleMatch(sa: Limit): LimitMatch =
    LimitMatch(MatchType.ONLY_SAMPLE, None, Some(sa))

  override protected def instantiateOnlyUserMatch(ua: Limit): LimitMatch =
    LimitMatch(MatchType.ONLY_USER, Some(ua), None)

  override protected def instantiateCompleteMatch(ua: Limit, sa: Limit): LimitMatch = {

    val matchType = if (ua.toString == sa.toString) {
      MatchType.SUCCESSFUL_MATCH
    } else {
      MatchType.PARTIAL_MATCH
    }

    LimitMatch(matchType, Some(ua), Some(sa))
  }
}
