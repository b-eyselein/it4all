package model.tools.regex

import model.tools.{SampleSolution, StringSampleSolutionToolJsonProtocol}
import play.api.libs.json.{Format, Json}

object RegexToolJsonProtocol extends StringSampleSolutionToolJsonProtocol[RegexExerciseContent, RegexExPart] {

  private val regexMatchTestDataFormat: Format[RegexMatchTestData] = Json.format[RegexMatchTestData]

  private val regexExtractionTestDataFormat: Format[RegexExtractionTestData] = Json.format[RegexExtractionTestData]

  override val exerciseContentFormat: Format[RegexExerciseContent] = {
    implicit val rgtf: Format[RegexCorrectionType]      = RegexCorrectionTypes.jsonFormat
    implicit val rmtdf: Format[RegexMatchTestData]      = regexMatchTestDataFormat
    implicit val retdf: Format[RegexExtractionTestData] = regexExtractionTestDataFormat
    implicit val ssf: Format[SampleSolution[String]]    = sampleSolutionFormat

    Json.format
  }

  override val partTypeFormat: Format[RegexExPart] = RegexExParts.jsonFormat

}
