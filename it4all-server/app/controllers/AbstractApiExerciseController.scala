package controllers

import model.User
import model.tools.ToolList
import model.tools.collectionTools.{CollectionToolMain, Exercise}
import play.api.Configuration
import play.api.mvc._

import scala.concurrent.Future

abstract class AbstractApiExerciseController(cc: ControllerComponents, configuration: Configuration)
  extends AbstractApiController(cc, configuration) {

  protected def getToolMain(toolType: String): Option[CollectionToolMain] =
    ToolList.getExCollToolMainOption(toolType)


  protected def onNoSuchTool(toolType: String): Result =
    NotFound(s"There is no tool with id ${toolType}")

  protected def onNoSuchExercise(collectionId: Int, exId: Int): Result =
    NotFound(s"There is no exercise with id $exId for collection $collectionId")

  protected def onNoSuchExercisePart(exercise: Exercise, partStr: String): Result =
    NotFound(s"There is no part $partStr for exercise ${exercise.title}")


  protected final def apiWithToolMain[B](toolType: String, bodyParser: BodyParser[B])
                                        (f: (Request[B], User, CollectionToolMain) => Future[Result]): Action[B] =
    apiWithUser(bodyParser) { (request, user) =>
      getToolMain(toolType) match {
        case None           => Future.successful(onNoSuchTool(toolType))
        case Some(toolMain) => f(request, user, toolMain)
      }
    }

  protected def apiWithToolMain(toolType: String)(f: (Request[AnyContent], User, CollectionToolMain) => Future[Result]): Action[AnyContent] =
    apiWithToolMain(toolType, parse.default)(f)

}
