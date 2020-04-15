package model.tools.regex

import model.tools.{SampleSolution, SemanticVersion, StringSampleSolutionToolJsonProtocol, ToolJsonProtocol}
import play.api.libs.json.{Format, Json}

object RegexToolJsonProtocol extends StringSampleSolutionToolJsonProtocol[RegexExercise, RegexExPart] {

  private val regexMatchTestDataFormat: Format[RegexMatchTestData] = Json.format[RegexMatchTestData]

  private val regexExtractionTestDataFormat: Format[RegexExtractionTestData] = Json.format[RegexExtractionTestData]

  override val exerciseFormat: Format[RegexExercise] = {
    implicit val svf: Format[SemanticVersion]           = ToolJsonProtocol.semanticVersionFormat
    implicit val rctf: Format[RegexCorrectionType]      = RegexCorrectionTypes.jsonFormat
    implicit val sssf: Format[SampleSolution[String]]   = sampleSolutionFormat
    implicit val rmtdf: Format[RegexMatchTestData]      = regexMatchTestDataFormat
    implicit val retdf: Format[RegexExtractionTestData] = regexExtractionTestDataFormat

    Json.format
  }

  override val partTypeFormat: Format[RegexExPart] = RegexExParts.jsonFormat

}
