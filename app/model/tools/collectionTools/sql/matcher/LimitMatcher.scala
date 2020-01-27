package model.tools.collectionTools.sql.matcher

import model.core.matching.{GenericAnalysisResult, Match, MatchType, Matcher}
import model.points._
import net.sf.jsqlparser.statement.select.Limit

final case class LimitMatch(
  userArg: Option[Limit],
  sampleArg: Option[Limit],
  analysisResult: GenericAnalysisResult
) extends Match[Limit, GenericAnalysisResult] {

  override val maybeAnalysisResult: Option[GenericAnalysisResult] = Some(analysisResult)

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

  override protected def instantiateOnlySampleMatch(sa: Limit): LimitMatch =
    LimitMatch(None, Some(sa), GenericAnalysisResult(MatchType.ONLY_SAMPLE))

  override protected def instantiateOnlyUserMatch(ua: Limit): LimitMatch =
    LimitMatch(Some(ua), None, GenericAnalysisResult(MatchType.ONLY_USER))

  override protected def instantiateCompleteMatch(ua: Limit, sa: Limit): LimitMatch = {

    val ar = GenericAnalysisResult(
      if (ua.toString == sa.toString) {
        MatchType.SUCCESSFUL_MATCH
      } else {
        MatchType.PARTIAL_MATCH
      }
    )

    LimitMatch(Some(ua), Some(sa), ar)
  }
}
