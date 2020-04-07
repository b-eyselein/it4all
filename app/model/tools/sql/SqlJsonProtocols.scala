package model.tools.sql

import model.tools.{SampleSolution, StringSampleSolutionToolJsonProtocol}
import play.api.libs.json._

object SqlJsonProtocols extends StringSampleSolutionToolJsonProtocol[SqlExerciseContent, SqlExPart] {

  override val exerciseContentFormat: Format[SqlExerciseContent] = {
    implicit val sssf: Format[SampleSolution[String]] = sampleSolutionFormat

    Json.format[SqlExerciseContent]
  }

  override val partTypeFormat: Format[SqlExPart] = SqlExParts.jsonFormat

}
