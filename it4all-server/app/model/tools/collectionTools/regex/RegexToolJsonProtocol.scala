package model.tools.collectionTools.regex

import model.core.matching.{GenericAnalysisResult, MatchingResult}
import model.points.Points
import model.tools.collectionTools.{SampleSolution, StringSampleSolutionToolJsonProtocol, ToolJsonProtocol}
import play.api.libs.json.{Format, Json, Writes}

import scala.util.matching.Regex.{Match => RegexMatch}

object RegexToolJsonProtocol extends StringSampleSolutionToolJsonProtocol[RegexExerciseContent, RegexCompleteResult] {

  val regexMatchTestDataFormat: Format[RegexMatchTestData] = Json.format[RegexMatchTestData]

  val regexExtractionTestDataFormat: Format[RegexExtractionTestData] = Json.format[RegexExtractionTestData]

  override val exerciseContentFormat: Format[RegexExerciseContent] = {
    implicit val rctf : Format[RegexCorrectionType]     = RegexCorrectionTypes.jsonFormat
    implicit val sssf : Format[SampleSolution[String]]  = sampleSolutionFormat
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

  private val regexMatchMatchingResultWrites: Writes[MatchingResult[RegexMatch, GenericAnalysisResult, RegexMatchMatch]] = {
    implicit val rmmw: Writes[RegexMatchMatch] = regexMatchMatchWrites

    Json.writes[MatchingResult[RegexMatch, GenericAnalysisResult, RegexMatchMatch]]
  }


  private val regexExtractionEvaluationResultWrites: Writes[RegexExtractionEvaluationResult] = {
    implicit val rmmrw: Writes[MatchingResult[RegexMatch, GenericAnalysisResult, RegexMatchMatch]] = regexMatchMatchingResultWrites

    Json.writes[RegexExtractionEvaluationResult]
  }

  override val completeResultWrites: Writes[RegexCompleteResult] = {
    implicit val regexCorrectionTypeFormat: Format[RegexCorrectionType] = RegexCorrectionTypes.jsonFormat

    implicit val pointsWrites: Writes[Points] = ToolJsonProtocol.pointsFormat

    implicit val matchEvalResultWrites: Writes[RegexMatchingEvaluationResult] = regexMatchingEvaluationResultWrites

    implicit val extractionEvalResultWrites: Writes[RegexExtractionEvaluationResult] = regexExtractionEvaluationResultWrites

    Json.writes[RegexCompleteResult]
  }

}
