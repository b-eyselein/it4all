package model.tools.collectionTools.rose

import model.points._
import model.tools.collectionTools.programming.{ProgDataType, ProgrammingToolJsonProtocol}
import model.tools.collectionTools.{SampleSolution, ToolJsonProtocol}
import play.api.libs.json.{Format, Json, Reads, Writes}

object RoseToolJsonProtocol extends ToolJsonProtocol[RoseExerciseContent, String, RoseCompleteResult, RoseExPart] {

  override val solutionFormat: Format[String] = Format(Reads.StringReads, Writes.StringWrites)

  val roseInputTypeFormat: Format[RoseInputType] = {
    implicit val pdtf: Format[ProgDataType] = ProgrammingToolJsonProtocol.progDataTypeFormat

    Json.format[RoseInputType]
  }

  override val exerciseContentFormat: Format[RoseExerciseContent] = {
    implicit val ritf: Format[RoseInputType]          = roseInputTypeFormat
    implicit val rssf: Format[SampleSolution[String]] = Json.format[SampleSolution[String]]

    Json.format[RoseExerciseContent]
  }

  // Other

  val roseExecutionResultFormat: Format[RoseExecutionResult] = {
    implicit val rsf: Format[RoseStart]   = Json.format[RoseStart]
    implicit val rrf: Format[RobotResult] = Json.format[RobotResult]

    Json.format[RoseExecutionResult]
  }

  override val completeResultWrites: Writes[RoseCompleteResult] = {
    implicit val pointsWrites: Writes[Points]      = ToolJsonProtocol.pointsFormat
    implicit val rerw: Writes[RoseExecutionResult] = roseExecutionResultFormat

    Json.writes[RoseCompleteResult]
  }

  override val partTypeFormat      : Format[RoseExPart] = RoseExParts.jsonFormat

}
