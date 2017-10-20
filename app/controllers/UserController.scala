package controllers

import javax.inject.Inject

import controllers.core.BaseController
import model.Secured
import model.user.User
import play.api.libs.json.Json
import play.api.mvc.ControllerComponents
import play.mvc.Security

@Security.Authenticated(classOf[Secured])
class UserController @Inject()(cc: ControllerComponents) extends BaseController(cc) {

  def index = Action { implicit request => Ok(views.html.user.render("User", getUser)) }

  def preferences = Action { implicit request => Ok(views.html.preferences.render("Präferenzen", getUser)) }

  def saveOptions = Action { implicit request =>
    val user: User = getUser
    //    user.todo = User.SHOW_HIDE_AGGREGATE.valueOf(factory.form.bindFromRequest().get("posTests"))
    user.save()

    // FIXME: tell user that settings habe been saved!
    Ok(Json.obj("todo" -> user.todo.toString))
  }
}
