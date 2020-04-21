package model.tools.regex

import model.core.matching.MatchType
import model.points._
import model.tools.{ExPart, ExParts}

import scala.util.matching.Regex
import scala.util.matching.Regex.{Match => RegexMatch}

sealed abstract class RegexExPart(val partName: String, val id: String) extends ExPart

object RegexExPart extends ExParts[RegexExPart] {

  val values: IndexedSeq[RegexExPart] = findValues

  case object RegexSingleExPart extends RegexExPart(partName = "Ausdruck erstellen", id = "regex")

}

object RegexExtractionCorrector {

  def correctExtraction(
    exerciseContent: RegexExerciseContent,
    userRegex: Regex,
    solutionSaved: Boolean
  ): RegexExtractionResult = {

    val extractionResults = exerciseContent.extractionTestData.map { extractionTestData =>
      // FIXME: build sample regex in calling function!
      val sampleRegex = exerciseContent.sampleSolutions.headOption.map(_.sample).getOrElse(???).r

      val sampleExtracted: Seq[RegexMatch] = sampleRegex.findAllMatchIn(extractionTestData.base).toList

      val userExtracted: Seq[RegexMatch] = userRegex.findAllMatchIn(extractionTestData.base).toList

      val regexMatchMatchingResult = RegexMatchMatcher.doMatch(userExtracted, sampleExtracted)

      val correct = regexMatchMatchingResult.allMatches.forall(_.matchType == MatchType.SUCCESSFUL_MATCH)

      RegexExtractionSingleResult(extractionTestData.base, regexMatchMatchingResult, correct)
    }

    val correctResultsCount: Int = extractionResults.count(_.correct)

    val points: Points =
      (correctResultsCount.toDouble / exerciseContent.extractionTestData.size.toDouble * exerciseContent.maxPoints * 4).toInt.quarterPoints

    RegexExtractionResult(
      solutionSaved,
      extractionResults,
      points,
      exerciseContent.maxPoints.points
    )
  }

}
