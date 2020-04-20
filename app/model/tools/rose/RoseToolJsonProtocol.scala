package model.tools.rose

import model.tools.StringSampleSolutionToolJsonProtocol
import model.tools.programming.{ProgDataType, ProgrammingToolJsonProtocol}
import play.api.libs.json.{Format, Json, Reads, Writes}

object RoseToolJsonProtocol extends StringSampleSolutionToolJsonProtocol[RoseExerciseContent, RoseExPart] {

  override val solutionFormat: Format[String] = Format(Reads.StringReads, Writes.StringWrites)

  val roseInputTypeFormat: Format[RoseInputType] = {
    implicit val pdtf: Format[ProgDataType] = ProgrammingToolJsonProtocol.progDataTypeFormat

    Json.format[RoseInputType]
  }

  override val exerciseContentFormat: Format[RoseExerciseContent] = {
    implicit val ritf: Format[RoseInputType] = roseInputTypeFormat

    Json.format
  }

  override val partTypeFormat: Format[RoseExPart] = RoseExPart.jsonFormat

  // Other

  val roseExecutionResultFormat: Format[RoseExecutionResult] = {
    implicit val rsf: Format[RoseStart]   = Json.format[RoseStart]
    implicit val rrf: Format[RobotResult] = Json.format[RobotResult]

    Json.format[RoseExecutionResult]
  }

}
