package model.tools.rose

import model.json.JsonProtocols
import model.tools.programming.{ProgDataType, ProgrammingToolJsonProtocol}
import model.tools.{ReadExercisesMessage, SampleSolution, StringSampleSolutionToolJsonProtocol, Topic}
import play.api.libs.json.{Format, Json, Reads, Writes}

object RoseToolJsonProtocol extends StringSampleSolutionToolJsonProtocol[RoseExercise, RoseExPart] {

  override val solutionFormat: Format[String] = Format(Reads.StringReads, Writes.StringWrites)

  val roseInputTypeFormat: Format[RoseInputType] = {
    implicit val pdtf: Format[ProgDataType] = ProgrammingToolJsonProtocol.progDataTypeFormat

    Json.format[RoseInputType]
  }

  override val exerciseFormat: Format[RoseExercise] = {
    implicit val tf: Format[Topic] = JsonProtocols.topicFormat

    implicit val ritf: Format[RoseInputType]          = roseInputTypeFormat
    implicit val rssf: Format[SampleSolution[String]] = sampleSolutionFormat

    Json.format
  }

  // Other

  val roseExecutionResultFormat: Format[RoseExecutionResult] = {
    implicit val rsf: Format[RoseStart]   = Json.format[RoseStart]
    implicit val rrf: Format[RobotResult] = Json.format[RobotResult]

    Json.format[RoseExecutionResult]
  }

  override val partTypeFormat: Format[RoseExPart] = RoseExPart.jsonFormat

  override val readExercisesMessageReads: Reads[ReadExercisesMessage[RoseExercise]] = {
    implicit val ef: Format[RoseExercise] = exerciseFormat

    Json.reads
  }

}
