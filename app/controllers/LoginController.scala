package controllers

import com.github.t3hnar.bcrypt._
import javax.inject._
import model.core.CoreConsts._
import model.core.Repository
import play.api.data.Form
import play.api.data.Forms._
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents}
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}
import scala.language.postfixOps

case class UserCredentials(name: String, pw: String)

class LoginController @Inject()(cc: ControllerComponents, val dbConfigProvider: DatabaseConfigProvider, val repo: Repository)
                               (implicit ec: ExecutionContext)
  extends AbstractController(cc) with HasDatabaseConfigProvider[JdbcProfile] {

  case class StrForm(str: String)

  def singleStrForm(str: String) = Form(mapping(str -> nonEmptyText)(StrForm.apply)(StrForm.unapply))

  val userCredForm = Form(tuple(NAME_NAME -> nonEmptyText, PW_NAME -> nonEmptyText))

  def registerForm = Action { implicit request => Ok(views.html.register.render()) }

  def register: Action[AnyContent] = Action.async { implicit request =>
    userCredForm.bindFromRequest.fold(
      _ => Future(BadRequest("There has been an error in your form...")),
      credentials => repo.saveUser(credentials._1, credentials._2) map (_ => Ok(views.html.registered.render(credentials._1))) recover {
        // TODO: Fehlermeldung
        case e => BadRequest("Error while inserting into db..." + e.getMessage)
      })
  }

  /**
    * Checks username in register form with ajax request
    *
    * FIXME: entfernen?
    *
    * @return {{userexists: Boolean, username: String (from form)}}
    */
  def checkUserName: Action[AnyContent] = Action.async { implicit request =>
    singleStrForm(NAME_NAME).bindFromRequest.fold(
      _ => Future(BadRequest("")),
      usernameForm => repo.userByName(usernameForm.str) map (res => Ok(Json.obj("userexists" -> res.isDefined, "username" -> usernameForm.str)))
    )
  }


  def authenticate: Action[AnyContent] = Action.async { implicit request =>
    userCredForm.bindFromRequest.fold(_ => Future(Redirect(controllers.routes.LoginController.login())),
      credentials => repo.userByName(credentials._1) map {
        case None       => Redirect(controllers.routes.LoginController.register())
        case Some(user) =>
          if (credentials._2 isBcrypted user.pwHash) Redirect(controllers.routes.Application.index()).withSession(SESSION_ID_FIELD -> user.username)
          else Redirect(controllers.routes.LoginController.login())
      })
  }

  def fromWuecampus(userName: String, courseId: Int, courseName: String): Action[AnyContent] = Action.async {
    // FIXME: not used yet...
    implicit request =>
      if (userName.isEmpty)
        Future(Redirect(routes.LoginController.login()))
      else {
        Future(Redirect(routes.LoginController.login()))
        //        findOrCreateStudent(userName, passwort = "").map {
        //          user =>
        //
        //                  if (Course.finder.byId(courseId) == null && courseId != -1) {
        //        Create course
        //                    val course = new Course()
        //                    course.name = courseName
        //                    course.save()
        //                  }
        //
        //            Redirect(controllers.routes.Application.activityDrawingIndex()).withSession(SESSION_ID_FIELD -> user.get.username)
      }
  }

  def login = Action { implicit request => Ok(views.html.login()) }

  def logout = Action { implicit request => Redirect(routes.LoginController.login()).withNewSession }

}
