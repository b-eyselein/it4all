package model.tools.regex

import model.tools.StringSolutionToolJsonProtocol
import play.api.libs.json.{Format, Json, OFormat}

import scala.annotation.unused

object RegexToolJsonProtocol extends StringSolutionToolJsonProtocol[RegexExerciseContent] {

  private val regexMatchTestDataFormat: Format[RegexMatchTestData] = Json.format[RegexMatchTestData]

  private val regexExtractionTestDataFormat: Format[RegexExtractionTestData] = Json.format[RegexExtractionTestData]

  override val exerciseContentFormat: OFormat[RegexExerciseContent] = {
    @unused implicit val rgtf: Format[RegexCorrectionType]      = RegexCorrectionType.jsonFormat
    @unused implicit val rmtdf: Format[RegexMatchTestData]      = regexMatchTestDataFormat
    @unused implicit val retdf: Format[RegexExtractionTestData] = regexExtractionTestDataFormat

    Json.format
  }

}
