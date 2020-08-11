package model.tools.programming

import model.tools._
import model.{ExerciseFile, JsonProtocols, SampleSolution}
import play.api.libs.json._

object ProgrammingToolJsonProtocol extends ToolJsonProtocol[ProgSolution, ProgrammingExerciseContent, ProgExPart] {

  override val partTypeFormat: Format[ProgExPart] = ProgExPart.jsonFormat

  private val progTestDataFormat: Format[ProgTestData] = Json.format[ProgTestData]

  override val solutionFormat: Format[ProgSolution] = {
    implicit val eff: Format[ExerciseFile] = JsonProtocols.exerciseFileFormat

    Json.format
  }

  // Exercise

  private val unitTestTestConfigFormat: Format[UnitTestTestConfig] = {
    implicit val eff: Format[ExerciseFile] = JsonProtocols.exerciseFileFormat

    Json.format
  }

  private val normalUnitTestPartFormat: Format[NormalUnitTestPart] = {
    implicit val eff: Format[ExerciseFile] = JsonProtocols.exerciseFileFormat

    implicit val uttcf: Format[UnitTestTestConfig] = unitTestTestConfigFormat

    Json.format
  }

  private val simplifiedUnitTestPartFormat: Format[SimplifiedUnitTestPart] = {
    implicit val eff: Format[ExerciseFile]  = JsonProtocols.exerciseFileFormat
    implicit val stdf: Format[ProgTestData] = progTestDataFormat

    Json.format
  }

  private val unitTestPartFormat: Format[UnitTestPart] = {
    implicit val nutpf: Format[NormalUnitTestPart]     = normalUnitTestPartFormat
    implicit val sutpf: Format[SimplifiedUnitTestPart] = simplifiedUnitTestPartFormat

    Json.format
  }

  val implementationPartFormat: Format[ImplementationPart] = {
    implicit val eff: Format[ExerciseFile] = JsonProtocols.exerciseFileFormat

    Json.format
  }

  val exerciseContentFormat: OFormat[ProgrammingExerciseContent] = {
    implicit val utf: Format[UnitTestPart]                 = unitTestPartFormat
    implicit val ipf: Format[ImplementationPart]           = implementationPartFormat
    implicit val ssf: Format[SampleSolution[ProgSolution]] = sampleSolutionFormat

    Json.format
  }

  // Result

  // Simplified execution

  def dumpCompleteTestDataToJson(testData: Seq[ProgTestData]): JsValue =
    Json.obj(
      "baseData" -> None,
      "testData" -> JsArray(
        testData.map(progTestDataFormat.writes)
      )
    )

  val simplifiedExecutionResultFileContentFormat: OFormat[SimplifiedExecutionResultFileContent] = {
    implicit val simplifiedExecutionResultFormat: OFormat[SimplifiedExecutionResult] = Json.format

    Json.format
  }

  // Normal execution

  final case class UnitTestTestData(
    folderName: String,
    filename: String,
    testFilename: String,
    testConfigs: Seq[UnitTestTestConfig]
  )

  val unitTestDataWrites: Writes[UnitTestTestData] = {
    implicit val uttcf: Format[UnitTestTestConfig] = unitTestTestConfigFormat

    Json.writes
  }

  val unitTestCorrectionResultsFileJsonReads: Reads[UnitTestCorrectionResultFileContent] = {
    implicit val unitTestCorrectionResultFormat: OFormat[UnitTestCorrectionResult] = Json.format

    Json.reads
  }

}
