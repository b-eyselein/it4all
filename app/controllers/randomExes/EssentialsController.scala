package controllers.randomExes

import controllers.Secured
import javax.inject.{Inject, Singleton}
import model.core.Repository
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.mvc.{AbstractController, ControllerComponents, EssentialAction}
import slick.jdbc.JdbcProfile

import scala.concurrent.ExecutionContext


@Singleton
class EssentialsController @Inject()(cc: ControllerComponents, val dbConfigProvider: DatabaseConfigProvider, val tables: Repository)
                                    (implicit ec: ExecutionContext)
  extends AbstractController(cc) with HasDatabaseConfigProvider[JdbcProfile] with Secured {

  def index: EssentialAction = withUser { user => implicit request => Ok(views.html.essentials.overview.render(user)) }

}