package model.tools.regex

import model.SampleSolution
import model.tools.StringSampleSolutionToolJsonProtocol
import play.api.libs.json.{Format, Json, OFormat}

object RegexToolJsonProtocol extends StringSampleSolutionToolJsonProtocol[RegexExerciseContent, RegexExPart] {

  override val partTypeFormat: Format[RegexExPart] = RegexExPart.jsonFormat

  private val regexMatchTestDataFormat: Format[RegexMatchTestData] = Json.format[RegexMatchTestData]

  private val regexExtractionTestDataFormat: Format[RegexExtractionTestData] = Json.format[RegexExtractionTestData]

  override val exerciseContentFormat: OFormat[RegexExerciseContent] = {
    implicit val rgtf: Format[RegexCorrectionType]      = RegexCorrectionType.jsonFormat
    implicit val rmtdf: Format[RegexMatchTestData]      = regexMatchTestDataFormat
    implicit val retdf: Format[RegexExtractionTestData] = regexExtractionTestDataFormat
    implicit val ssf: Format[SampleSolution[String]]    = sampleSolutionFormat

    Json.format
  }

}
