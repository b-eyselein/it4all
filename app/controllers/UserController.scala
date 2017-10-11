package controllers

import javax.inject.Inject

import controllers.core.BaseController
import model.{Secured, StringConsts}
import model.user.User
import play.data.{DynamicForm, FormFactory}
import play.libs.Json
import play.mvc.{Controller, Result, Results, Security}


@Security.Authenticated(classOf[Secured])
class UserController @Inject()(f: FormFactory) extends BaseController(f) {

  def index: Result = Results.ok(views.html.user.user.render("User", getUser))

  def preferences: Result = Results.ok(views.html.user.preferences.render("Präferenzen", getUser))


  def saveOptions: Result = {
    val user: User = getUser
    user.todo = User.SHOW_HIDE_AGGREGATE.valueOf(factory.form.bindFromRequest().get("posTests"))
    user.save()

    // FIXME: tell user that settings habe been saved!
    Results.ok(Json.parse(s"""{"todo": "${user.todo}"}"""))
  }
}
