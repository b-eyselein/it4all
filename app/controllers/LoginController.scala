package controllers

import javax.inject._

import com.github.t3hnar.bcrypt._
import model.User
import model.core.Repository
import model.core.StringConsts._
import play.api.data.Form
import play.api.data.Forms._
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents}
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

case class UserCredentials(name: String, pw: String)

class LoginController @Inject()(cc: ControllerComponents, val dbConfigProvider: DatabaseConfigProvider, val repo: Repository)
                               (implicit ec: ExecutionContext)
  extends AbstractController(cc) with HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

  case class StrForm(str: String)

  def singleStrForm(str: String) = Form(mapping(str -> nonEmptyText)(StrForm.apply)(StrForm.unapply))

  val userCredForm: Form[UserCredentials] = Form(
    mapping(
      NAME_NAME -> nonEmptyText,
      PW_NAME -> nonEmptyText
    )(UserCredentials.apply)(UserCredentials.unapply))

  def registerForm = Action { implicit request => Ok(views.html.register.render()) }

  def register: Action[AnyContent] = Action.async { implicit request =>
    userCredForm.bindFromRequest.fold(
      // TODO: Fehlermeldung
      _ => Future(BadRequest("There has been an error in your form...")),
      credentials => {
        val newUser = User(credentials.name, credentials.pw.bcrypt)
        db.run(repo.users += newUser).map(_ => Ok(views.html.registered.render(newUser))).recover {
          // TODO: Fehlermeldung
          case e => BadRequest("Error while inserting into db..." + e.getMessage)
        }
      }
    )
  }

  /**
    * Checks username in register form with ajax request
    *
    * @return {{userexists: Boolean, username: String (from form)}}
    */
  def checkUserName: Action[AnyContent] = Action.async { implicit request =>
    singleStrForm(NAME_NAME).bindFromRequest.fold(
      _ => Future(BadRequest("")),
      usernameForm => repo.userByName(usernameForm.str).map(res => Ok(Json.obj("userexists" -> res.isDefined, "username" -> usernameForm.str)))
    )
  }


  def authenticate: Action[AnyContent] = Action.async { implicit request =>
    userCredForm.bindFromRequest.fold(_ => Future(Redirect(controllers.routes.LoginController.login())),
      credentials => repo.userByName(credentials.name).map {
        case None       => Redirect(controllers.routes.LoginController.register())
        case Some(user) =>
          if (credentials.pw.isBcrypted(user.pwHash))
            Redirect(controllers.routes.Application.index()).withSession(SESSION_ID_FIELD -> user.username)
          else
            Redirect(controllers.routes.LoginController.login())
      }

    )
  }

  def fromWuecampus(userName: String, courseId: Int, courseName: String): Action[AnyContent] = Action.async {
    // FIXME: not used yet...
    implicit request =>
      if (userName.isEmpty)
        Future(Redirect(routes.LoginController.login()))
      else {
        findOrCreateStudent(userName, passwort = "").map {
          user =>

            //      if (Course.finder.byId(courseId) == null && courseId != -1) {
            //        // Create course
            //        val course = new Course()
            //        course.name = courseName
            //        course.save()
            //      }

            Redirect(controllers.routes.Application.index()).withSession(SESSION_ID_FIELD -> user.get.username)
        }
      }
  }

  def login(tries: Int = 0) = Action {
    implicit request => Ok(views.html.login())
  }

  def logout = Action {
    implicit request =>
      Redirect(routes.LoginController.login()).withNewSession
  }

  def findOrCreateStudent(username: String, passwort: String): Future[Option[User]] = db.run(repo.users.filter(_.username === username).result.headOption)

  /*.map {
     case Some(user) => user
     case None =>
       val newUser = User(username)
       db.run(repo.users += newUser).map(_ => newUser)
   }*/

}
