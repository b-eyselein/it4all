package model.tools.flask

import model.tools.{FilesSolutionToolJsonProtocol, ToolJsonProtocol}
import model.{ExerciseFile, FilesSolution, FilesSolutionInput}
import play.api.libs.json._

object FlaskToolJsonProtocol extends ToolJsonProtocol[FilesSolutionInput, FlaskExerciseContent] with FilesSolutionToolJsonProtocol {

  // Test Config

  val flaskTestsConfigFormat: OFormat[FlaskTestsConfig] = {
    implicit val flaskSingleTestConfigWrites: OFormat[FlaskSingleTestConfig] = Json.format

    Json.format
  }

  override val exerciseContentFormat: OFormat[FlaskExerciseContent] = {
    implicit val eff: Format[ExerciseFile]      = exerciseFileFormat
    implicit val ftcf: Format[FlaskTestsConfig] = flaskTestsConfigFormat
    implicit val fsf: Format[FilesSolution]     = filesSolutionFormat

    Json.format
  }

  // Result file content

  val flaskCorrectionResultReads: Reads[FlaskTestResult] = Json.reads

}
