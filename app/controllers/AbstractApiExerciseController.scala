package controllers

import model.User
import model.tools.{CollectionToolMain, Exercise, ToolList}
import play.api.mvc.Security.AuthenticatedRequest
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

trait AbstractApiExerciseController extends AbstractApiController {
  self: AbstractController =>

  protected def getToolMain(toolType: String): Option[CollectionToolMain] =
    ToolList.getExCollToolMainOption(toolType)

  protected def onNoSuchTool(toolType: String): Result =
    NotFound(s"There is no tool with id $toolType")

  protected def onNoSuchExercise(collectionId: Int, exId: Int): Result =
    NotFound(s"There is no exercise with id $exId for collection $collectionId")

  protected def onNoSuchExercisePart(exercise: Exercise, partStr: String): Result =
    NotFound(s"There is no part $partStr for exercise ${exercise.title}")

  protected case class ToolMainRequest[B](
    toolMain: CollectionToolMain,
    user: User,
    request: AuthenticatedRequest[B, User]
  ) extends WrappedRequest[B](request)

  protected def ToolMainAction(toolId: String): ActionRefiner[AuthenticatedRequest[*, User], ToolMainRequest] =
    new ActionRefiner[AuthenticatedRequest[*, User], ToolMainRequest] {

      override protected def executionContext: ExecutionContext = self.defaultExecutionContext

      override protected def refine[A](
        request: AuthenticatedRequest[A, User]
      ): Future[Either[Result, ToolMainRequest[A]]] =
        Future.successful {
          getToolMain(toolId)
            .map(ToolMainRequest(_, request.user, request))
            .toRight(onNoSuchTool(toolId))
        }

    }

  protected def JwtAuthenticatedToolMainAction(toolId: String): ActionBuilder[ToolMainRequest, AnyContent] =
    JwtAuthenticatedAction.andThen(ToolMainAction(toolId))

}
