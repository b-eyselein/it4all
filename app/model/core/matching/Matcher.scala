package model.core.matching

import model.Enums
import model.Enums.MatchType._
import model.Enums.SuccessType._
import model.core.CoreConsts._
import model.core.EvaluationResult
import play.api.libs.json.{JsObject, Json}

import scala.language.postfixOps

trait MatchingResult[T, M <: Match[T]] extends EvaluationResult {

  def allMatches: Seq[M]

  // FIXME: is it possible to use ... match { case ...} ?!?
  override def success: Enums.SuccessType =
    if ((allMatches exists (_.matchType == ONLY_USER)) || (allMatches exists (_.matchType == ONLY_SAMPLE)))
      NONE
    else if (allMatches exists (_.matchType == UNSUCCESSFUL_MATCH))
      PARTIALLY
    else
      COMPLETE

  def toJson: JsObject = Json.obj(
    successName -> allMatches.forall(_.isSuccessful),
    matchesName -> allMatches.map(_.toJson)
  )

}

trait Matcher[T, M <: Match[T], R <: MatchingResult[T, M]] {

  protected def canMatch: (T, T) => Boolean

  protected def matchInstantiation: (Option[T], Option[T]) => M

  protected def resultInstantiation: Seq[M] => R

  def doMatch(firstCollection: Seq[T], secondCollection: Seq[T]): R = {

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
    def go(firstCollection: List[T], secondCollection: List[T], matches: List[M]): R = firstCollection match {
      case Nil =>
        val missing = secondCollection map (s => matchInstantiation(None, Some(s)))
        resultInstantiation(matches ++ missing)

      case firstHead :: firstTail =>
        val (foundMatch, notMatchedInSecond) = findMatchInSecondCollection(firstHead, secondCollection)
        go(firstTail, notMatchedInSecond, matches :+ foundMatch)
    }

    go(firstCollection.toList, secondCollection.toList, matches = List.empty)
  }


}