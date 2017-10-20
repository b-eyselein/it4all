package controllers.excontrollers

import controllers.core.BaseController
import model.tools.RandomExToolObject
import model.user.User
import play.api.mvc.ControllerComponents
import play.mvc.Security.Authenticated
import play.twirl.api.Html

@Authenticated(classOf[model.Secured])
abstract class ARandomExController(cc: ControllerComponents, toolObject: RandomExToolObject)
  extends BaseController(cc) {

  def index = Action { implicit request => Ok(renderIndex(getUser)) }

  def renderIndex(user: User): Html

}