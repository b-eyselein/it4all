package model.tools.collectionTools.regex

import model.core.matching.MatchType
import model.points.{Points, _}

import scala.util.matching.Regex
import scala.util.matching.Regex.{Match => RegexMatch}

object RegexExtractionCorrector {

  def correctExtraction(
    regexExerciseContent: RegexExerciseContent,
    userRegex: Regex,
    solutionSaved: Boolean
  ): RegexExtractionResult = {

    val extractionResults = regexExerciseContent.extractionTestData.map { extractionTestData =>
      // FIXME: build sample regex in calling function!
      val sampleRegex = regexExerciseContent.sampleSolutions.headOption.map(_.sample).getOrElse(???).r

      val sampleExtracted: Seq[RegexMatch] = sampleRegex.findAllMatchIn(extractionTestData.base).toList

      val userExtracted: Seq[RegexMatch] = userRegex.findAllMatchIn(extractionTestData.base).toList

      val regexMatchMatchingResult = RegexMatchMatcher.doMatch(userExtracted, sampleExtracted)

      val correct = regexMatchMatchingResult.allMatches.forall(_.matchType == MatchType.SUCCESSFUL_MATCH)

      RegexExtractionSingleResult(extractionTestData.base, regexMatchMatchingResult, correct)
    }

    val correctResultsCount: Int = extractionResults.count(_.correct)

    val points: Points =
      (correctResultsCount.toDouble / regexExerciseContent.extractionTestData.size.toDouble * regexExerciseContent.maxPoints * 4).toInt.quarterPoints

    RegexExtractionResult(
      solutionSaved,
      extractionResults,
      points,
      regexExerciseContent.maxPoints.points
    )
  }

}
