package model.core.matching

import model._
import model.core.CoreConsts._
import model.core.JsonWriteable
import model.core.result.{EvaluationResult, SuccessType}
import model.core.result.SuccessType.{COMPLETE, NONE, PARTIALLY}
import play.api.libs.json.{JsObject, Json}

import scala.language.postfixOps

final case class MatchingResult[M <: Match](matchName: String, matchSingularName: String, allMatches: Seq[M])
  extends EvaluationResult with JsonWriteable {

  // FIXME: is it possible to use ... match { case ...} ?!?
  override def success: SuccessType =
    if (allMatches exists (m => m.matchType == MatchType.ONLY_USER || m.matchType == MatchType.ONLY_SAMPLE))
      NONE
    else if (allMatches exists (m => m.matchType == MatchType.UNSUCCESSFUL_MATCH || m.matchType == MatchType.PARTIAL_MATCH))
      PARTIALLY
    else
      COMPLETE

  val points: Points = allMatches.map(_.points).fold(0 points)(_ + _)

  val maxPoints: Points = allMatches.map(_.maxPoints).fold(0 points)(_ + _)

  override def toJson: JsObject = Json.obj(
    matchNameName -> matchName,
    matchSingularNameName -> matchSingularName,
    successName -> allMatches.forall(_.matchType == MatchType.SUCCESSFUL_MATCH),
    matchesName -> allMatches.map(_.toJson),
    pointsName -> points.asDouble,
    maxPointsName -> maxPoints.asDouble
  )

}

