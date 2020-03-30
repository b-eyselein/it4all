package model.tools.collectionTools.regex

import model.core.result.AbstractCorrectionResult
import model.points._
import model.tools.collectionTools.regex.RegexToolMain.ExtractedValuesComparison

// single matching result

final case class RegexMatchingEvaluationResult(
  matchData: String,
  isIncluded: Boolean,
  resultType: BinaryClassificationResultType
)

// single extraction result

final case class RegexExtractionEvaluationResult(
  base: String,
  extractionMatchingResult: ExtractedValuesComparison,
  correct: Boolean
)

// complete result

sealed trait AbstractRegexResult extends AbstractCorrectionResult

final case class IllegalRegexResult(
  solutionSaved: Boolean,
  maxPoints: Points
) extends AbstractRegexResult {

  override def points: Points = zeroPoints

}

final case class RegexCompleteResult(
  correctionType: RegexCorrectionType,
  matchingResults: Seq[RegexMatchingEvaluationResult],
  extractionResults: Seq[RegexExtractionEvaluationResult],
  points: Points,
  maxPoints: Points,
  solutionSaved: Boolean
) extends AbstractRegexResult
