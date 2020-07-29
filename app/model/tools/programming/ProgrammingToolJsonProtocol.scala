package model.tools.programming

import model.tools._
import model.tools.uml.{UmlClassDiagram, UmlClassDiagramJsonFormat}
import model.{ExerciseFile, JsonProtocols, SampleSolution}
import play.api.libs.json._

object ProgrammingToolJsonProtocol extends ToolJsonProtocol[ProgSolution, ProgrammingExerciseContent, ProgExPart] {

  override val partTypeFormat: Format[ProgExPart] = ProgExPart.jsonFormat

  private val progTestDataFormat: Format[ProgTestData] = Json.format[ProgTestData]

  override val solutionFormat: Format[ProgSolution] = {
    implicit val eff: Format[ExerciseFile]   = JsonProtocols.exerciseFileFormat
    implicit val putdf: Format[ProgTestData] = progTestDataFormat

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
    implicit val utf: Format[UnitTestPart]       = unitTestPartFormat
    implicit val ipf: Format[ImplementationPart] = implementationPartFormat
    // implicit val pstdf: Format[ProgTestData]               = progTestDataFormat
    implicit val ucdf: Format[UmlClassDiagram]             = UmlClassDiagramJsonFormat.umlClassDiagramJsonFormat
    implicit val ssf: Format[SampleSolution[ProgSolution]] = sampleSolutionFormat

    Json.format
  }

  // Result

  // Simplified execution

  val simplifiedExecutionResultFormat: Format[SimplifiedExecutionResult] = Json.format[SimplifiedExecutionResult]

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

  private val unitTestCorrectionResultFormat: Format[UnitTestCorrectionResult] = {
    implicit val uttcf: Format[UnitTestTestConfig] = unitTestTestConfigFormat

    Json.format
  }

  val unitTestCorrectionResultsFileJsonReads: Reads[UnitTestCorrectionResultFileContent] = {
    implicit val utcrf: Format[UnitTestCorrectionResult] = unitTestCorrectionResultFormat

    Json.reads
  }

  /*
  override val completeResultWrites: Writes[ProgCompleteResult] = {
    implicit val serw: Writes[SimplifiedExecutionResult]  = simplifiedExecutionResultFormat
    implicit val uttcrf: Writes[UnitTestCorrectionResult] = unitTestCorrectionResultFormat
    implicit val nerw: Writes[NormalExecutionResult]      = Json.format[NormalExecutionResult]

    Json.writes[ProgCompleteResult]
  }
   */

  // Simplified ProgUserTestData

  def dumpCompleteTestDataToJson(testData: Seq[ProgTestData]): JsValue =
    Json.obj(
      "testData" -> JsArray(testData.map(progTestDataFormat.writes)),
      "baseData" -> None
    )

}
