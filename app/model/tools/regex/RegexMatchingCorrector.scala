package model.tools.regex

import model.core.matching._
import model.points._

import scala.util.matching.Regex
import scala.util.matching.Regex.{Match => RegexMatch}

final case class RegexMatchMatch(
  matchType: MatchType,
  userArg: Option[RegexMatch],
  sampleArg: Option[RegexMatch]
) extends Match[RegexMatch] {

  override protected def argDescription: RegexMatch => String = _.group(0)

}

object RegexMatchMatcher extends Matcher[RegexMatch, RegexMatchMatch] {

  override protected def canMatch(t1: RegexMatch, t2: RegexMatch): Boolean =
    t1.source == t2.source && t1.start == t2.start && t1.end == t2.end

  override protected def instantiateOnlySampleMatch(sa: RegexMatch): RegexMatchMatch =
    RegexMatchMatch(MatchType.ONLY_SAMPLE, None, Some(sa))

  override protected def instantiateOnlyUserMatch(ua: RegexMatch): RegexMatchMatch =
    RegexMatchMatch(MatchType.ONLY_USER, Some(ua), None)

  override protected def instantiateCompleteMatch(ua: RegexMatch, sa: RegexMatch): RegexMatchMatch =
    RegexMatchMatch(MatchType.SUCCESSFUL_MATCH, Some(ua), Some(sa))
}

object RegexMatchingCorrector {

  def correctMatching(
    exerciseContent: RegexExerciseContent,
    userRegex: Regex,
    solutionSaved: Boolean
  ): RegexMatchingResult = {

    val matchResults = exerciseContent.matchTestData.map { matchTestData =>
      val classificationResultType: BinaryClassificationResultType = matchTestData.data match {
        case userRegex(_*) =>
          if (matchTestData.isIncluded) BinaryClassificationResultTypes.TruePositive
          else BinaryClassificationResultTypes.FalsePositive
        case _ =>
          if (matchTestData.isIncluded) BinaryClassificationResultTypes.FalseNegative
          else BinaryClassificationResultTypes.TrueNegative
      }

      RegexMatchingSingleResult(matchTestData.data, matchTestData.isIncluded, classificationResultType)
    }

    val correctResultsCount: Int = matchResults.count(_.resultType.correct)

    val points: Points =
      (correctResultsCount.toDouble / exerciseContent.matchTestData.size.toDouble * exerciseContent.maxPoints * 4).toInt.quarterPoints

    RegexMatchingResult(
      solutionSaved,
      matchResults,
      points,
      exerciseContent.maxPoints.points
    )
  }
}
