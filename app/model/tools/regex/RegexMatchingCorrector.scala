package model.tools.regex

import model.matching._
import model.points._

import scala.util.matching.Regex
import scala.util.matching.Regex.{Match => RegexMatch}
import scala.util.{Success, Try}

final case class RegexMatchMatch(matchType: MatchType, userArg: RegexMatch, sampleArg: RegexMatch)
    extends Match[RegexMatch] {

  override protected def argDescription: RegexMatch => String = _.group(0)

}

object RegexMatchMatcher extends Matcher[RegexMatch, RegexMatchMatch] {

  override protected def canMatch(t1: RegexMatch, t2: RegexMatch): Boolean =
    t1.source == t2.source && t1.start == t2.start && t1.end == t2.end

  override protected def instantiateMatch(ua: RegexMatch, sa: RegexMatch): RegexMatchMatch =
    RegexMatchMatch(MatchType.SUCCESSFUL_MATCH, ua, sa)
}

object RegexMatchingCorrector {

  def correctMatching(
    exerciseContent: RegexExerciseContent,
    userRegex: Regex
  ): Try[RegexAbstractResult] = Success {

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
      matchResults,
      points,
      exerciseContent.maxPoints.points
    )
  }

}
