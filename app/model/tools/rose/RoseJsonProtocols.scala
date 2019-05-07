package model.tools.rose

import model.core.result.CompleteResultJsonProtocol
import model.points._
import model.tools.programming.{ProgLanguage, ProgLanguages}
import play.api.libs.json.{Format, JsString, Json, Writes}

object RoseCompleteResultJsonProtocol extends CompleteResultJsonProtocol[RoseEvalResult, RoseCompleteResult] {

  private implicit val pointsWrites: Writes[Points] = pointsJsonWrites

  private implicit def roseEvalResultWrites: Writes[RoseEvalResult] = x => JsString(s"TODO: ${x.toString}!")

  override val completeResultWrites: Writes[RoseCompleteResult] = Json.writes[RoseCompleteResult]

}


object RoseSampleSolutionJsonProtocol {

  private implicit val progLanguageJsonFormat: Format[ProgLanguage] = ProgLanguages.jsonFormat

  val roseSampleSolutionJsonFormat: Format[RoseSampleSolution] = Json.format[RoseSampleSolution]

}
