package controllers

import javax.inject.Inject
import model.core.Repository
import model.toolMains.ToolList
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.mvc.{AbstractController, ControllerComponents, EssentialAction}
import slick.jdbc.JdbcProfile

import scala.concurrent.ExecutionContext

class Application @Inject()(cc: ControllerComponents, val dbConfigProvider: DatabaseConfigProvider, toolList: ToolList, val repository: Repository)(implicit ec: ExecutionContext)
  extends AbstractController(cc) with HasDatabaseConfigProvider[JdbcProfile] with Secured with play.api.i18n.I18nSupport {

  override protected val adminRightsRequired: Boolean = false

  def index: EssentialAction = withUser { user =>
    implicit request => Ok(views.html.index(user, toolList))
  }

}
