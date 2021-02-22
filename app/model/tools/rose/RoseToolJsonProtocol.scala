package model.tools.rose

import model.tools.StringSolutionToolJsonProtocol
import play.api.libs.json._

object RoseToolJsonProtocol extends StringSolutionToolJsonProtocol[RoseExerciseContent, RoseExPart] {

  override val partTypeFormat: Format[RoseExPart] = RoseExPart.jsonFormat

  override protected val exerciseContentFormat: OFormat[RoseExerciseContent] = Json.format

  // Other

  val roseExecutionResultFormat: Format[RoseExecutionResult] = {
    implicit val rsf: Format[RoseStart]   = Json.format[RoseStart]
    implicit val rrf: Format[RobotResult] = Json.format[RobotResult]

    Json.format[RoseExecutionResult]
  }

}
