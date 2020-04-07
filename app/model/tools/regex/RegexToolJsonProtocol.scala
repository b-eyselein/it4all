package model.tools.regex

import model.tools.{SampleSolution, StringSampleSolutionToolJsonProtocol}
import play.api.libs.json.{Format, Json}

object RegexToolJsonProtocol extends StringSampleSolutionToolJsonProtocol[RegexExerciseContent, RegexExPart] {

  private val regexMatchTestDataFormat: Format[RegexMatchTestData] = Json.format[RegexMatchTestData]

  private val regexExtractionTestDataFormat: Format[RegexExtractionTestData] = Json.format[RegexExtractionTestData]

  override val exerciseContentFormat: Format[RegexExerciseContent] = {
    implicit val rctf: Format[RegexCorrectionType]      = RegexCorrectionTypes.jsonFormat
    implicit val sssf: Format[SampleSolution[String]]   = sampleSolutionFormat
    implicit val rmtdf: Format[RegexMatchTestData]      = regexMatchTestDataFormat
    implicit val retdf: Format[RegexExtractionTestData] = regexExtractionTestDataFormat

    Json.format[RegexExerciseContent]
  }

  override val partTypeFormat: Format[RegexExPart] = RegexExParts.jsonFormat
}
