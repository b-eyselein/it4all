package model.tools.sql

import model.SampleSolution
import model.tools.StringSampleSolutionToolJsonProtocol
import play.api.libs.json._

object SqlToolJsonProtocols extends StringSampleSolutionToolJsonProtocol[SqlExerciseContent, SqlExPart] {

  override val partTypeFormat: Format[SqlExPart] = SqlExPart.jsonFormat

  override val exerciseContentFormat: Format[SqlExerciseContent] = {
    implicit val ssf: Format[SampleSolution[String]] = sampleSolutionFormat

    Json.format
  }

}