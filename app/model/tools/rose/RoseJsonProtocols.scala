package model.tools.rose

import model.core.result.CompleteResultJsonProtocol
import model.tools.programming.{ProgLanguage, ProgLanguages}
import play.api.libs.json.{Format, Json, Writes}

object RoseCompleteResultJsonProtocol extends CompleteResultJsonProtocol[RoseEvalResult, RoseCompleteResult] {

  override def completeResultWrites(solutionSaved: Boolean): Writes[RoseCompleteResult] = ???

}


object RoseSampleSolutionJsonProtocol {

  private implicit val progLanguageJsonFormat: Format[ProgLanguage] = ProgLanguages.jsonFormat

  val roseSampleSolutionJsonFormat: Format[RoseSampleSolution] = Json.format[RoseSampleSolution]

}
