package model.tools.regex

import model.tools.StringSolutionToolJsonProtocol
import play.api.libs.json.{Format, Json, OFormat}

object RegexToolJsonProtocol extends StringSolutionToolJsonProtocol[RegexExerciseContent, RegexExPart] {

  override val partTypeFormat: Format[RegexExPart] = RegexExPart.jsonFormat

  private val regexMatchTestDataFormat: Format[RegexMatchTestData] = Json.format[RegexMatchTestData]

  private val regexExtractionTestDataFormat: Format[RegexExtractionTestData] = Json.format[RegexExtractionTestData]

  override val exerciseContentFormat: OFormat[RegexExerciseContent] = {
    implicit val rgtf: Format[RegexCorrectionType]      = RegexCorrectionType.jsonFormat
    implicit val rmtdf: Format[RegexMatchTestData]      = regexMatchTestDataFormat
    implicit val retdf: Format[RegexExtractionTestData] = regexExtractionTestDataFormat

    Json.format
  }

}
