package model.tools.collectionTools.regex

import model.StringSampleSolution
import model.core.matching.MatchingResult
import model.points.{Points, pointsJsonWrites}
import model.tools.collectionTools.StringSampleSolutionToolJsonProtocol
import play.api.libs.json.{Format, Json, Writes}

object RegexToolJsonProtocol extends StringSampleSolutionToolJsonProtocol[RegexExPart, RegexExerciseContent, RegexCompleteResult](RegexExParts.jsonFormat) {

  val regexMatchTestDataFormat: Format[RegexMatchTestData] = Json.format[RegexMatchTestData]

  val regexExtractionTestDataFormat: Format[RegexExtractionTestData] = Json.format[RegexExtractionTestData]

  override val exerciseContentFormat: Format[RegexExerciseContent] = {
    implicit val rctf : Format[RegexCorrectionType]     = RegexCorrectionTypes.jsonFormat
    implicit val sssf : Format[StringSampleSolution]    = sampleSolutionFormat
    implicit val rmtdf: Format[RegexMatchTestData]      = regexMatchTestDataFormat
    implicit val retdf: Format[RegexExtractionTestData] = regexExtractionTestDataFormat

    Json.format[RegexExerciseContent]
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
