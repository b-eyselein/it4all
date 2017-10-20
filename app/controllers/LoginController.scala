package controllers


import javax.inject.Inject

import controllers.core.BaseController
import model.StringConsts._
import model.user.{Course, User}
import play.Environment
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc.ControllerComponents

class LoginController @Inject()(cc: ControllerComponents, env: Environment) extends BaseController(cc) {

  case class Creds(name: String, pw: String)

  val userForm = Form(
    mapping(
      NAME_NAME -> text,
      PW_NAME -> text
    )(Creds.apply)(Creds.unapply)
  )

  def authenticate = Action { implicit request =>
    if (env.isProd)
    // Disable login in prod mode
      Redirect(routes.LoginController.login())
    else {
      val creds = userForm.bindFromRequest().get

      val user = findOrCreateStudent(creds.name, creds.pw)

      Redirect(controllers.routes.Application.index()).withNewSession.withSession(SESSION_ID_FIELD -> user.name)
    }
  }

  def fromWuecampus(userName: String, courseId: Int, courseName: String) = Action { implicit request =>
    if (userName.isEmpty)
      Redirect(routes.LoginController.login())
    else {
      val user = findOrCreateStudent(userName, passwort = "")

      if (Course.finder.byId(courseId) == null && courseId != -1) {
        // Create course
        val course = new Course()
        course.name = courseName
        course.save()
      }

      Redirect(controllers.routes.Application.index()).withSession(SESSION_ID_FIELD -> user.name)
    }
  }

  def login = Action { implicit request =>
    Ok(views.html.login.render())
  }

  def logout = Action { implicit request =>
    Redirect(routes.LoginController.login()).withNewSession
  }

  def findOrCreateStudent(userName: String, passwort: String): User = Option(User.finder.byId(userName)) match {
    case Some(user) => user
    case None =>
      val user = new User()
      user.name = userName
      user.save()

      // FIXME: get toolObject??
      //    Path solutionDirectory = getSolDirForUser(userName)
      //    if(solutionDirectory.toFile().exists())
      //      return user
      //
      //    try {
      //      Files.createDirectories(solutionDirectory)
      //    } catch (IOException e) {
      //      Logger.error("Could not create solution directory for user " + userName, e)
      //    }

      user
  }

}
