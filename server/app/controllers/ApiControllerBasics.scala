package controllers

import java.time.Clock

import model.toolMains.CollectionToolMain
import model.{JsonProtocol, User}
import pdi.jwt.JwtSession
import play.api.Configuration
import play.api.libs.json.Format
import play.api.mvc.{Action, AnyContent, Request, Result}

import scala.concurrent.Future
import scala.util.matching.Regex

trait ApiControllerBasics {
  self: AExerciseController =>

  override protected def getToolMain(toolType: String): Option[CollectionToolMain] = toolList.getExCollToolMainOption(toolType)


  private val clock: Clock = Clock.systemDefaultZone()

  protected val bearerHeaderRegex: Regex = "Bearer (.*)".r

  protected val adminRightsRequired: Boolean

  val configuration: Configuration


  protected def createJwtSession(username: User): JwtSession = {
    implicit val uf: Format[User] = JsonProtocol.userFormat

    JwtSession()(configuration, clock) + ("user", username)
  }


  protected final def apiWithToolMain(toolType: String)(f: (Request[AnyContent], User, CollectionToolMain) => Future[Result]): Action[AnyContent] = Action.async { implicit request =>
    request.headers.get("Authorization") match {

      // No authorization header present
      case None => Future.successful(Unauthorized("You are not authorized to access this resource!"))

      // Authorization header present and correct
      case Some(bearerHeaderRegex(serializedJwtToken)) =>

        val jwtSession = JwtSession.deserialize(serializedJwtToken)(configuration, clock)

        jwtSession.getAs("user")(JsonProtocol.userFormat) match {
          case None          => Future.successful(Unauthorized("You are not authorized to access this resource!"))
          case Some(jwtUser) =>

            getToolMain(toolType) match {
              case None           => Future.successful(onNoSuchTool(toolType))
              case Some(toolMain) => f(request, jwtUser, toolMain)
            }
        }

      // Authorization header had wrong format...
      case Some(_) => Future.successful(Unauthorized("You are not authorized to access this resource!"))
    }
  }


}
