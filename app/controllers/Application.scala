package controllers

import javax.inject.Inject

import controllers.core.BaseController
import play.Environment
import play.api.mvc.ControllerComponents
import play.mvc.Security

@Security.Authenticated(classOf[model.Secured])
class Application @Inject()(cc: ControllerComponents, env: Environment) extends BaseController(cc) {

  def index = Action { implicit request => Ok(views.html.index.render(getUser, env.isDev)) }

}
