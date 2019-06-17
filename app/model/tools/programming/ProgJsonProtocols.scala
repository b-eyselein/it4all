package model.tools.programming

import model.{ExerciseFile, ExerciseState, FilesSampleSolutionJsonProtocol}
import model.core.result.CompleteResultJsonProtocol
import model.tools.programming.ProgConsts._
import play.api.libs.functional.syntax._
import play.api.libs.json._

object ProgJsonProtocols extends CompleteResultJsonProtocol[ProgEvalResult, ProgCompleteResult] {

  private implicit val exerciseFileJsonWrites: Format[ExerciseFile] = FilesSampleSolutionJsonProtocol.exerciseFileJsonFormat

  // Simplified execution

  private implicit val simplifiedExecutionResultFormat: Format[SimplifiedExecutionResult] = Json.format[SimplifiedExecutionResult]

  val simplifiedResultsFileJsonReads: Reads[SimplifiedResultFileContent] = Json.reads[SimplifiedResultFileContent]

  // Normal execution

  private implicit val unitTestTestConfigFormat: Format[UnitTestTestConfig] = Json.format[UnitTestTestConfig]


  final case class UnitTestTestData(foldername: String, filename: String, testConfigs: Seq[UnitTestTestConfig])

  val unitTestDataWrites: Writes[UnitTestTestData] = Json.writes[UnitTestTestData]


  private implicit val unitTestCorrectionResultFormat: Format[UnitTestCorrectionResult] = Json.format[UnitTestCorrectionResult]

  val unitTestCorrectionResultsFileJsonReads: Reads[UnitTestCorrectionResultFileContent] = Json.reads[UnitTestCorrectionResultFileContent]

  private implicit val normalExecutionResultFormat: Format[NormalExecutionResult] = Json.format[NormalExecutionResult]


  override val completeResultWrites: Writes[ProgCompleteResult] = Json.writes[ProgCompleteResult]

  // Sample Solutions

  private implicit val progSolutionJsonFormat: Format[ProgSolution] = Json.format[ProgSolution]

  val sampleSolutionJsonFormat: Format[ProgSampleSolution] = Json.format[ProgSampleSolution]


  // Simplified ProgUserTestData

  private implicit val progUserTestDataWrites: Writes[ProgUserTestData] = Json.writes[ProgUserTestData]

  private implicit val progSampleTestDataWrites: Writes[ProgSampleTestData] = Json.writes[ProgSampleTestData]

  private val progTestDataWrites: Writes[ProgTestData] = Json.writes[ProgTestData]

  def dumpCompleteTestDataToJson(exercise: ProgExercise, testData: Seq[ProgTestData]): JsValue = Json.obj(
    testDataName -> JsArray(testData.map(progTestDataWrites.writes)),
    baseDataName -> exercise.baseData
  )

  private implicit val userTestDataReads: Reads[ProgUserTestData] = (
    (__ \ idName).read[Int] and
      (__ \ inputsName).read[JsValue] and
      (__ \ outputName).read[JsValue]
    ) (ProgUserTestData.apply(_, _, _, ExerciseState.RESERVED))

  val progSolutionReads: Reads[ProgSolution] = Json.reads[ProgSolution]

}
