package model.hubapi

import model.core.CoreConsts._
import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import play.api.libs.json._

object HubJsonProtocol {

  private val hubToolReads: Reads[HubTool] = (
    (__ \ idName).read[String] and
      (__ \ nameName).read[String] and
      (__ \ selfName).read[String]
    ) (HubTool.apply _)

  private val hubToolWrites: Writes[HubTool] = (
    (__ \ idName).write[String] and
      (__ \ nameName).write[String] and
      (__ \ selfName).write[String]
    ) (unlift(HubTool.unapply))

  val hubToolFormat = Format(hubToolReads, hubToolWrites)

}
