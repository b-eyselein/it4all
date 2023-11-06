package model.tools.flask

import model.tools.{FilesSolutionToolJsonProtocol, ToolJsonProtocol}
import model.{ExerciseFile, FilesSolution, FilesSolutionInput}
import play.api.libs.json._

import scala.annotation.unused

object FlaskToolJsonProtocol extends ToolJsonProtocol[FilesSolutionInput, FlaskExerciseContent] with FilesSolutionToolJsonProtocol {

  // Test Config

  val flaskTestsConfigFormat: OFormat[FlaskTestsConfig] = {
    @unused implicit val flaskSingleTestConfigWrites: OFormat[FlaskSingleTestConfig] = Json.format

    Json.format
  }

  override val exerciseContentFormat: OFormat[FlaskExerciseContent] = {
    @unused implicit val eff: Format[ExerciseFile]      = exerciseFileFormat
    @unused implicit val ftcf: Format[FlaskTestsConfig] = flaskTestsConfigFormat
    @unused implicit val fsf: Format[FilesSolution]     = filesSolutionFormat

    Json.format
  }

  // Result file content

  val flaskTestResultReads: Reads[FlaskTestResult] = Json.reads

}
