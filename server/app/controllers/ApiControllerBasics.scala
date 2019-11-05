package controllers

import java.time.Clock

import model.toolMains.{CollectionToolMain, ToolList}
import model.{Exercise, ExerciseCollection, JsonProtocol, User}
import pdi.jwt.JwtSession
import play.api.Configuration
import play.api.libs.json.Format
import play.api.mvc._

import scala.concurrent.Future
import scala.util.matching.Regex

abstract class ApiControllerBasics(cc: ControllerComponents, toolList: ToolList, configuration: Configuration) extends AbstractController(cc) {

  private val clock: Clock = Clock.systemDefaultZone()

  private val bearerHeaderRegex: Regex = "Bearer (.*)".r

  private def getToolMain(toolType: String): Option[CollectionToolMain] = toolList.getExCollToolMainOption(toolType)


  protected val adminRightsRequired: Boolean


  protected def onNoSuchTool(toolType: String): Result =
    NotFound(s"There is no tool with id ${toolType}")

  protected def onNoSuchCollection(tool: CollectionToolMain, collId: Int): Result =
    NotFound(s"There is no collection $collId for tool ${tool.toolname}")

  protected def onNoSuchExercise(tool: CollectionToolMain, collection: ExerciseCollection, exId: Int): Result =
    NotFound(s"There is no exercise with id $exId for collection ${collection.title}")

  protected def onNoSuchExercisePart(tool: CollectionToolMain, exercise: Exercise, partStr: String): Result =
    NotFound(s"There is no part $partStr for exercise ${exercise.title}")


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


  protected final def apiWithToolMain(toolType: String)(f: (Request[AnyContent], User, CollectionToolMain) => Future[Result]): Action[AnyContent] =
    apiWithUser { (request, user) =>
      getToolMain(toolType) match {
        case None           => Future.successful(onNoSuchTool(toolType))
        case Some(toolMain) => f(request, user, toolMain)
      }
    }

}
