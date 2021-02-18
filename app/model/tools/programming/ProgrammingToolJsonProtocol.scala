package model.tools.programming

import model.tools._
import model.{ExerciseFile, FilesSolution, JsonProtocols}
import play.api.libs.json._

object ProgrammingToolJsonProtocol extends FilesSampleSolutionToolJsonProtocol[ProgrammingExerciseContent, ProgExPart] {

  override val partTypeFormat: Format[ProgExPart] = ProgExPart.jsonFormat

  // Exercise

  private val unitTestTestConfigFormat: Format[UnitTestTestConfig] = {
    implicit val eff: Format[ExerciseFile] = JsonProtocols.exerciseFileFormat

    Json.format
  }

  private val unitTestPartFormat: Format[UnitTestPart] = {
    implicit val eff: Format[ExerciseFile]         = JsonProtocols.exerciseFileFormat
    implicit val uttcf: Format[UnitTestTestConfig] = unitTestTestConfigFormat

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

  // Normal execution

  val normalExecutionResultFileJsonReads: Reads[ImplementationCorrectionResult] = {

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
