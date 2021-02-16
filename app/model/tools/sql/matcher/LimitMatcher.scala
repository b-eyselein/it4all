package model.tools.sql.matcher

import model.matching.{Match, MatchType, Matcher}
import model.points._
import net.sf.jsqlparser.statement.select.Limit

final case class LimitMatch(matchType: MatchType, userArg: Limit, sampleArg: Limit) extends Match[Limit] {

  override def points: Points = if (matchType == MatchType.SUCCESSFUL_MATCH) singleHalfPoint else zeroPoints

  override val maxPoints: Points = singleHalfPoint

}

object LimitMatcher extends Matcher[Limit, LimitMatch] {

  override protected def canMatch(l1: Limit, l2: Limit): Boolean = true

  override protected def instantiateMatch(ua: Limit, sa: Limit): LimitMatch = {

    val matchType = if (ua.toString == sa.toString) { MatchType.SUCCESSFUL_MATCH }
    else { MatchType.PARTIAL_MATCH }

    LimitMatch(matchType, ua, sa)
  }
}
