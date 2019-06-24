package model.tools.rose

import model.core.result.CompleteResultJsonProtocol
import model.points._
import model.tools.programming.{ProgLanguage, ProgLanguages}
import play.api.libs.json.{Format, JsString, Json, Writes}

object RoseCompleteResultJsonProtocol extends CompleteResultJsonProtocol[RoseExecutionResult, RoseCompleteResult] {

  private val roseStartFormat: Format[RoseStart] = Json.format[RoseStart]

  private val robotResultFormat: Format[RobotResult] = Json.format[RobotResult]


  val roseExecutionResultWrites: Format[RoseExecutionResult] = {
    implicit val rsf: Format[RoseStart] = roseStartFormat

    implicit val rrf: Format[RobotResult] = robotResultFormat

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


