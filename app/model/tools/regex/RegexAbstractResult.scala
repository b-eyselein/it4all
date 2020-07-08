package model.tools.regex

import model.points._
import model.result.{AbstractCorrectionResult, InternalErrorResult}
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

sealed trait RegexAbstractResult extends AbstractCorrectionResult[RegexAbstractResult]

final case class RegexInternalErrorResult(
  msg: String,
  maxPoints: Points = (-1).points,
  solutionSaved: Boolean = false
) extends RegexAbstractResult
    with InternalErrorResult[RegexAbstractResult] {

  override def updateSolutionSaved(solutionSaved: Boolean): RegexAbstractResult =
    this.copy(solutionSaved = solutionSaved)

}

final case class RegexMatchingResult(
  matchingResults: Seq[RegexMatchingSingleResult],
  points: Points,
  maxPoints: Points,
  solutionSaved: Boolean = false
) extends RegexAbstractResult {

  override def isCompletelyCorrect: Boolean = matchingResults.forall(_.resultType.correct)

  override def updateSolutionSaved(solutionSaved: Boolean): RegexAbstractResult =
    this.copy(solutionSaved = solutionSaved)

}

final case class RegexExtractionResult(
  extractionResults: Seq[RegexExtractionSingleResult],
  points: Points,
  maxPoints: Points,
  solutionSaved: Boolean = false
) extends RegexAbstractResult {

  override def isCompletelyCorrect: Boolean = extractionResults.forall(_.correct)

  override def updateSolutionSaved(solutionSaved: Boolean): RegexAbstractResult =
    this.copy(solutionSaved = solutionSaved)

}
