package model.tools.collectionTools.regex

import model.core.matching._
import model.points._

import scala.util.Try
import scala.util.matching.Regex
import scala.util.matching.Regex.{Match => RegexMatch}

final case class RegexMatchMatch(
  matchType: MatchType,
  userArg: Option[RegexMatch],
  sampleArg: Option[RegexMatch]
) extends Match[RegexMatch]

object RegexMatchMatcher extends Matcher[RegexMatch, RegexMatchMatch] {

  override protected val matchName: String         = "RegexMatches"
  override protected val matchSingularName: String = "RegexMatch"

  override protected def canMatch(t1: RegexMatch, t2: RegexMatch): Boolean =
    t1.source == t2.source && t1.start == t2.start && t1.end == t2.end

  override protected def instantiateOnlySampleMatch(sa: RegexMatch): RegexMatchMatch =
    RegexMatchMatch(MatchType.ONLY_SAMPLE, None, Some(sa))

  override protected def instantiateOnlyUserMatch(ua: RegexMatch): RegexMatchMatch =
    RegexMatchMatch(MatchType.ONLY_USER, Some(ua), None)

  override protected def instantiateCompleteMatch(ua: RegexMatch, sa: RegexMatch): RegexMatchMatch =
    RegexMatchMatch(MatchType.SUCCESSFUL_MATCH, Some(ua), Some(sa))
}

object RegexCorrector {

  def correct(sol: String, exercise: RegexExerciseContent, solutionSaved: Boolean): Try[RegexCompleteResult] =
    Try(sol.r).map { userRegex =>
      exercise.correctionType match {
        case RegexCorrectionTypes.MATCHING   => correctMatching(exercise, userRegex, solutionSaved)
        case RegexCorrectionTypes.EXTRACTION => correctExtraction(exercise, userRegex, solutionSaved)
      }
    }

  private def correctExtraction(
    exercise: RegexExerciseContent,
    userRegex: Regex,
    solutionSaved: Boolean
  ): RegexCompleteResult = {

    val extractionResults = exercise.extractionTestData.map { extractionTestData =>
      val sampleRegex = exercise.sampleSolutions.headOption.map(_.sample).getOrElse(???).r

      val sampleExtracted: Seq[RegexMatch] = sampleRegex.findAllMatchIn(extractionTestData.base).toList

      val userExtracted: Seq[RegexMatch] = userRegex.findAllMatchIn(extractionTestData.base).toList

      val regexMatchMatchingResult = RegexMatchMatcher.doMatch(userExtracted, sampleExtracted)

      val correct = regexMatchMatchingResult.allMatches.forall(_.matchType == MatchType.SUCCESSFUL_MATCH)

      RegexExtractionEvaluationResult(extractionTestData.base, regexMatchMatchingResult, correct)
    }

    val correctResultsCount: Int = extractionResults.count(_.correct)

    val points: Points =
      (correctResultsCount.toDouble / exercise.extractionTestData.size.toDouble * exercise.maxPoints * 4).toInt.quarterPoints

    RegexCompleteResult(
      exercise.correctionType,
      Seq.empty,
      extractionResults,
      points,
      exercise.maxPoints.points,
      solutionSaved
    )
  }

  private def correctMatching(
    exercise: RegexExerciseContent,
    userRegex: Regex,
    solutionSaved: Boolean
  ): RegexCompleteResult = {

    val matchResults = exercise.matchTestData.map { matchTestData =>
      val classificationResultType: BinaryClassificationResultType = matchTestData.data match {
        case userRegex(_*) =>
          if (matchTestData.isIncluded) BinaryClassificationResultTypes.TruePositive
          else BinaryClassificationResultTypes.FalsePositive
        case _ =>
          if (matchTestData.isIncluded) BinaryClassificationResultTypes.FalseNegative
          else BinaryClassificationResultTypes.TrueNegative
      }

      RegexMatchingEvaluationResult(matchTestData.data, matchTestData.isIncluded, classificationResultType)
    }

    val correctResultsCount: Int = matchResults.count(_.resultType.correct)

    val points: Points =
      (correctResultsCount.toDouble / exercise.matchTestData.size.toDouble * exercise.maxPoints * 4).toInt.quarterPoints

    RegexCompleteResult(
      exercise.correctionType,
      matchResults,
      Seq.empty,
      points,
      exercise.maxPoints.points,
      solutionSaved
    )
  }
}
