package controllers.core.excontrollers

import controllers.core.BaseController
import model.User
import model.core.tools.RandomExToolObject
import model.core.{Repository, Secured}
import play.api.db.slick.DatabaseConfigProvider
import play.api.mvc.{ControllerComponents, EssentialAction}
import play.twirl.api.Html

import scala.concurrent.ExecutionContext

abstract class ARandomExController(cc: ControllerComponents, dbcp: DatabaseConfigProvider, r: Repository, toolObject: RandomExToolObject)
                                  (implicit ec: ExecutionContext)
  extends BaseController(cc, dbcp, r) with Secured {

  def index: EssentialAction = withUser { user => implicit request => Ok(renderIndex(user)) }

  def renderIndex(user: User): Html

}