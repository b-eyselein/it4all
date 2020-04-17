package model.tools.rose

import model.json.JsonProtocols
import model.tools.programming.{ProgDataType, ProgrammingToolJsonProtocol}
import model.tools.{SampleSolution, StringSampleSolutionToolJsonProtocol, Topic}
import play.api.libs.json.{Format, Json, Reads, Writes}

object RoseToolJsonProtocol
    extends StringSampleSolutionToolJsonProtocol[RoseExerciseContent, RoseExercise, RoseExPart] {

  override val solutionFormat: Format[String] = Format(Reads.StringReads, Writes.StringWrites)

  val roseInputTypeFormat: Format[RoseInputType] = {
    implicit val pdtf: Format[ProgDataType] = ProgrammingToolJsonProtocol.progDataTypeFormat

    Json.format[RoseInputType]
  }

  override val exerciseContentFormat: Format[RoseExerciseContent] = {
    implicit val ritf: Format[RoseInputType] = roseInputTypeFormat

    Json.format
  }

  override val exerciseFormat: Format[RoseExercise] = {
    implicit val tf: Format[Topic]                    = JsonProtocols.topicFormat
    implicit val rssf: Format[SampleSolution[String]] = sampleSolutionFormat
    implicit val ecf: Format[RoseExerciseContent]     = exerciseContentFormat

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
