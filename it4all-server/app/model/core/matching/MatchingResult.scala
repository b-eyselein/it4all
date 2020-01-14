package model.core.matching

import model.core.CoreConsts._
import model.core.result.{EvaluationResult, SuccessType}
import model.points._
import play.api.libs.json._


final case class MatchingResult[T, AR <: AnalysisResult, M <: Match[T, AR]](
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


  /*
  val points: Points = allMatches.map(_.points).fold(zeroPoints)(_ + _)

  val maxPoints: Points = allMatches.map(_.maxPoints).fold(zeroPoints)(_ + _)
   */

  @deprecated
  def toJson: JsObject = Json.obj(
    matchNameName -> matchName,
    matchSingularNameName -> matchSingularName,
    successName -> allMatches.forall(_.matchType == MatchType.SUCCESSFUL_MATCH),
    matchesName -> allMatches.map(_.toJson),
    pointsName -> points.asDouble,
    maxPointsName -> maxPoints.asDouble
  )


}
