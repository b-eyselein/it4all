package model.tools.regex

import model.result.{AbstractCorrectionResult, InternalErrorResult}
import model.points._
import model.tools.regex.RegexTool.ExtractedValuesComparison

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

sealed trait RegexAbstractResult extends AbstractCorrectionResult

final case class RegexInternalErrorResult(
  msg: String,
  solutionSaved: Boolean,
  maxPoints: Points = (-1).points
) extends RegexAbstractResult
    with InternalErrorResult

final case class RegexMatchingResult(
  solutionSaved: Boolean,
  matchingResults: Seq[RegexMatchingSingleResult],
  points: Points,
  maxPoints: Points
) extends RegexAbstractResult {

  override def isCompletelyCorrect: Boolean = matchingResults.forall(_.resultType.correct)

}

final case class RegexExtractionResult(
  solutionSaved: Boolean,
  extractionResults: Seq[RegexExtractionSingleResult],
  points: Points,
  maxPoints: Points
) extends RegexAbstractResult {

  override def isCompletelyCorrect: Boolean = extractionResults.forall(_.correct)

}
