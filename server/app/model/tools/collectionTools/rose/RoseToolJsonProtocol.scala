package model.tools.collectionTools.rose

import model.core.{LongText, LongTextJsonProtocol}
import model.points._
import model.tools.ToolJsonProtocol
import model.tools.collectionTools.programming.{ProgDataType, ProgLanguage, ProgLanguages, ProgrammingToolJsonProtocol}
import model.{SemanticVersion, SemanticVersionHelper}
import play.api.libs.json.{Format, Json, Writes}

object RoseToolJsonProtocol extends ToolJsonProtocol[RoseExercise, RoseSampleSolution, RoseCompleteResult] {

  override val sampleSolutionFormat: Format[RoseSampleSolution] = {
    implicit val progLanguageJsonFormat: Format[ProgLanguage] = ProgLanguages.jsonFormat

    Json.format[RoseSampleSolution]
  }

  val roseInputTypeFormat: Format[RoseInputType] = {
    implicit val pdtf: Format[ProgDataType] = ProgrammingToolJsonProtocol.progDataTypeFormat

    Json.format[RoseInputType]
  }

  override val exerciseFormat: Format[RoseExercise] = {
    implicit val svf : Format[SemanticVersion]    = SemanticVersionHelper.format
    implicit val ltf : Format[LongText]           = LongTextJsonProtocol.format
    implicit val rssf: Format[RoseSampleSolution] = sampleSolutionFormat

    implicit val ritf: Format[RoseInputType] = roseInputTypeFormat

    Json.format[RoseExercise]
  }

  // Other

  val roseExecutionResultFormat: Format[RoseExecutionResult] = {
    implicit val rsf: Format[RoseStart]   = Json.format[RoseStart]
    implicit val rrf: Format[RobotResult] = Json.format[RobotResult]

    Json.format[RoseExecutionResult]
  }

  override val completeResultWrites: Writes[RoseCompleteResult] = {
    implicit val pointsWrites: Writes[Points]              = pointsJsonWrites
    implicit val rerw        : Writes[RoseExecutionResult] = roseExecutionResultFormat

    Json.writes[RoseCompleteResult]
  }

}


