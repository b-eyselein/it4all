package model.core.matching

import model.Enums
import model.Enums.MatchType._
import model.Enums.SuccessType._
import model.core.CoreConsts._
import model.core.EvaluationResult
import model.core.EvaluationResult.PimpedHtmlString
import play.api.libs.json.{JsObject, Json}

import scala.language.postfixOps

trait MatchingResult[T, M <: Match[T]] extends EvaluationResult {

  val matchName: String

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

  // FIXME: use scalatags for results...

  def describe: String = success match {
    case COMPLETE => s"""<span class="glyphicon glyphicon-ok"></span> Die Korrektur der $matchName war erfolgreich.""" asDivWithClass "alert alert-success"

    case (PARTIALLY | NONE) =>
      val groupedMatches = allMatches groupBy (_.matchType)

      val message =
        s"""<h4><span class="${success.glyphicon}"></span> Die Korrektur der $matchName ergab folgendes Ergebnis:</h4>""" +
          (groupedMatches get SUCCESSFUL_MATCH map describeCorrectMatches getOrElse "") +
          (groupedMatches get PARTIAL_MATCH map describePartialMatches getOrElse "") +
          (groupedMatches get UNSUCCESSFUL_MATCH map describeUnsuccessfulMatches getOrElse "") +
          (groupedMatches get ONLY_SAMPLE map describeOnlySampleMatches getOrElse "") +
          (groupedMatches get ONLY_USER map describeOnlyUserMatches getOrElse "")

      message asDiv

    case ERROR => s"""<span class="glyphicon glyphicon-ok"></span> Es gab einen Fehler bei der Korrektur der $matchName!""" asDiv
  }

  protected def describeCorrectMatches(colMatches: Seq[M]): String = colMatches match {
    case Nil => ""
    case ms  => describeMatches(s"Folgende $matchName waren korrekt:", ms, "alert alert-success")(isCorrect = true)
  }

  protected def describePartialMatches(colMatches: Seq[M]): String = colMatches match {
    case Nil => ""
    case ms  => describeMatches(s"Bei folgenden $matchName war die Korrektur nicht komplett erfolgreich:", ms, "alert alert-warning")
  }

  protected def describeUnsuccessfulMatches(colMatches: Seq[M]): String = colMatches match {
    case Nil => ""
    case ms  => describeMatches(s"Bei folgenden $matchName war die Korrektur nicht erfolgreich:", ms, "alert alert-danger")
  }

  protected def describeOnlySampleMatches(matches: Seq[M]): String = matches match {
    case Nil => ""
    case ms  => describeMatches(s"Folgende $matchName fehlen:", ms, "alert alert-danger", userArg = false)
  }

  protected def describeOnlyUserMatches(matches: Seq[M]): String = matches match {
    case Nil => ""
    case ms  => describeMatches(s"Folgende $matchName waren falsch:", ms, "alert alert-danger")
  }

  protected def describeMatches(message: String, matches: Seq[M], cssClass: String, userArg: Boolean = true)(implicit isCorrect: Boolean = false): String =
    (message + "<ul>" + (matches map (m => (if (userArg) m.descUserArgWithReason else m.descSampleArgWithReason) asListElem) mkString) + "<ul>") asDivWithClass cssClass

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