package model.core.matching

import model.Enums
import model.Enums.MatchType._
import model.Enums.SuccessType._
import model.core.EvaluationResult
import play.twirl.api.Html

import scala.collection.mutable.ListBuffer

trait MatchingResult[T, M <: Match[T]] extends EvaluationResult {

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

  def describe: Html = new Html("")

}

class Matcher[T, M <: Match[T], R <: MatchingResult[T, M]](val headings: Seq[String], canMatch: (T, T) => Boolean,
                                                           matchInstantiation: (Option[T], Option[T], Int) => M,
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
          matches += matchInstantiation(Some(arg1), Some(arg2), headings.size)
          firstList -= arg1
          secondList -= arg2
        }
      }
    }

    val wrong = firstList map (t => matchInstantiation apply(Some(t), None, headings.size))
    val missing = secondList map (t => matchInstantiation apply(None, Some(t), headings.size))

    resultInstantiation(matches ++ wrong ++ missing)
  }

}

case class StringMatchingResult(allMatches: Seq[Match[String]]) extends MatchingResult[String, Match[String]] {

  override val matchName = "TODO!"

  override val headings: Seq[String] = Seq("String-Repräsentation")

  override def describe: Html = super.describe

}

class StringMatcher(matchName: String) extends Matcher[String, Match[String], StringMatchingResult](
  Seq("String-Repraesentation"), _ == _, new Match[String](_, _, _), StringMatchingResult)
