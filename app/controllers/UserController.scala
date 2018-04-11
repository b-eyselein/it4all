package controllers

import com.github.t3hnar.bcrypt._
import javax.inject._
import model.Enums.ShowHideAggregate
import model.FormMappings._
import model.core.Repository
import play.api.data.Form
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, ControllerComponents, EssentialAction, Result}
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

class UserController @Inject()(cc: ControllerComponents, val dbConfigProvider: DatabaseConfigProvider, val repository: Repository)(implicit ec: ExecutionContext)
  extends AbstractController(cc) with HasDatabaseConfigProvider[JdbcProfile] with Secured {

  def index: EssentialAction = withUser { user => implicit request => Ok(views.html.user("User", user)) }

  def preferences: EssentialAction = withUser { user => implicit request => Ok(views.html.preferences("Präferenzen", user)) }

  def myCourses: EssentialAction = futureWithUser { user =>
    implicit request => repository.coursesForUser(user) map (courses => Ok(views.html.myCourses(user, courses)))
  }

  def saveShowHideAgg: EssentialAction = futureWithUser { user =>
    implicit request =>

      val onFormError: Form[String] => Future[Result] = { _ =>
        Future(BadRequest("TODO"))
      }

      val onRead: String => Future[Result] = { str =>
        ShowHideAggregate.byString(str) match {
          case None         => Future(BadRequest("TODO!"))
          case Some(newVal) =>
            repository.updateShowHideAggregate(user, newVal) map {
              case 1 => Ok(Json.obj("todo" -> newVal.toString))
              case _ => BadRequest("TODO!")
            }
        }
      }

      saveOptionsForm.bindFromRequest().fold(onFormError, onRead)
  }

  def saveNewPassword: EssentialAction = futureWithAdmin { user =>
    implicit request =>
      val onFormError: Form[(String, String, String)] => Future[Result] = { _ =>
        Future(BadRequest("Es gab einen Fehler beim Einlesen ihrer Daten!"))
      }

      val onFormRead: ((String, String, String)) => Future[Result] = { pwChangeData =>

        repository.pwHashForUser(user.username) flatMap {
          case None         => Future(BadRequest("Could not change password!"))
          case Some(pwHash) =>

            if (pwChangeData._1.isBcrypted(pwHash.pwHash) && pwChangeData._2 == pwChangeData._3) {
              repository.updateUserPassword(user, pwChangeData._2) map {
                case 1 => Ok(Json.obj("changed" -> true, "reason" -> ""))
                case _ => Ok(Json.obj("changed" -> false, "reason" -> "Ihr Passwort konnte nicht gespeichert werden. Waren eventuell die Daten falsch?"))
              }
            }
            else Future(Ok(Json.obj("changed" -> false, "reason" -> "Ihr Passwort konnte nicht gespeichert werden.")))
        }
      }

      pwChangeForm.bindFromRequest().fold(onFormError, onFormRead)
  }


}
