package model.tools.regex

import model.core.matching.MatchingResult
import model.core.result.CompleteResultJsonProtocol
import model.points.{Points, pointsJsonWrites}
import model.{SemanticVersion, SemanticVersionHelper, StringSampleSolution, StringSampleSolutionJsonProtocol}
import play.api.libs.json.{Format, Json, Writes}

object RegexCompleteResultJsonProtocol extends CompleteResultJsonProtocol[RegexEvalutationResult, RegexCompleteResult] {

  private val regexMatchTestDataFormat: Format[RegexMatchTestData] = Json.format[RegexMatchTestData]

  private val regexExtractionTestDataFormat = Json.format[RegexExtractionTestData]

  val exerciseFormat: Format[RegexExercise] = {
    implicit val svf: Format[SemanticVersion] = SemanticVersionHelper.format

    implicit val rctf: Format[RegexCorrectionType] = RegexCorrectionTypes.jsonFormat

    implicit val sssf: Format[StringSampleSolution] = StringSampleSolutionJsonProtocol.stringSampleSolutionJsonFormat

    implicit val rmtdf: Format[RegexMatchTestData] = regexMatchTestDataFormat

    implicit val retdf: Format[RegexExtractionTestData] = regexExtractionTestDataFormat

    Json.format[RegexExercise]
  }

  // Other...

  private val regexMatchingEvaluationResultWrites: Writes[RegexMatchingEvaluationResult] = {
    implicit val bcrtw: Writes[BinaryClassificationResultType] = BinaryClassificationResultTypes.jsonFormat

    Json.writes[RegexMatchingEvaluationResult]
  }


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
