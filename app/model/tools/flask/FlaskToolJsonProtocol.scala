package model.tools.flask

import model.tools.FilesSampleSolutionToolJsonProtocol
import model.{ExerciseFile, FilesSolution, JsonProtocols, SampleSolution}
import play.api.libs.json.{Format, Json, OFormat}

object FlaskToolJsonProtocol extends FilesSampleSolutionToolJsonProtocol[FlaskExerciseContent, FlaskExPart] {

  override val partTypeFormat: Format[FlaskExPart] = FlaskExPart.jsonFormat

  override protected val exerciseContentFormat: OFormat[FlaskExerciseContent] = {
    implicit val eff: OFormat[ExerciseFile]                  = JsonProtocols.exerciseFileFormat
    implicit val fsf: OFormat[SampleSolution[FilesSolution]] = sampleSolutionFormat

    Json.format
  }

}
