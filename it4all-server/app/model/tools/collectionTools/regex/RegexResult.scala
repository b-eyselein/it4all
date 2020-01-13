package model.tools.collectionTools.regex

import model.core.matching.{GenericAnalysisResult, MatchingResult}
import model.core.result.{CompleteResult, EvaluationResult, SuccessType}
import model.points._
import model.tools.collectionTools.regex.BinaryClassificationResultTypes._

import scala.util.matching.Regex.{Match => RegexMatch}


sealed trait RegexEvaluationResult extends EvaluationResult


final case class RegexMatchingEvaluationResult(matchData: String, isIncluded: Boolean, resultType: BinaryClassificationResultType) extends RegexEvaluationResult {

  override def success: SuccessType = resultType match {
    case TruePositive | TrueNegative   => SuccessType.COMPLETE
    case FalsePositive | FalseNegative => SuccessType.NONE
  }

}


final case class RegexExtractionEvaluationResult(
  base: String,
  extractionMatchingResult: MatchingResult[RegexMatch, GenericAnalysisResult, RegexMatchMatch],
  correct: Boolean
) extends RegexEvaluationResult {

  override def success: SuccessType = ???

}


final case class RegexCompleteResult(
  correctionType: RegexCorrectionType,
  matchingResults: Seq[RegexMatchingEvaluationResult],
  extractionResults: Seq[RegexExtractionEvaluationResult],
  points: Points,
  maxPoints: Points,
  solutionSaved: Boolean
) extends CompleteResult[RegexEvaluationResult] {

  override def results: Seq[RegexEvaluationResult] = matchingResults ++ extractionResults

}
