package model.tools.collectionTools.regex

import model.core.matching.MatchingResult
import model.core.result.{CompleteResult, EvaluationResult, SuccessType}
import model.points._
import model.tools.collectionTools.regex.BinaryClassificationResultTypes._


sealed trait RegexEvalutationResult extends EvaluationResult


final case class RegexMatchingEvaluationResult(matchData: String, isIncluded: Boolean, resultType: BinaryClassificationResultType) extends RegexEvalutationResult {

  override def success: SuccessType = resultType match {
    case TruePositive | TrueNegative   => SuccessType.COMPLETE
    case FalsePositive | FalseNegative => SuccessType.NONE
  }

}


final case class RegexExtractionEvaluationResult(base: String, extractionMatchingResult: MatchingResult[RegexMatchMatch], correct: Boolean) extends RegexEvalutationResult {

  override def success: SuccessType = ???

}


final case class RegexCompleteResult(
  correctionType: RegexCorrectionType,
  matchingResults: Seq[RegexMatchingEvaluationResult],
  extractionResults: Seq[RegexExtractionEvaluationResult],
  points: Points, maxPoints: Points, solutionSaved: Boolean = false
) extends CompleteResult[RegexEvalutationResult] {

  override def results: Seq[RegexEvalutationResult] = matchingResults ++ extractionResults

}
