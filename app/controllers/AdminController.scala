package controllers

import javax.inject.Inject

import controllers.core.BaseController
import model.feedback.{Feedback, FeedbackResult}
import model.user.{Role, User}
import play.api.libs.json.Json
import play.api.mvc.ControllerComponents
import play.mvc.Security

import scala.collection.JavaConverters._

@Security.Authenticated(classOf[model.AdminSecured])
class AdminController @Inject()(cc: ControllerComponents) extends BaseController(cc) {


  def changeRole(username: String) = Action { implicit request =>
    if (getUser.stdRole != Role.SUPERADMIN)
      Forbidden("You do not have sufficient privileges to change roles!")
    else {
      val newrole = Role.USER.toString //factory.form().bindFromRequest().get("newrole")

      Option(User.finder.byId(username)) match {
        case None => BadRequest("TODO!")
        case Some(userToChange) =>
          userToChange.stdRole = Role.valueOf(newrole)
          userToChange.save()

          Ok(Json.obj("user" -> userToChange.toString, "newRole" -> newrole))
      }
    }
  }

  def evaluation = Action { implicit request =>
    val results: List[FeedbackResult] = FeedbackResult.evaluate(Feedback.finder.all.asScala.toList)
    Ok(views.html.evaluation.stats.render(getUser, results))
  }

  def index = Action { implicit request =>
    Ok(views.html.admin.adminPage.render(getUser))
  }

  def users = Action { implicit request =>
    Ok(views.html.admin.users.render(getUser, User.finder.all.asScala.toList))

  }

}