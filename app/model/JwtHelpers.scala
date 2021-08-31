package model

import model.graphql.GraphQLMutations
import pdi.jwt.{JwtClaim, JwtSession}
import play.api.Configuration
import play.api.libs.json.JsValue
import play.api.mvc.RequestHeader

import java.time.Clock
import scala.concurrent.Future

trait JwtHelpers {
  self: GraphQLMutations =>

  private implicit val clock: Clock = Clock.systemDefaultZone()

  protected implicit val configuration: Configuration

  protected def createJwtSession(username: String): JwtSession = JwtSession(JwtClaim(subject = Some(username)))

  protected def writeJsonWebToken(user: LoggedInUserWithToken): JsValue = JsonProtocols.loggedInUserWithTokenFormat.writes(user)

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
      case None                             => None
      case Some(User(username, _, isAdmin)) => Some(LoggedInUser(username, isAdmin))
    }
  }

}
