package model.tools.programming

import model.{ExerciseFile, ExerciseState, FilesSampleSolutionJsonProtocol}
import model.core.result.CompleteResultJsonProtocol
import model.tools.programming.ProgConsts._
import play.api.libs.functional.syntax._
import play.api.libs.json._

object ProgCompleteResultJsonProtocol extends CompleteResultJsonProtocol[ProgEvalResult, ProgCompleteResult] {

  private implicit val executionResultWrites: Writes[ExecutionResult] = Json.writes[ExecutionResult]

  private implicit val unitTestTestConfigWrites: Writes[UnitTestTestConfig] = Json.writes[UnitTestTestConfig]

  private implicit val unitTestCorrectionResultWrites: Writes[UnitTestCorrectionResult] = Json.writes[UnitTestCorrectionResult]


  override val completeResultWrites: Writes[ProgCompleteResult] = Json.writes[ProgCompleteResult]

}

object ProgSolutionJsonFormat {

  private implicit val exerciseFileJsonWrites: Format[ExerciseFile] = FilesSampleSolutionJsonProtocol.exerciseFileJsonFormat

  val sampleSolutionJsonFormat: Format[ProgSampleSolution] = Json.format[ProgSampleSolution]

  // ProgUserTestData

  private implicit val userTestDataReads: Reads[ProgUserTestData] = (
    (__ \ idName).read[Int] and
      (__ \ inputsName).read[JsValue] and
      (__ \ outputName).read[JsValue]
    ) (ProgUserTestData.apply(_, _, _, ExerciseState.RESERVED))

  val progSolutionReads: Reads[ProgSolution] = Json.reads[ProgSolution]

}
