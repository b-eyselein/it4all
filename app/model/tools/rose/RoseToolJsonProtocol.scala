package model.tools.rose

import model.tools.StringSampleSolutionToolJsonProtocol
import play.api.libs.json._

object RoseToolJsonProtocol extends StringSampleSolutionToolJsonProtocol[RoseExerciseContent, RoseExPart] {

  override val partTypeFormat: Format[RoseExPart] = RoseExPart.jsonFormat

  override val solutionFormat: Format[String] = Format(Reads.StringReads, Writes.StringWrites)

  override protected val exerciseContentFormat: OFormat[RoseExerciseContent] = Json.format

  // Other

  val roseExecutionResultFormat: Format[RoseExecutionResult] = {
    implicit val rsf: Format[RoseStart]   = Json.format[RoseStart]
    implicit val rrf: Format[RobotResult] = Json.format[RobotResult]

    Json.format[RoseExecutionResult]
  }

}
