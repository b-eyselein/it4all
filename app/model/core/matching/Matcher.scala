package model.core.matching

import model.core.CoreConsts._
import model.core.JsonWriteable
import model.core.result.SuccessType._
import model.core.result.{EvaluationResult, SuccessType}
import play.api.libs.json.{JsObject, Json}

import scala.language.postfixOps

case class MatchingResult[T, M <: Match[T]](allMatches: Seq[M]) extends EvaluationResult with JsonWriteable {

  // FIXME: is it possible to use ... match { case ...} ?!?
  override def success: SuccessType =
    if (allMatches exists (m => m.matchType == MatchType.ONLY_USER || m.matchType == MatchType.ONLY_SAMPLE))
      NONE
    else if (allMatches exists (m => m.matchType == MatchType.UNSUCCESSFUL_MATCH || m.matchType == MatchType.PARTIAL_MATCH))
      PARTIALLY
    else
      COMPLETE

  override def toJson: JsObject = Json.obj(
    successName -> allMatches.forall(_.matchType == MatchType.SUCCESSFUL_MATCH),
    matchesName -> allMatches.map(_.toJson)
  )

}

trait Matcher[T, M <: Match[T]] {

  protected def canMatch: (T, T) => Boolean

  protected def matchInstantiation: (Option[T], Option[T]) => M

  def doMatch(firstCollection: Seq[T], secondCollection: Seq[T]): MatchingResult[T, M] = {

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
    def go(firstCollection: List[T], secondCollection: List[T], matches: List[M]): MatchingResult[T, M] = firstCollection match {
      case Nil =>
        val missing = secondCollection map (s => matchInstantiation(None, Some(s)))
        MatchingResult[T, M](matches ++ missing)

      case firstHead :: firstTail =>
        val (foundMatch, notMatchedInSecond) = findMatchInSecondCollection(firstHead, secondCollection)
        go(firstTail, notMatchedInSecond, matches :+ foundMatch)
    }

    go(firstCollection.toList, secondCollection.toList, matches = List.empty)
  }


}