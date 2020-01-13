package model.core.matching

import model.core.CoreConsts._
import model.core.result.SuccessType.{COMPLETE, NONE, PARTIALLY}
import model.core.result.{EvaluationResult, SuccessType}
import model.points._
import play.api.libs.json._

//object MatchingResultJsonProtocol {
//
//  private def unapplyMatchingResult: MatchingResult[Match] => (String, String, SuccessType, Seq[Match], Points, Points) =
//    mr => (mr.matchName, mr.matchSingularName, mr.success, mr.allMatches, mr.points, mr.maxPoints)
//
//  private implicit def matchWrites: Writes[Match] = ???
//
//  val matchingResultJsonWrites: Writes[MatchingResult[Match]] = (
//    (__ \ matchNameName).write[String] and
//      (__ \ matchSingularNameName).write[String] and
//      (__ \ successName).write[SuccessType] and
//      (__ \ matchesName).write[Seq[Match]] and
//      (__ \ pointsName).write[Points] and
//      (__ \ maxPointsName).write[Points]
//    ) (unapplyMatchingResult)
//
//}

final case class MatchingResult[T, AR <: AnalysisResult, M <: Match[T, AR]](
  matchName: String,
  matchSingularName: String,
  allMatches: Seq[M]
) extends EvaluationResult {

  // FIXME: is it possible to use ... match { case ...} ?!?
  override def success: SuccessType =
    if (allMatches exists (m => m.matchType == MatchType.ONLY_USER || m.matchType == MatchType.ONLY_SAMPLE))
      NONE
    else if (allMatches exists (m => m.matchType == MatchType.UNSUCCESSFUL_MATCH || m.matchType == MatchType.PARTIAL_MATCH))
      PARTIALLY
    else
      COMPLETE

  val points: Points = allMatches.map(_.points).fold(zeroPoints)(_ + _)

  val maxPoints: Points = allMatches.map(_.maxPoints).fold(zeroPoints)(_ + _)

  def toJson: JsObject = Json.obj(
    matchNameName -> matchName,
    matchSingularNameName -> matchSingularName,
    successName -> allMatches.forall(_.matchType == MatchType.SUCCESSFUL_MATCH),
    matchesName -> allMatches.map(_.toJson),
    pointsName -> points.asDouble,
    maxPointsName -> maxPoints.asDouble
  )

}
