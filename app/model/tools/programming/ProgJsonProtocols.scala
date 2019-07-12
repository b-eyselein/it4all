package model.tools.programming

import model.core.result.CompleteResultJsonProtocol
import model.tools.programming.ProgConsts._
import model.{ExerciseFile, ExerciseState, FilesSampleSolutionJsonProtocol}
import play.api.libs.functional.syntax._
import play.api.libs.json._

object ProgJsonProtocols extends CompleteResultJsonProtocol[ProgEvalResult, ProgCompleteResult] {

  // Simplified execution

  private val simplifiedExecutionResultFormat: Format[SimplifiedExecutionResult] = Json.format[SimplifiedExecutionResult]

  val simplifiedResultsFileJsonReads: Reads[SimplifiedResultFileContent] = {
    implicit val serf: Format[SimplifiedExecutionResult] = simplifiedExecutionResultFormat

    Json.reads[SimplifiedResultFileContent]
  }

  // Normal execution

  private val unitTestTestConfigFormat: Format[UnitTestTestConfig] = Json.format[UnitTestTestConfig]

  final case class UnitTestTestData(foldername: String, filename: String, testConfigs: Seq[UnitTestTestConfig])

  val unitTestDataWrites: Writes[UnitTestTestData] = {
    implicit val uttcf: Format[UnitTestTestConfig] = unitTestTestConfigFormat

    Json.writes[UnitTestTestData]
  }


  private val unitTestCorrectionResultFormat: Format[UnitTestCorrectionResult] = {
    implicit val uttcf: Format[UnitTestTestConfig] = unitTestTestConfigFormat

    Json.format[UnitTestCorrectionResult]
  }

  val unitTestCorrectionResultsFileJsonReads: Reads[UnitTestCorrectionResultFileContent] = {
    implicit val utcrf: Format[UnitTestCorrectionResult] = unitTestCorrectionResultFormat

    Json.reads[UnitTestCorrectionResultFileContent]
  }

  private val normalExecutionResultFormat: Format[NormalExecutionResult] = Json.format[NormalExecutionResult]


  override val completeResultWrites: Writes[ProgCompleteResult] = {
    implicit val serw: Writes[SimplifiedExecutionResult] = simplifiedExecutionResultFormat

    implicit val nerw: Writes[NormalExecutionResult] = normalExecutionResultFormat

    implicit val uttcrf: Writes[UnitTestCorrectionResult] = unitTestCorrectionResultFormat

    Json.writes[ProgCompleteResult]
  }

  // Sample Solutions

  private val progSolutionJsonFormat: Format[ProgSolution] = {
    implicit val eff: Format[ExerciseFile] = FilesSampleSolutionJsonProtocol.exerciseFileJsonFormat

    implicit val prtdf: Format[ProgUserTestData] = progUserTestDataFormat

    Json.format[ProgSolution]
  }

  val sampleSolutionJsonFormat: Format[ProgSampleSolution] = {
    implicit val psf: Format[ProgSolution] = progSolutionJsonFormat

    Json.format[ProgSampleSolution]
  }


  // Simplified ProgUserTestData

  private val progUserTestDataFormat: Format[ProgUserTestData] = Json.format[ProgUserTestData]

  private val progSampleTestDataFormat: Format[ProgSampleTestData] = Json.format[ProgSampleTestData]

  private val progTestDataWrites: Writes[ProgTestData] = {
    implicit val petdw: Writes[ProgUserTestData] = progUserTestDataFormat

    implicit val pstdw: Writes[ProgSampleTestData] = progSampleTestDataFormat

    Json.writes[ProgTestData]
  }

  def dumpCompleteTestDataToJson(exercise: ProgExercise, testData: Seq[ProgTestData]): JsValue = Json.obj(
    testDataName -> JsArray(testData.map(progTestDataWrites.writes)),
    baseDataName -> exercise.baseData
  )

  private val userTestDataReads: Reads[ProgUserTestData] = (
    (__ \ idName).read[Int] and
      (__ \ inputsName).read[JsValue] and
      (__ \ outputName).read[JsValue]
    ) (ProgUserTestData.apply(_, _, _, ExerciseState.RESERVED))

  val progSolutionReads: Reads[ProgSolution] = {
    implicit val efr: Reads[ExerciseFile] = FilesSampleSolutionJsonProtocol.exerciseFileJsonFormat

    implicit val putdr: Reads[ProgUserTestData] = userTestDataReads

    Json.reads[ProgSolution]
  }

}
