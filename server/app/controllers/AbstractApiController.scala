package controllers

import java.time.Clock

import javax.inject.Inject
import model.{JsonProtocol, User}
import pdi.jwt.JwtSession
import play.api.Configuration
import play.api.libs.json.Format
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}
import scala.util.matching.Regex

abstract class AbstractApiController(cc: ControllerComponents, configuration: Configuration)
  extends AbstractController(cc) {

  private val clock            : Clock = Clock.systemDefaultZone()
  private val bearerHeaderRegex: Regex = "Bearer (.*)".r

  protected val adminRightsRequired: Boolean

  protected def createJwtSession(username: User): JwtSession = {
    implicit val uf: Format[User] = JsonProtocol.userFormat

    JwtSession()(configuration, clock) + ("user", username)
  }

  protected def apiWithUser(f: (Request[AnyContent], User) => Future[Result]): Action[AnyContent] = Action.async { implicit request =>
    request.headers.get("Authorization") match {

      // No authorization header present
      case None => Future.successful(Unauthorized("You are not authorized to access this resource!"))

      // Authorization header present and correct
      case Some(bearerHeaderRegex(serializedJwtToken)) =>

        val jwtSession = JwtSession.deserialize(serializedJwtToken)(configuration, clock)

        jwtSession.getAs("user")(JsonProtocol.userFormat) match {
          case None          => Future.successful(Unauthorized("You are not authorized to access this resource!"))
          case Some(jwtUser) =>

            if (adminRightsRequired && !jwtUser.isAdmin) {
              Future.successful(Unauthorized(""))
            } else {
              f(request, jwtUser)
            }
        }

      // Authorization header had wrong format...
      case Some(_) => Future.successful(Unauthorized("You are not authorized to access this resource!"))
    }
  }

}
