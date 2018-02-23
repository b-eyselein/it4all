package controllers

import javax.inject._
import model.core.Repository
import play.Environment
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.mvc.{AbstractController, ControllerComponents, EssentialAction}
import play.api.routing.JavaScriptReverseRouter
import slick.jdbc.JdbcProfile

import scala.concurrent.ExecutionContext

class Application @Inject()(cc: ControllerComponents, val dbConfigProvider: DatabaseConfigProvider, val tables: Repository, env: Environment)(implicit ec: ExecutionContext)
  extends AbstractController(cc) with HasDatabaseConfigProvider[JdbcProfile] with Secured {

  def index: EssentialAction = withUser { user => implicit request => Ok(views.html.index.render(user, env.isDev)) }

  def javascriptRoutes = Action { implicit request =>
    Ok(JavaScriptReverseRouter("jsRoutes")(
      exes.idPartExes.routes.javascript.AIdPartExController.correctLive,
      exCollections.routes.javascript.AExCollectionController.correctLive
    )).as("text/javascript")
  }

}
