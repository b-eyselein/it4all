package model.tools.regex

import model.SampleSolution
import model.matching.MatchType
import model.points._
import play.api.Logger

import scala.util.Try
import scala.util.matching.Regex

object RegexExtractionCorrector extends RegexAbstractCorrector {

  override protected val logger: Logger = Logger(RegexExtractionCorrector.getClass)

  def correctExtraction(
    exerciseContent: RegexExerciseContent,
    userRegex: Regex
  ): RegexAbstractResult =
    exerciseContent.sampleSolutions.headOption match {
      case None => onError("No sample solution found")
      case Some(SampleSolution(_, sample)) =>
        Try(sample.r).fold(
          exception => onError("Error while building sample regex", maybeException = Some(exception)),
          sampleRegex => {

            val extractionResults = exerciseContent.extractionTestData.map {
              extractionTestData =>
                val regexMatchMatchingResult = RegexMatchMatcher.doMatch(
                  userRegex.findAllMatchIn(extractionTestData.base).toList,
                  sampleRegex.findAllMatchIn(extractionTestData.base).toList
                )

                val correct = regexMatchMatchingResult.allMatches.forall(_.matchType == MatchType.SUCCESSFUL_MATCH)

                RegexExtractionSingleResult(extractionTestData.base, regexMatchMatchingResult, correct)
            }

            val correctResultsCount: Int = extractionResults.count(_.correct)

            val points: Points =
              (correctResultsCount.toDouble / exerciseContent.extractionTestData.size.toDouble * exerciseContent.maxPoints * 4).toInt.quarterPoints

            RegexExtractionResult(
              extractionResults,
              points,
              exerciseContent.maxPoints.points
            )
          }
        )
    }

}
