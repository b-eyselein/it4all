package controllers

import javax.inject._

import controllers.core.BaseController
import model.core.{Repository, Secured}
import play.Environment
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.mvc.{ControllerComponents, EssentialAction}
import slick.jdbc.JdbcProfile

import scala.concurrent.ExecutionContext
class Application @Inject()(cc: ControllerComponents, dbcp: DatabaseConfigProvider, r: Repository, env: Environment)
                           (implicit ec: ExecutionContext)
  extends BaseController(cc, dbcp, r) with HasDatabaseConfigProvider[JdbcProfile] with Secured {

  def index: EssentialAction = withUser { user =>
    implicit request =>
      Ok(views.html.index.render(user, env.isDev))
  }

}
