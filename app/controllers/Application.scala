package controllers

import javax.inject._

import model.core.{Repository, Secured}
import play.Environment
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.mvc.{AbstractController, ControllerComponents, EssentialAction}
import slick.jdbc.JdbcProfile

import scala.concurrent.ExecutionContext

class Application @Inject()(cc: ControllerComponents, val dbConfigProvider: DatabaseConfigProvider, val repo: Repository, env: Environment)(implicit ec: ExecutionContext)
  extends AbstractController(cc) with HasDatabaseConfigProvider[JdbcProfile] with Secured {

  def index: EssentialAction = withUser { user => implicit request => Ok(views.html.index.render(user, env.isDev)) }

}
