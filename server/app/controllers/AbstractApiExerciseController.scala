package controllers

import model.User
import model.tools.ToolList
import model.tools.collectionTools.{CollectionToolMain, Exercise, ExerciseCollection}
import play.api.Configuration
import play.api.mvc._

import scala.concurrent.Future

abstract class AbstractApiExerciseController(cc: ControllerComponents, configuration: Configuration)
  extends AbstractApiController(cc, configuration) {

  private def getToolMain(toolType: String): Option[CollectionToolMain] =
    ToolList.getExCollToolMainOption(toolType)


  protected def onNoSuchTool(toolType: String): Result =
    NotFound(s"There is no tool with id ${toolType}")

  protected def onNoSuchCollection(toolName: String, collId: Int): Result =
    NotFound(s"There is no collection $collId for tool ${toolName}")

  protected def onNoSuchExercise(collection: ExerciseCollection, exId: Int): Result =
    NotFound(s"There is no exercise with id $exId for collection ${collection.title}")

  protected def onNoSuchExercisePart(exercise: Exercise, partStr: String): Result =
    NotFound(s"There is no part $partStr for exercise ${exercise.title}")


  protected final def apiWithToolMain(toolType: String)(f: (Request[AnyContent], User, CollectionToolMain) => Future[Result]): Action[AnyContent] =
    apiWithUser { (request, user) =>
      getToolMain(toolType) match {
        case None           => Future.successful(onNoSuchTool(toolType))
        case Some(toolMain) => f(request, user, toolMain)
      }
    }

}
