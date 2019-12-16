package controllers

import java.time.Clock

import model.{JsonProtocol, User}
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

  private implicit val clock     : Clock            = Clock.systemDefaultZone()
  private implicit val ec        : ExecutionContext = self.defaultExecutionContext
  private implicit val userFormat: Format[User]     = JsonProtocol.userFormat

  protected implicit val configuration: Configuration

  protected val adminRightsRequired: Boolean

  protected def createJwtSession(user: User): JwtSession = {
    JwtSession() + ("user", user)
  }

  private def userFromHeader(request: RequestHeader): Option[User] = request.headers.get("Authorization").flatMap {
    case bearerHeaderRegex(serializedJwtToken) =>
      JwtSession.deserialize(serializedJwtToken).getAs[User]("user").flatMap { user =>
        if (adminRightsRequired && !user.isAdmin) {
          None
        } else {
          Some(user)
        }
      }
    case _                                     => None
  }

  object JwtAuthenticatedAction extends AuthenticatedBuilder[User](userFromHeader, self.parse.default)

}
