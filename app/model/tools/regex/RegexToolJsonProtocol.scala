package model.tools.regex

import model.json.JsonProtocols
import model.tools.{ReadExercisesMessage, SampleSolution, StringSampleSolutionToolJsonProtocol, Topic}
import play.api.libs.json.{Format, Json, Reads}

object RegexToolJsonProtocol extends StringSampleSolutionToolJsonProtocol[RegexExercise, RegexExPart] {

  private val regexMatchTestDataFormat: Format[RegexMatchTestData] = Json.format[RegexMatchTestData]

  private val regexExtractionTestDataFormat: Format[RegexExtractionTestData] = Json.format[RegexExtractionTestData]

  override val exerciseFormat: Format[RegexExercise] = {
    implicit val tf: Format[Topic]                 = JsonProtocols.topicFormat
    implicit val rctf: Format[RegexCorrectionType] = RegexCorrectionTypes.jsonFormat

    implicit val sssf: Format[SampleSolution[String]]   = sampleSolutionFormat
    implicit val rmtdf: Format[RegexMatchTestData]      = regexMatchTestDataFormat
    implicit val retdf: Format[RegexExtractionTestData] = regexExtractionTestDataFormat

    Json.format
  }

  override val partTypeFormat: Format[RegexExPart] = RegexExParts.jsonFormat

  override val readExercisesMessageReads: Reads[ReadExercisesMessage[RegexExercise]] = {
    implicit val ef: Format[RegexExercise] = exerciseFormat

    Json.reads
  }

}
