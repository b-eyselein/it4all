package model.tools.flask

import model.tools.FilesSampleSolutionToolJsonProtocol
import model.{ExerciseFile, FilesSolution, JsonProtocols}
import play.api.libs.json._

object FlaskToolJsonProtocol extends FilesSampleSolutionToolJsonProtocol[FlaskExerciseContent, FlaskExPart] {

  // Test Config

  val flaskTestsConfigFormat: OFormat[FlaskTestsConfig] = {
    implicit val flaskSingleTestConfigWrites: OFormat[FlaskSingleTestConfig] = Json.format

    Json.format
  }

  override val partTypeFormat: Format[FlaskExPart] = FlaskExPart.jsonFormat

  override protected val exerciseContentFormat: OFormat[FlaskExerciseContent] = {
    implicit val eff: Format[ExerciseFile]      = JsonProtocols.exerciseFileFormat
    implicit val ftcf: Format[FlaskTestsConfig] = flaskTestsConfigFormat
    implicit val fsf: Format[FilesSolution]     = solutionFormat

    Json.format
  }

  // Result file content

  val flaskCorrectionResultFileReads: Reads[FlaskCorrectionResultFileContent] = {
    implicit val flaskCorrectionResultFormat: Reads[FlaskTestResult] = Json.reads

    Json.reads
  }

}
