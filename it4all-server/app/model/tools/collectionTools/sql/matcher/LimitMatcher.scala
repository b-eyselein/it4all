package model.tools.collectionTools.sql.matcher

import model.core.matching.{GenericAnalysisResult, Match, MatchType, Matcher}
import model.points._
import net.sf.jsqlparser.statement.select.Limit
import play.api.libs.json.{JsString, JsValue}

final case class LimitMatch(
  userArg: Option[Limit],
  sampleArg: Option[Limit],
  analysisResult: Option[GenericAnalysisResult]
) extends Match[Limit, GenericAnalysisResult] {

  /*
  override protected def analyze(arg1: Limit, arg2: Limit): GenericAnalysisResult = GenericAnalysisResult(
    if (arg1.toString == arg2.toString) MatchType.SUCCESSFUL_MATCH
    else MatchType.PARTIAL_MATCH
  )

   */

  override protected def descArgForJson(arg: Limit): JsValue = JsString(arg.toString)

  override def points: Points = if (matchType == MatchType.SUCCESSFUL_MATCH) singleHalfPoint else zeroPoints

  override def maxPoints: Points = sampleArg match {
    case None    => zeroPoints
    case Some(_) => singleHalfPoint
  }

}

object LimitMatcher extends Matcher[Limit, GenericAnalysisResult, LimitMatch] {

  override protected val matchName: String = "Limits"

  override protected val matchSingularName: String = "des Limits"

  override protected def canMatch(l1: Limit, l2: Limit): Boolean = true

  override protected def instantiatePartMatch(ua: Option[Limit], sa: Option[Limit]): LimitMatch = LimitMatch(ua, sa, None)

  override protected def instantiateCompleteMatch(ua: Limit, sa: Limit): LimitMatch = {

    val ar = GenericAnalysisResult(
      if (ua.toString == sa.toString) {
        MatchType.SUCCESSFUL_MATCH
      } else {
        MatchType.PARTIAL_MATCH
      }
    )

    LimitMatch(Some(ua), Some(sa), Some(ar))
  }
}
