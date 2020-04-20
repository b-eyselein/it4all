package model.tools.sql

import model.tools.StringSampleSolutionToolJsonProtocol
import play.api.libs.json._

object SqlJsonProtocols extends StringSampleSolutionToolJsonProtocol[SqlExerciseContent, SqlExPart] {

  override val exerciseContentFormat: Format[SqlExerciseContent] = Json.format

  /*
  override val exerciseFormat: Format[SqlExercise] = {
    implicit val tf: Format[Topic]                   = JsonProtocols.topicFormat
    implicit val ssf: Format[SampleSolution[String]] = sampleSolutionFormat
    implicit val ecf: Format[SqlExerciseContent]     = exerciseContentFormat

    Json.format
  }
   */

  override val partTypeFormat: Format[SqlExPart] = SqlExParts.jsonFormat

}
