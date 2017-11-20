package model.core.matching

import model.Enums
import model.Enums.MatchType._
import model.Enums.SuccessType._
import model.core.EvaluationResult

import scala.collection.mutable.ListBuffer

class MatchingResult[T, M <: Match[T]](val matchName: String, val headings: Seq[String], val colWidth: Int, val allMatches: Seq[M])
  extends EvaluationResult {

  // FIXME: is it possible to use ... match { case ...} ?!?
  override val success: Enums.SuccessType =
    if ((allMatches exists (_.matchType == ONLY_USER)) || (allMatches exists (_.matchType == ONLY_SAMPLE)))
      NONE
    else if (allMatches exists (_.matchType == UNSUCCESSFUL_MATCH))
      PARTIALLY
    else
      COMPLETE

}

class Matcher[T, M <: Match[T]]
(matchName: String, headings: Seq[String], canMatch: (T, T) => Boolean, matchInstantiation: (Option[T], Option[T], Int) => M) {

  val colWidth: Int = headings.size

  def doMatch(firstCollection: Seq[T], secondCollection: Seq[T]): MatchingResult[T, M] = {
    val matches: ListBuffer[M] = ListBuffer.empty

    val firstList = ListBuffer.empty ++ firstCollection
    val secondList = ListBuffer.empty ++ secondCollection

    for (arg1 <- firstList) {
      var matched = false
      for (arg2 <- secondList if !matched) {
        matched = canMatch apply(arg1, arg2)
        if (matched) {
          matches += matchInstantiation apply(Some(arg1), Some(arg2), headings.size)
          firstList -= arg1
          secondList -= arg2
        }
      }
    }

    val wrong = firstList map (t => matchInstantiation apply(Some(t), None, headings.size))
    val missing = secondList map (t => matchInstantiation apply(None, Some(t), headings.size))

    new MatchingResult[T, M](matchName, headings, colWidth, matches ++ wrong ++ missing)
  }

}

class StringMatcher(matchName: String) extends Matcher[String, Match[String]](matchName, Seq("String-Repraesentation"), _ == _, new Match[String](_, _, _))
