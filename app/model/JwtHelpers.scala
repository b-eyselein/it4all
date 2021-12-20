package model

import model.mongo.MongoUserQueries
import pdi.jwt.{JwtClaim, JwtSession}
import play.api.Configuration
import play.api.libs.json.{JsValue, Json, OFormat}
import play.api.mvc.RequestHeader

import java.time.Clock
import scala.concurrent.Future

trait JwtHelpers {
  self: MongoUserQueries =>

  private val clock: Clock = Clock.systemDefaultZone()

  protected val configuration: Configuration

  private val loggedInUserWithTokenFormat: OFormat[LoggedInUserWithToken] = {
    implicit val liuf: OFormat[LoggedInUser] = Json.format

    Json.format
  }

  protected def createJwtSession(username: String): JwtSession = JwtSession(JwtClaim(subject = Some(username)))(configuration, clock)

  protected def writeJsonWebToken(user: LoggedInUserWithToken): JsValue = loggedInUserWithTokenFormat.writes(user)

  protected def deserializeJwt(jwtString: String): JwtSession = JwtSession.deserialize(jwtString)(configuration, clock)

  protected def userFromRequestHeader(request: RequestHeader): Future[Option[LoggedInUser]] = {
    val maybeUsername = for {
      header <- request.headers.get("Authorization")
      jwt = deserializeJwt(header)
      username <- jwt.claim.subject
    } yield username

    val loggedInUser = maybeUsername match {
      case None           => Future.successful(None)
      case Some(username) => futureUserByUsername(username)
    }

    loggedInUser.map {
      case None                       => None
      case Some(User(username, _, _)) => Some(LoggedInUser(username))
    }
  }

}
