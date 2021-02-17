package model.tools.programming

import model.tools._
import model.{ExerciseFile, FilesSolution, JsonProtocols}
import play.api.libs.json._

object ProgrammingToolJsonProtocol extends FilesSampleSolutionToolJsonProtocol[ProgrammingExerciseContent, ProgExPart] {

  override val partTypeFormat: Format[ProgExPart] = ProgExPart.jsonFormat

  private val progTestDataFormat: Format[ProgTestData] = Json.format[ProgTestData]

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
    implicit val utf: Format[UnitTestPart]       = unitTestPartFormat
    implicit val ipf: Format[ImplementationPart] = implementationPartFormat
    implicit val ssf: Format[FilesSolution]      = solutionFormat

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

  val simplifiedExecutionResultReads: Reads[SimplifiedExecutionResult] = Json.reads

  // Normal execution

  val normalExecutionResultFileJsonReads: Reads[NormalExecutionResult] = {

    Json.reads
  }

  // Unit test correction

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

  val unitTestCorrectionResultReads: Reads[UnitTestCorrectionResult] = Json.reads

}
