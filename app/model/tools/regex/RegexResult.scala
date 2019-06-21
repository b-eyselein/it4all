package model.tools.regex

import model.core.matching.MatchingResult
import model.core.result.{CompleteResult, CompleteResultJsonProtocol, EvaluationResult, SuccessType}
import model.points._
import model.tools.regex.BinaryClassificationResultTypes._
import play.api.libs.json._


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


object RegexCompleteResultJsonProtocol extends CompleteResultJsonProtocol[RegexEvalutationResult, RegexCompleteResult] {


  private val regexMatchingEvaluationResultWrites: Writes[RegexMatchingEvaluationResult] = Json.writes[RegexMatchingEvaluationResult]


  private val regexMatchMatchWrites: Writes[RegexMatchMatch] = _.toJson

  private val regexMatchMatchingResultWrites: Writes[MatchingResult[RegexMatchMatch]] = {
    implicit val rmmw: Writes[RegexMatchMatch] = regexMatchMatchWrites

    Json.writes[MatchingResult[RegexMatchMatch]]
  }


  private val regexExtractionEvaluationResultWrites: Writes[RegexExtractionEvaluationResult] = {
    implicit val rmmrw: Writes[MatchingResult[RegexMatchMatch]] = regexMatchMatchingResultWrites

    Json.writes[RegexExtractionEvaluationResult]
  }

  override val completeResultWrites: Writes[RegexCompleteResult] = {
    implicit val regexCorrectionTypeFormat: Format[RegexCorrectionType] = RegexCorrectionTypes.jsonFormat

    implicit val pointsWrites: Writes[Points] = pointsJsonWrites

    implicit val matchEvalResultWrites: Writes[RegexMatchingEvaluationResult] = regexMatchingEvaluationResultWrites

    implicit val extractionEvalResultWrites: Writes[RegexExtractionEvaluationResult] = regexExtractionEvaluationResultWrites

    Json.writes[RegexCompleteResult]
  }

}
