package model.tools.regex

import model.json.JsonProtocols
import model.tools.{SampleSolution, StringSampleSolutionToolJsonProtocol, Topic}
import play.api.libs.json.{Format, Json}

object RegexToolJsonProtocol
    extends StringSampleSolutionToolJsonProtocol[RegexExerciseContent, RegexExercise, RegexExPart] {

  private val regexMatchTestDataFormat: Format[RegexMatchTestData] = Json.format[RegexMatchTestData]

  private val regexExtractionTestDataFormat: Format[RegexExtractionTestData] = Json.format[RegexExtractionTestData]

  override val exerciseContentFormat: Format[RegexExerciseContent] = {
    implicit val rgtf: Format[RegexCorrectionType]      = RegexCorrectionTypes.jsonFormat
    implicit val rmtdf: Format[RegexMatchTestData]      = regexMatchTestDataFormat
    implicit val retdf: Format[RegexExtractionTestData] = regexExtractionTestDataFormat

    Json.format
  }

  override val exerciseFormat: Format[RegexExercise] = {
    implicit val tf: Format[Topic]                   = JsonProtocols.topicFormat
    implicit val ssf: Format[SampleSolution[String]] = sampleSolutionFormat
    implicit val ecf: Format[RegexExerciseContent]   = exerciseContentFormat

    Json.format
  }

  override val partTypeFormat: Format[RegexExPart] = RegexExParts.jsonFormat

}
