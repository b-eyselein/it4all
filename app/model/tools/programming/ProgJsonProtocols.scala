package model.tools.programming

import model.ExerciseState
import model.core.result.CompleteResultJsonProtocol
import model.tools.programming.ProgConsts._
import play.api.libs.functional.syntax._
import play.api.libs.json.{Format, JsString, JsValue, Json, Reads, Writes, __}

object ProgCompleteResultJsonProtocol extends CompleteResultJsonProtocol[ProgEvalResult, ProgCompleteResult] {

  private val executionResultWrites: Writes[ExecutionResult] = (
    (__ \ idName).write[Int] and
      (__ \ successTypeName).write[String] and
      (__ \ correctName).write[Boolean] and
      (__ \ inputName).write[JsValue] and
      (__ \ awaitedName).write[JsValue] and
      (__ \ gottenName).write[JsValue] and
      (__ \ consoleOutputName).write[Option[String]]
    ) (er => (er.id, er.success.entryName, er.isSuccessful, er.input, er.awaited, er.result, er.consoleOutput))

  private implicit val progEvalResultWrites: Writes[ProgEvalResult] = {
    case er: ExecutionResult   => executionResultWrites.writes(er)
    case SyntaxError(errorMsg) => JsString(errorMsg)
  }

  override def completeResultWrites(solutionSaved: Boolean): Writes[ProgCompleteResult] = (
    (__ \ solutionSavedName).write[Boolean] and
      (__ \ resultsName).write[Seq[ProgEvalResult]]
    ) (pcr => (solutionSaved, pcr.results))

}

//noinspection ConvertibleToMethodValue
object ProgSolutionJsonFormat {

  //  private implicit val progLanguageJsonFormat: Format[ProgLanguage] = ProgLanguages.jsonFormat

  val sampleSolutionJsonFormat: Format[ProgSampleSolution] = Json.format[ProgSampleSolution]

  // ProgUserTestData

  private implicit val userTestDataReads: Reads[ProgUserTestData] = (
    (__ \ idName).read[Int] and
      (__ \ inputsName).read[JsValue] and
      (__ \ outputName).read[JsValue]
    ) (ProgUserTestData.apply(_, _, _, ExerciseState.RESERVED))

  val progSolutionReads: Reads[ProgSolution] = Json.reads[ProgSolution]

}
