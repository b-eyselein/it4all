package model.core.matching

import model.core.result.{EvaluationResult, SuccessType}
import model.points._


final case class MatchingResult[T, M <: Match[T]](
  matchName: String,
  matchSingularName: String,
  allMatches: Seq[M],
  points: Points,
  maxPoints: Points
) extends EvaluationResult {

  // FIXME: is it possible to use ... match { case ...} ?!?
  override def success: SuccessType =
    if (allMatches.exists(m => m.matchType == MatchType.ONLY_USER || m.matchType == MatchType.ONLY_SAMPLE)) {
      SuccessType.NONE
    } else if (allMatches exists (m => m.matchType == MatchType.UNSUCCESSFUL_MATCH || m.matchType == MatchType.PARTIAL_MATCH)) {
      SuccessType.PARTIALLY
    } else {
      SuccessType.COMPLETE
    }

}
