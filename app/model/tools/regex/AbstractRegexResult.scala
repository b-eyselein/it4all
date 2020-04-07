package model.tools.regex

import model.core.result.AbstractCorrectionResult
import model.points._
import model.tools.regex.RegexToolMain.ExtractedValuesComparison

// single matching result

final case class RegexMatchingSingleResult(
  matchData: String,
  isIncluded: Boolean,
  resultType: BinaryClassificationResultType
)

// single extraction result

final case class RegexExtractionSingleResult(
  base: String,
  extractionMatchingResult: ExtractedValuesComparison,
  correct: Boolean
)

// complete result

sealed trait AbstractRegexResult extends AbstractCorrectionResult

final case class RegexIllegalRegexResult(
  solutionSaved: Boolean,
  message: String,
  maxPoints: Points
) extends AbstractRegexResult {

  override def points: Points = zeroPoints

}

final case class RegexMatchingResult(
  solutionSaved: Boolean,
  matchingResults: Seq[RegexMatchingSingleResult],
  points: Points,
  maxPoints: Points
) extends AbstractRegexResult

final case class RegexExtractionResult(
  solutionSaved: Boolean,
  extractionResults: Seq[RegexExtractionSingleResult],
  points: Points,
  maxPoints: Points
) extends AbstractRegexResult
