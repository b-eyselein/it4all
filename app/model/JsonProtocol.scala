package model

import play.api.libs.json.{Format, Json}

object JsonProtocol {

  val userFormat: Format[User] = Json.format

}
