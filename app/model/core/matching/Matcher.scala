package model.core.matching

import model.Enums
import model.Enums.MatchType
import model.Enums.MatchType._
import model.Enums.SuccessType._
import model.core.EvaluationResult
import play.twirl.api.Html

import scala.collection.mutable.ListBuffer

trait MatchingResult[T, M <: Match[T]] extends EvaluationResult {

  implicit class PimpedBSStrign(string: String) {

    def asCode: String = "<code>" + string + "</code>"

    def asListElem: String = "<li>" + string + "</li>"

  }

  val matchName: String

  val headings: Seq[String]

  def colWidth: Int = headings.size

  def allMatches: Seq[M]

  // FIXME: is it possible to use ... match { case ...} ?!?
  override def success: Enums.SuccessType =
    if ((allMatches exists (_.matchType == ONLY_USER)) || (allMatches exists (_.matchType == ONLY_SAMPLE)))
      NONE
    else if (allMatches exists (_.matchType == UNSUCCESSFUL_MATCH))
      PARTIALLY
    else
      COMPLETE

  private def groupWrongMatches: Map[MatchType, Seq[M]] = allMatches groupBy (_.matchType)

  def describe: Html = {
    val message: String = success match {
      case COMPLETE => s"Die Korrektur der $matchName war erfolgreich."

      case (PARTIALLY | NONE) =>
        val groupedWrongMatches = groupWrongMatches
        s"Die Korrektur der $matchName ergab folgende Fehler:<ul>" +
          (groupedWrongMatches get UNSUCCESSFUL_MATCH map describePartialMatches getOrElse "") +
          (groupedWrongMatches get ONLY_SAMPLE map describeOnlySampleMatches getOrElse "") +
          (groupedWrongMatches get ONLY_USER map describeOnlyUserMatches getOrElse "") + "</ul>"

      case ERROR => s"Es gab einen Fehler bei der Korrektur der $matchName!"
    }

    new Html(s"""<div class="alert alert-${success.color}"><span class="${success.glyphicon}"></span> $message</div>""")
  }


  protected def describePartialMatches(colMatches: Seq[M]): String = colMatches match {
    case Nil => ""
    case ms  => s"Bei folgenden $matchName war die Korrektur nicht komplett erfolgreich: ${ms map (_.descUserArg.asCode) mkString ", "}".asListElem
  }

  protected def describeOnlySampleMatches(matches: Seq[M]): String = matches match {
    case Nil => ""
    case ms  => s"Folgende $matchName fehlen: ${ms map (_.descSampleArg.asCode) mkString ", "}".asListElem
  }

  protected def describeOnlyUserMatches(matches: Seq[M]): String = matches match {
    case Nil => ""
    case ms  => s"Folgende $matchName waren falsch: ${ms map (_.descUserArg.asCode) mkString ", "}".asListElem
  }

}

class Matcher[T, M <: Match[T], R <: MatchingResult[T, M]](val headings: Seq[String], canMatch: (T, T) => Boolean,
                                                           matchInstantiation: (Option[T], Option[T]) => M,
                                                           resultInstantiation: Seq[M] => R) {

  def doMatch(firstCollection: Seq[T], secondCollection: Seq[T]): R = {
    val matches: ListBuffer[M] = ListBuffer.empty

    val firstList = ListBuffer.empty ++ firstCollection
    val secondList = ListBuffer.empty ++ secondCollection

    for (arg1 <- firstList) {
      var matched = false
      for (arg2 <- secondList if !matched) {
        matched = canMatch(arg1, arg2)
        if (matched) {
          matches += matchInstantiation(Some(arg1), Some(arg2))
          firstList -= arg1
          secondList -= arg2
        }
      }
    }

    val wrong = firstList map (t => matchInstantiation(Some(t), None))
    val missing = secondList map (t => matchInstantiation(None, Some(t)))

    resultInstantiation(matches ++ wrong ++ missing)
  }

}
