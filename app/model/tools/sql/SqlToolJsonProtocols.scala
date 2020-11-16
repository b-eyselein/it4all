package model.tools.sql

import model.tools.StringSampleSolutionToolJsonProtocol
import play.api.libs.json._

object SqlToolJsonProtocols extends StringSampleSolutionToolJsonProtocol[SqlExerciseContent, SqlExPart] {

  override val partTypeFormat: Format[SqlExPart] = SqlExPart.jsonFormat

  override val exerciseContentFormat: OFormat[SqlExerciseContent] = Json.format

}
