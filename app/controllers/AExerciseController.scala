package controllers

import model.{Exercise, ExerciseCollection, User}
import model.toolMains.{AToolMain, CollectionToolMain, ToolList}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.mvc._
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}


abstract class AExerciseController(cc: ControllerComponents, val dbConfigProvider: DatabaseConfigProvider,
                                   protected val toolList: ToolList)(implicit ec: ExecutionContext)
  extends AbstractController(cc) with HasDatabaseConfigProvider[JdbcProfile] with Secured {

  protected type ToolMainType <: AToolMain

  protected def getToolMain(toolType: String): Option[ToolMainType]

  // Helper methods

  protected def onNoSuchTool(toolType: String): Result =
    NotFound(s"There is no tool with id ${toolType}")

  protected def onNoSuchTool(user: User, toolType: String): Result =
    NotFound(views.html.errorViews.noSuchToolView(user, toolType))

  protected def onNoSuchCollection(tool: CollectionToolMain, collId: Int): Result =
    NotFound(s"There is no collection $collId for tool ${tool.toolname}")

  protected def onNoSuchCollection(user: User, tool: CollectionToolMain, collId: Int): Result =
    NotFound(views.html.errorViews.noSuchCollectionView(user, tool, collId))

  protected def onNoSuchExercise(tool: CollectionToolMain, collection: ExerciseCollection, exId: Int): Result =
    NotFound(s"There is no exercise with id $exId for collection ${collection.title}")

  protected def onNoSuchExercise(user: User, tool: CollectionToolMain, collection: ExerciseCollection, id: Int): Result =
    NotFound(views.html.errorViews.noSuchExerciseView(user, tool, collection, id))

  protected def onNoSuchExercisePart(tool: CollectionToolMain, exercise: Exercise, partStr: String): Result =
    NotFound(s"There is no part $partStr for exercise ${exercise.title}")

  protected def onNoSuchExercisePart(user: User, tool: CollectionToolMain, collection: ExerciseCollection, exercise: Exercise, partStr: String): Result =
    NotFound(views.html.errorViews.noSuchExercisePartView(user, tool, collection, exercise, partStr))


  protected def withUserWithToolMain(toolType: String)(f: (User, ToolMainType) => Request[AnyContent] => Result): EssentialAction = withUser { user =>
    implicit request =>
      getToolMain(toolType) match {
        case None           => onNoSuchTool(user, toolType)
        case Some(toolMain) => f(user, toolMain)(request)
      }
  }

  protected def futureWithUserWithToolMain(toolType: String)(f: (User, ToolMainType) => Request[AnyContent] => Future[Result]): EssentialAction = futureWithUser { user =>
    implicit request =>
      getToolMain(toolType) match {
        case None           => Future(onNoSuchTool(user, toolType))
        case Some(toolMain) => f(user, toolMain)(request)
      }
  }

}
