package model.core.matching

import model._
import model.core.CoreConsts._
import model.core.JsonWriteable
import model.core.result.SuccessType._
import model.core.result.{EvaluationResult, SuccessType}
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

trait Matcher[T, AR <: AnalysisResult, M <: Match] {

  protected val matchName: String

  protected val matchSingularName: String

  protected def canMatch(t1: T, t2: T): Boolean

  protected def matchInstantiation(ua: Option[T], sa: Option[T]): M

  def doMatch(firstCollection: Seq[T], secondCollection: Seq[T]): MatchingResult[M] = {

    def findMatchInSecondCollection(firstHead: T, secondCollection: List[T]): (M, List[T]) = {

      @annotation.tailrec
      def go(firstHead: T, secondCollection: List[T], notMatched: List[T]): (M, List[T]) = secondCollection match {
        case Nil                      => (matchInstantiation(Some(firstHead), None), notMatched)
        case secondHead :: secondTail =>
          if (canMatch(firstHead, secondHead)) {
            (matchInstantiation(Some(firstHead), Some(secondHead)), notMatched ++ secondTail)
          } else {
            go(firstHead, secondTail, notMatched :+ secondHead)
          }
      }

      go(firstHead, secondCollection, List.empty)
    }

    @annotation.tailrec
    def go(firstCollection: List[T], secondCollection: List[T], matches: List[M]): MatchingResult[M] = firstCollection match {
      case Nil =>
        val missing = secondCollection map (s => matchInstantiation(None, Some(s)))
        MatchingResult[M](matchName, matchSingularName, matches ++ missing)

      case firstHead :: firstTail =>
        val (foundMatch, notMatchedInSecond) = findMatchInSecondCollection(firstHead, secondCollection)
        go(firstTail, notMatchedInSecond, matches :+ foundMatch)
    }

    go(firstCollection.toList, secondCollection.toList, matches = List.empty)
  }


}