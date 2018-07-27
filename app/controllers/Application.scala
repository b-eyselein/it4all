package controllers

import javax.inject._
import model.core.Repository
import model.toolMains.ToolList
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.mvc.{AbstractController, ControllerComponents, EssentialAction}
import play.api.routing.JavaScriptReverseRouter
import slick.jdbc.JdbcProfile

import scala.concurrent.ExecutionContext

class Application @Inject()(cc: ControllerComponents, val dbConfigProvider: DatabaseConfigProvider, toolList: ToolList, val repository: Repository)(implicit ec: ExecutionContext)
  extends AbstractController(cc) with HasDatabaseConfigProvider[JdbcProfile] with Secured {

  def index: EssentialAction = withUser { user => implicit request => Ok(views.html.index(user, toolList.toolMains.toSeq)) }

  def javascriptRoutes = Action { implicit request =>
    Ok(JavaScriptReverseRouter("jsRoutes")(

      // User and Course Administration routes
      routes.javascript.AdminController.changeRole,

      // Exercise Administration routes
      routes.javascript.ExerciseController.adminDeleteExercise,
      routes.javascript.ExerciseController.adminChangeExState,

      routes.javascript.CollectionController.adminDeleteCollection,
      routes.javascript.CollectionController.deleteExerciseInCollection,
      routes.javascript.CollectionController.adminChangeCollectionState,

      // Correction routes
      routes.javascript.RandomExerciseController.correctLive,
      routes.javascript.ExerciseController.correctLive,
      routes.javascript.CollectionController.correctLive,

      // Special routes
      routes.javascript.ExerciseController.updateWebSolution
    )).as("text/javascript")
  }

}
