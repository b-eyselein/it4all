package model.matching

import scala.collection.JavaConverters.asScalaBufferConverter
import scala.collection.mutable.ListBuffer

import model.exercise.Success
import model.result.EvaluationResult

class MatchingResult[T, M <: Match[T]](
  val matchName: String,
  val headings: List[String],
  val colWidth: Int,
  val allMatches: List[M])
  extends EvaluationResult(MatchingResult.analyze(allMatches))

object MatchingResult {
  def analyze(allMatches: List[Match[_]]): Success = {
    allMatches.map(_.matchType).distinct
    // FIXME: is it possible to user ... match {} ?!?
    if (allMatches.exists(_.matchType == MatchType.ONLY_USER) || allMatches.exists(_.matchType == MatchType.ONLY_SAMPLE))
      Success.NONE
    else if (allMatches.exists(_.matchType == MatchType.UNSUCCESSFUL_MATCH))
      Success.PARTIALLY
    else
      Success.COMPLETE
  }
}

class Matcher[T, M <: Match[T]](
  matchName: String,
  headings: List[String],
  canMatch: (T, T) => Boolean,
  matchInstantiation: (Option[T], Option[T], Int) => M) {

  val colWidth = headings.size

  def doMatch(firstCollection: List[T], secondCollection: List[T]) = {
    val matches: ListBuffer[M] = ListBuffer.empty

    val firstList = ListBuffer.empty ++ firstCollection
    val secondList = ListBuffer.empty ++ secondCollection

    for (arg1 <- firstList) {

      var matched = false
      for (arg2 <- secondList if !matched) {
        matched = canMatch.apply(arg1, arg2)
        if (matched) {
          matches += matchInstantiation.apply(Some(arg1), Some(arg2), headings.size)
          firstList -= arg1
          secondList -= arg2
        }
      }
    }

    val wrong = firstList.map(t => matchInstantiation.apply(Some(t), None, headings.size))
    val missing = secondList.map(t => matchInstantiation.apply(None, Some(t), headings.size))

    new MatchingResult[T, M](matchName, headings, colWidth, (matches ++ wrong ++ missing).toList)
  }

  def doJavaMatch(firstCol: java.util.List[T], secondCol: java.util.List[T]): MatchingResult[T, M] =
    doMatch(firstCol.asScala.toList, secondCol.asScala.toList)

}

class StringMatcher(matchName: String) extends Matcher[String, Match[String]](
  matchName,
  List("String-Repraesentation"),
  _ == _,
  new Match[String](_, _, _))
