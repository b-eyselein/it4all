package controllers

import java.time.Clock

import model.User
import model.json.JsonProtocols
import pdi.jwt.JwtSession
import play.api.Configuration
import play.api.libs.json.Format
import play.api.mvc.Security.AuthenticatedBuilder
import play.api.mvc._

import scala.concurrent.ExecutionContext
import scala.util.matching.Regex

trait AbstractApiController {
  self: AbstractController =>

  private val bearerHeaderRegex: Regex = "Bearer (.*)".r
  private val clock: Clock             = Clock.systemDefaultZone()

  private implicit val userFormat: Format[User] = JsonProtocols.userFormat
  private implicit val ec: ExecutionContext     = self.defaultExecutionContext

  protected val configuration: Configuration
  protected val adminRightsRequired: Boolean

  protected def createJwtSession(user: User): JwtSession = {
    JwtSession()(configuration, clock) + ("user", user)
  }

  def userFromHeader(request: RequestHeader): Option[User] = request.headers.get("Authorization").flatMap {
    case bearerHeaderRegex(serializedJwtToken) =>
      JwtSession.deserialize(serializedJwtToken)(configuration, clock).getAs[User]("user").flatMap { user =>
        if (adminRightsRequired && !user.isAdmin) {
          None
        } else {
          Some(user)
        }
      }
    case _ => None
  }

  object JwtAuthenticatedAction extends AuthenticatedBuilder[User](userFromHeader, self.parse.default)

}
