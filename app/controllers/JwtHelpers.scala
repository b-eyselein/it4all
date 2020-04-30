package controllers

import java.time.Clock

import model.json.JsonProtocols
import model.{LoggedInUser, LoggedInUserWithToken}
import pdi.jwt.JwtSession
import play.api.Configuration
import play.api.libs.json.{JsValue, OFormat}

trait JwtHelpers {

  private val userFieldName = "user"
  private val clock         = Clock.systemDefaultZone()

  private implicit val userFormat: OFormat[LoggedInUser] = JsonProtocols.loggedInUserFormat

  protected val configuration: Configuration

  protected def createJwtSession(user: LoggedInUser): JwtSession = {
    JwtSession()(configuration, clock) + (userFieldName, user)
  }

  protected def writeJsonWebToken(user: LoggedInUserWithToken): JsValue =
    JsonProtocols.loggedInUserWithTokenFormat.writes(user)

  protected def deserializeJwt(jwtString: String): Option[LoggedInUser] =
    JwtSession
      .deserialize(jwtString)(configuration, clock)
      .getAs[LoggedInUser](userFieldName)

}
