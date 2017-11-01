package controllers.core

import model.User
import model.core.tools.RandomExToolObject
import model.core.{Repository, Secured}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.mvc.{AbstractController, ControllerComponents, EssentialAction}
import play.twirl.api.Html
import slick.jdbc.JdbcProfile

import scala.concurrent.ExecutionContext

abstract class ARandomExController(cc: ControllerComponents, val dbConfigProvider: DatabaseConfigProvider, val repo: Repository, toolObject: RandomExToolObject)
                                  (implicit ec: ExecutionContext)
  extends AbstractController(cc) with HasDatabaseConfigProvider[JdbcProfile] with Secured {

  def index: EssentialAction = withUser { user => implicit request => Ok(renderIndex(user)) }

  def renderIndex(user: User): Html

}