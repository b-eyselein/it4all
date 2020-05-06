package model.tools.rose

import model.SampleSolution
import model.tools.StringSampleSolutionToolJsonProtocol
import model.tools.programming.{ProgDataType, ProgrammingToolJsonProtocol}
import play.api.libs.json._

object RoseToolJsonProtocol extends StringSampleSolutionToolJsonProtocol[RoseExerciseContent, RoseExPart] {

  override val partTypeFormat: Format[RoseExPart] = RoseExPart.jsonFormat

  override val solutionFormat: Format[String] = Format(Reads.StringReads, Writes.StringWrites)

  val roseInputTypeFormat: Format[RoseInputType] = {
    implicit val pdtf: Format[ProgDataType] = ProgrammingToolJsonProtocol.progDataTypeFormat

    Json.format[RoseInputType]
  }

  override val exerciseContentFormat: OFormat[RoseExerciseContent] = {
    implicit val ritf: Format[RoseInputType]         = roseInputTypeFormat
    implicit val ssf: Format[SampleSolution[String]] = sampleSolutionFormat

    Json.format
  }

  // Other

  val roseExecutionResultFormat: Format[RoseExecutionResult] = {
    implicit val rsf: Format[RoseStart]   = Json.format[RoseStart]
    implicit val rrf: Format[RobotResult] = Json.format[RobotResult]

    Json.format[RoseExecutionResult]
  }

}
