package controllers

import com.github.t3hnar.bcrypt._
import javax.inject._
import model._
import model.core.CoreConsts._
import model.core.Repository
import play.api.Configuration
import play.api.data.Form
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.mvc._
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

class LoginController @Inject()(
  cc: ControllerComponents,
  val dbConfigProvider: DatabaseConfigProvider,
  repo: Repository
)(implicit configuration: Configuration, ec: ExecutionContext)
  extends AbstractController(cc)
    with HasDatabaseConfigProvider[JdbcProfile]
    with play.api.i18n.I18nSupport {

  def registerForm: Action[AnyContent] = Action {
    implicit request => Ok(views.html.registerForm(RequestBodyHelpers.userCredForm))
  }

  def register: Action[AnyContent] = Action.async { implicit request =>

    val onError: Form[UserCredentials] => Future[Result] = { _ =>
      Future(Redirect(routes.LoginController.registerForm()))
    }

    val onRead: UserCredentials => Future[Result] = { credentials =>
      val newUser = RegisteredUser(credentials.username)
      val pwHash  = PwHash(credentials.username, credentials.password.bcrypt)

      repo.saveUser(newUser).flatMap {
        case false => Future(BadRequest("Could not save user!"))
        case true  =>
          repo.savePwHash(pwHash) map {
            _ => Ok(views.html.registered(credentials.username))
          }
      }
    }

    RequestBodyHelpers.userCredForm.bindFromRequest.fold(onError, onRead)
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
            Redirect(controllers.routes.FrontendController.index()).withSession(sessionIdField -> user.username)
          } else {
            Ok(views.html.loginForm(RequestBodyHelpers.userCredForm.fill(credentials)))
          }
      }
    }

    RequestBodyHelpers.userCredForm.bindFromRequest.fold(onError, onRead)
  }

  def loginForm: Action[AnyContent] = Action {
    implicit request => Ok(views.html.loginForm(RequestBodyHelpers.userCredForm))
  }

  def logout: Action[AnyContent] = Action {
    implicit request => Redirect(routes.LoginController.loginForm()).withNewSession
  }

}
