package model.tools.sql

import model.tools.{SampleSolution, StringSampleSolutionToolJsonProtocol}
import play.api.libs.json._

object SqlToolJsonProtocols extends StringSampleSolutionToolJsonProtocol[SqlExerciseContent, SqlExPart] {

  override val exerciseContentFormat: Format[SqlExerciseContent] = {
    implicit val ssf: Format[SampleSolution[String]] = sampleSolutionFormat

    Json.format
  }

  override val partTypeFormat: Format[SqlExPart] = SqlExPart.jsonFormat

}
