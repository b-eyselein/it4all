package model.tools.sql

import model.tools.StringSolutionToolJsonProtocol
import play.api.libs.json._

object SqlToolJsonProtocols extends StringSolutionToolJsonProtocol[SqlExerciseContent, SqlExPart] {

  override val partTypeFormat: Format[SqlExPart] = SqlExPart.jsonFormat

  override val exerciseContentFormat: OFormat[SqlExerciseContent] = Json.format

}
