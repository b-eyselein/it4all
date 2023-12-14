package model.tools.programming

import model.tools._
import model.{ExerciseFile, FilesSolution, FilesSolutionInput}
import play.api.libs.json._

import scala.annotation.unused

object ProgrammingToolJsonProtocol
    extends ToolJsonProtocol[FilesSolutionInput, ProgrammingExerciseContent]
    with FilesSolutionToolJsonProtocol[ProgrammingExerciseContent] {

  // Exercise

  private val unitTestTestConfigFormat: Format[UnitTestTestConfig] = {
    @unused implicit val eff: Format[ExerciseFile] = exerciseFileFormat

    Json.format
  }

  private val unitTestPartFormat: Format[UnitTestPart] = {
    @unused implicit val eff: Format[ExerciseFile]         = exerciseFileFormat
    @unused implicit val uttcf: Format[UnitTestTestConfig] = unitTestTestConfigFormat

    Json.format

  }

  val implementationPartFormat: Format[ImplementationPart] = {
    @unused implicit val eff: Format[ExerciseFile] = exerciseFileFormat

    Json.format
  }

  val exerciseContentFormat: OFormat[ProgrammingExerciseContent] = {
    @unused implicit val utf: Format[UnitTestPart]       = unitTestPartFormat
    @unused implicit val ipf: Format[ImplementationPart] = implementationPartFormat
    @unused implicit val ssf: Format[FilesSolution]      = filesSolutionFormat

    Json.format
  }

  // Result

  val implementationCorrectionResultReads: Reads[ImplementationCorrectionResult] = Json.reads

  // Unit test correction

  final case class UnitTestTestData(
    folderName: String,
    filename: String,
    testFilename: String,
    testConfigs: Seq[UnitTestTestConfig]
  )

  val unitTestDataWrites: Writes[UnitTestTestData] = {
    @unused implicit val uttcf: Format[UnitTestTestConfig] = unitTestTestConfigFormat

    Json.writes
  }

  val unitTestCorrectionResultReads: Reads[UnitTestCorrectionResult] = Json.reads

}
