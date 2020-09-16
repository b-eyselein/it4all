package model.tools.flask

import model.tools.FilesSampleSolutionToolJsonProtocol
import model.{ExerciseFile, FilesSolution, JsonProtocols, SampleSolution}
import play.api.libs.json._

object FlaskToolJsonProtocol extends FilesSampleSolutionToolJsonProtocol[FlaskExerciseContent, FlaskExPart] {

  // Test Config

  val flaskTestsConfigFormat: OFormat[FlaskTestsConfig] = {
    implicit val flaskSingleTestConfigWrites: OFormat[FlaskSingleTestConfig] = Json.format

    Json.format
  }

  override val partTypeFormat: Format[FlaskExPart] = FlaskExPart.jsonFormat

  override protected val exerciseContentFormat: OFormat[FlaskExerciseContent] = {
    implicit val eff: OFormat[ExerciseFile]                  = JsonProtocols.exerciseFileFormat
    implicit val ftcf: OFormat[FlaskTestsConfig]             = flaskTestsConfigFormat
    implicit val fsf: OFormat[SampleSolution[FilesSolution]] = sampleSolutionFormat

    Json.format
  }

  // Result file content

  val flaskCorrectionResultFileReads: Reads[FlaskCorrectionResultFileContent] = {
    implicit val flaskCorrectionResultFormat: Reads[FlaskTestResult] = Json.reads

    Json.reads
  }

}
