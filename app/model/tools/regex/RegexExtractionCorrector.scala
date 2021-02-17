package model.tools.regex

import model.matching.MatchType
import model.points._

import scala.util.matching.Regex
import scala.util.{Failure, Try}

object RegexExtractionCorrector {

  def correctExtraction(
    exerciseContent: RegexExerciseContent,
    userRegex: Regex
  ): Try[RegexAbstractResult] = exerciseContent.sampleSolutions.headOption match {
    case None => Failure(new Exception("No sample solution found"))
    case Some(sample) =>
      Try(sample.r)
        .map { sampleRegex =>
          val extractionResults = exerciseContent.extractionTestData.map { extractionTestData =>
            val regexMatchMatchingResult = RegexMatchMatcher.doMatch(
              userRegex.findAllMatchIn(extractionTestData.base).toList,
              sampleRegex.findAllMatchIn(extractionTestData.base).toList
            )

            val correct = regexMatchMatchingResult.allMatches.forall(_.matchType == MatchType.SUCCESSFUL_MATCH)

            RegexExtractionSingleResult(extractionTestData.base, regexMatchMatchingResult, correct)
          }

          val correctResultsCount: Int = extractionResults.count(_.correct)

          val correctPercentage = correctResultsCount.toDouble / exerciseContent.extractionTestData.size.toDouble

          val points: Points = (correctPercentage * exerciseContent.maxPoints * 4).toInt.quarterPoints

          RegexExtractionResult(extractionResults, points, exerciseContent.maxPoints.points)
        }
  }

}
