package controllers

import model.{Exercise, ExerciseCollection, User}
import model.toolMains.{AToolMain, ToolList}
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

  // FIXME: Redirect and flash!
  private def onNoSuchTool(toolType: String): Result = NotFound(s"Es gibt kein Tool mit dem Kürzel '$toolType'")

  protected def onNoSuchCollection(tool: ToolMainType, collId: Int): Result =
    NotFound(s"Es gibt keine Sammlung mit der ID '$collId' für das Tool '${tool.toolname}'")

  protected def onNoSuchExercise(tool: ToolMainType, collection: ExerciseCollection, id: Int): Result =
    NotFound(s"Es gibt keine Aufgabe mit der ID '$id' für die Sammlung '${collection.title}' im Tool '${tool.toolname}")

  protected def onNoSuchExercisePart(tool: ToolMainType, collection: ExerciseCollection, exercise: Exercise, partStr: String): Result =
    NotFound(s"Es gibt keine Aufgabenteil '$partStr' für die Aufgabe '${exercise.title}' in der Sammlung Sammlung '${collection.title}' im Tool '${tool.toolname}")


  protected def withUserWithToolMain(toolType: String)(f: (User, ToolMainType) => Request[AnyContent] => Result): EssentialAction = withUser { user =>
    implicit request =>
      getToolMain(toolType) match {
        case None           => onNoSuchTool(toolType)
        case Some(toolMain) => f(user, toolMain)(request)
      }
  }

  protected def futureWithUserWithToolMain(toolType: String)(f: (User, ToolMainType) => Request[AnyContent] => Future[Result]): EssentialAction = futureWithUser { user =>
    implicit request =>
      getToolMain(toolType) match {
        case None           => Future(onNoSuchTool(toolType))
        case Some(toolMain) => f(user, toolMain)(request)
      }
  }

}
