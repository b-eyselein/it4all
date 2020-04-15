package model.tools.sql

import model.tools.{SampleSolution, SemanticVersion, StringSampleSolutionToolJsonProtocol, ToolJsonProtocol}
import play.api.libs.json._

object SqlJsonProtocols extends StringSampleSolutionToolJsonProtocol[SqlExercise, SqlExPart] {

  override val exerciseFormat: Format[SqlExercise] = {
    implicit val svf: Format[SemanticVersion]         = ToolJsonProtocol.semanticVersionFormat
    implicit val sssf: Format[SampleSolution[String]] = sampleSolutionFormat

    Json.format
  }

  override val partTypeFormat: Format[SqlExPart] = SqlExParts.jsonFormat

}
