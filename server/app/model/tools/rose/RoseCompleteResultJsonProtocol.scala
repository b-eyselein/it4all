package model.tools.rose

import model.core.result.CompleteResultJsonProtocol
import model.points._
import model.tools.programming.{ProgDataType, ProgLanguage, ProgLanguages, ProgrammingJsonProtocols}
import model.{SemanticVersion, SemanticVersionHelper}
import play.api.libs.json.{Format, Json, Writes}

object RoseCompleteResultJsonProtocol extends CompleteResultJsonProtocol[RoseExecutionResult, RoseCompleteResult] {
  
  val exerciseFormat: Format[RoseExercise] = {
    implicit val svf: Format[SemanticVersion] = SemanticVersionHelper.format

    implicit val ritf: Format[RoseInputType] = {
      implicit val pdtf: Format[ProgDataType] = ProgrammingJsonProtocols.progDataTypeFormat

      Json.format[RoseInputType]
    }

    implicit val rssf: Format[RoseSampleSolution] = roseSampleSolutionJsonFormat

    Json.format[RoseExercise]
  }

  // Other

  val roseExecutionResultWrites: Format[RoseExecutionResult] = {
    implicit val rsf: Format[RoseStart] = Json.format[RoseStart]

    implicit val rrf: Format[RobotResult] = Json.format[RobotResult]

    Json.format[RoseExecutionResult]
  }

  override val completeResultWrites: Writes[RoseCompleteResult] = {
    implicit val pointsWrites: Writes[Points] = pointsJsonWrites

    implicit val rerw: Writes[RoseExecutionResult] = roseExecutionResultWrites

    Json.writes[RoseCompleteResult]
  }

  val roseSampleSolutionJsonFormat: Format[RoseSampleSolution] = {
    implicit val progLanguageJsonFormat: Format[ProgLanguage] = ProgLanguages.jsonFormat

    Json.format[RoseSampleSolution]
  }

}


