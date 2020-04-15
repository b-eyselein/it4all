package model.tools.rose

import model.tools.programming.{ProgDataType, ProgrammingToolJsonProtocol}
import model.tools.{SampleSolution, SemanticVersion, StringSampleSolutionToolJsonProtocol, ToolJsonProtocol}
import play.api.libs.json.{Format, Json, Reads, Writes}

object RoseToolJsonProtocol extends StringSampleSolutionToolJsonProtocol[RoseExercise, RoseExPart] {

  override val solutionFormat: Format[String] = Format(Reads.StringReads, Writes.StringWrites)

  val roseInputTypeFormat: Format[RoseInputType] = {
    implicit val pdtf: Format[ProgDataType] = ProgrammingToolJsonProtocol.progDataTypeFormat

    Json.format[RoseInputType]
  }

  override val exerciseFormat: Format[RoseExercise] = {
    implicit val svf: Format[SemanticVersion]         = ToolJsonProtocol.semanticVersionFormat
    implicit val ritf: Format[RoseInputType]          = roseInputTypeFormat
    implicit val rssf: Format[SampleSolution[String]] = Json.format[SampleSolution[String]]

    Json.format
  }

  // Other

  val roseExecutionResultFormat: Format[RoseExecutionResult] = {
    implicit val rsf: Format[RoseStart]   = Json.format[RoseStart]
    implicit val rrf: Format[RobotResult] = Json.format[RobotResult]

    Json.format[RoseExecutionResult]
  }

  /*
  override val completeResultWrites: Writes[RoseCompleteResult] = {
    implicit val pointsWrites: Writes[Points]      = ToolJsonProtocol.pointsFormat
    implicit val rerw: Writes[RoseExecutionResult] = roseExecutionResultFormat

    Json.writes[RoseCompleteResult]
  }
   */

  override val partTypeFormat: Format[RoseExPart] = RoseExPart.jsonFormat

}
