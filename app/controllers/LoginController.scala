package controllers

import com.github.t3hnar.bcrypt._
import javax.inject._
import model.FormMappings._
import model.core.CoreConsts._
import model.core.Repository
import model.{FormMappings, User}
import play.api.data.Form
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.libs.json.Json
import play.api.mvc._
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}
import scala.language.postfixOps

class LoginController @Inject()(cc: ControllerComponents, val dbConfigProvider: DatabaseConfigProvider, val repo: Repository)
                               (implicit ec: ExecutionContext)
  extends AbstractController(cc) with HasDatabaseConfigProvider[JdbcProfile] {

  def registerForm = Action { implicit request => Ok(views.html.register.render()) }

  def register: Action[AnyContent] = Action.async { implicit request =>

    val onError: Form[UserCredForm] => Future[Result] = { _ =>
      Future(BadRequest("There has been an error in your form..."))
    }

    val onRead: UserCredForm => Future[Result] = { credentials =>
      val newUser = User(credentials.username, credentials.password.bcrypt)
      repo.saveUser(newUser) map {
        _ => Ok(views.html.registered.render(credentials.username))
      } recover {
        // TODO: Fehlermeldung
        case e => BadRequest("Error while inserting into db..." + e.getMessage)
      }
    }

    userCredForm.bindFromRequest.fold(onError, onRead)
  }

  /**
    * Checks username in register form with ajax request
    *
    * FIXME: entfernen?
    *
    * @return {{userexists: Boolean, username: String (from form)}}
    */
  def checkUserName: Action[AnyContent] = Action.async { implicit request =>
    val onError: Form[String] => Future[Result] = { _ =>
      Future(BadRequest("TODO!"))
    }

    val onRead: String => Future[Result] = { userName =>
      repo.userByName(userName) map (res => Ok(Json.obj("userexists" -> res.isDefined, "username" -> userName)))
    }

    singleStrForm(nameName).bindFromRequest.fold(onError, onRead)
  }

  def authenticate: Action[AnyContent] = Action.async { implicit request =>

    val onError: Form[UserCredForm] => Future[Result] = { formWithErrors =>
      Future(BadRequest(views.html.login(formWithErrors)))
    }

    val onRead: UserCredForm => Future[Result] = { credentials =>
      repo.userByName(credentials.username) map {
        case None       => Redirect(controllers.routes.LoginController.register())
        case Some(user) =>
          val pwOkay = credentials.password isBcrypted user.pwHash
          if (pwOkay)
            Redirect(controllers.routes.Application.index()).withSession(SESSION_ID_FIELD -> user.username)
          else
            Ok(views.html.login(FormMappings.userCredForm.fill(credentials)))
      }
    }

    userCredForm.bindFromRequest.fold(onError, onRead)
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

  def login = Action { implicit request => Ok(views.html.login(FormMappings.userCredForm)) }

  def logout = Action { implicit request => Redirect(routes.LoginController.login()).withNewSession }

}
