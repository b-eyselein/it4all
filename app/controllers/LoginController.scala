package controllers

import com.github.t3hnar.bcrypt._
import javax.inject._
import model.FormMappings._
import model.core.CoreConsts._
import model.core.Repository
import model.{FormMappings, PwHash, RegisteredUser, User}
import play.api.data.Form
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.libs.json.Json
import play.api.mvc._
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}
import scala.language.postfixOps

class LoginController @Inject()(cc: ControllerComponents, val dbConfigProvider: DatabaseConfigProvider, val repo: Repository)(implicit ec: ExecutionContext)
  extends AbstractController(cc) with HasDatabaseConfigProvider[JdbcProfile] with play.api.i18n.I18nSupport {

  def registerForm: Action[AnyContent] = Action {
    implicit request => Ok(views.html.registerForm(userCredForm))
  }

  def register: Action[AnyContent] = Action.async { implicit request =>

    val onError: Form[UserCredentials] => Future[Result] = { _ =>
      Future(BadRequest("There has been an error in your form..."))
    }

    val onRead: UserCredentials => Future[Result] = { credentials =>
      val newUser = RegisteredUser(credentials.username)
      val pwHash = PwHash(credentials.username, credentials.password.bcrypt)

      repo.saveUser(newUser) flatMap {
        case false => Future(BadRequest("Could not save user!"))
        case true  =>
          repo.savePwHash(pwHash) map {
            _ => Ok(views.html.registered.render(credentials.username))
          }
      }
    }

    userCredForm.bindFromRequest.fold(onError, onRead)
  }

  def authenticate: Action[AnyContent] = Action.async { implicit request =>

    val onError: Form[UserCredentials] => Future[Result] = { formWithErrors =>
      Future(BadRequest(views.html.loginForm(formWithErrors)))
    }

    val onRead: UserCredentials => Future[Result] = { credentials =>

      val futureUserAndPwHash: Future[(Option[User], Option[PwHash])] = for {
        user <- repo.userByName(credentials.username)
        pwHash <- repo.pwHashForUser(credentials.username)
      } yield (user, pwHash)

      futureUserAndPwHash map {
        case (None, _)                  => Redirect(controllers.routes.LoginController.register())
        case (Some(_), None)            => BadRequest("Cannot change password!")
        case (Some(user), Some(pwHash)) =>
          if (credentials.password isBcrypted pwHash.pwHash) {
            Redirect(controllers.routes.Application.index()).withSession(sessionIdField -> user.username)
          } else {
            Ok(views.html.loginForm(FormMappings.userCredForm.fill(credentials)))
          }
      }
    }

    userCredForm.bindFromRequest.fold(onError, onRead)
  }

  def fromWuecampus(userName: String, courseId: Int, courseName: String): Action[AnyContent] = Action.async {
    // FIXME: not used yet...
    implicit request =>
      if (userName.isEmpty)
        Future(Redirect(routes.LoginController.loginForm()))
      else {
        Future(Redirect(routes.LoginController.loginForm()))
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

  def loginForm: Action[AnyContent] = Action {
    implicit request => Ok(views.html.loginForm(FormMappings.userCredForm))
  }

  def logout: Action[AnyContent] = Action {
    implicit request => Redirect(routes.LoginController.loginForm()).withNewSession
  }

}
