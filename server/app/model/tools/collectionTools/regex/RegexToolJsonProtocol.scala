package model.tools.collectionTools.regex

import model.core.matching.MatchingResult
import model.points.{Points, pointsJsonWrites}
import model.tools.ToolJsonProtocol
import model.{LongText, LongTextJsonProtocol, SemanticVersion, SemanticVersionHelper, StringSampleSolution, StringSampleSolutionJsonProtocol}
import play.api.libs.json.{Format, Json, Writes}

object RegexToolJsonProtocol extends ToolJsonProtocol[RegexExercise, StringSampleSolution, RegexCompleteResult] {

  override val sampleSolutionFormat: Format[StringSampleSolution] =
    StringSampleSolutionJsonProtocol.stringSampleSolutionJsonFormat

  val regexMatchTestDataFormat: Format[RegexMatchTestData] = Json.format[RegexMatchTestData]

  val regexExtractionTestDataFormat: Format[RegexExtractionTestData] = Json.format[RegexExtractionTestData]

  override val exerciseFormat: Format[RegexExercise] = {
    implicit val svf  : Format[SemanticVersion]         = SemanticVersionHelper.format
    implicit val ltf  : Format[LongText]                = LongTextJsonProtocol.format
    implicit val rctf : Format[RegexCorrectionType]     = RegexCorrectionTypes.jsonFormat
    implicit val sssf : Format[StringSampleSolution]    = sampleSolutionFormat
    implicit val rmtdf: Format[RegexMatchTestData]      = regexMatchTestDataFormat
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
