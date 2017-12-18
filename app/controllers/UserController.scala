package controllers

import javax.inject._

import com.github.t3hnar.bcrypt._
import model.Enums.ShowHideAggregate
import model.core.Repository
import play.api.data.Form
import play.api.data.Forms._
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, ControllerComponents, EssentialAction}
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

class UserController @Inject()(cc: ControllerComponents, val dbConfigProvider: DatabaseConfigProvider, val repo: Repository)
                              (implicit ec: ExecutionContext)
  extends AbstractController(cc) with HasDatabaseConfigProvider[JdbcProfile] with Secured {

  def index: EssentialAction = withUser { user => implicit request => Ok(views.html.user.render("User", user)) }

  def preferences: EssentialAction = withUser { user => implicit request => Ok(views.html.preferences.render("Präferenzen", user)) }

  private val saveOptionsForm = Form("posTests" -> nonEmptyText)

  private val pwChangeForm = Form(tuple("oldpw" -> nonEmptyText, "newpw1" -> nonEmptyText, "newpw2" -> nonEmptyText))

  def saveShowHideAgg: EssentialAction = futureWithUser { user =>
    implicit request =>
      saveOptionsForm.bindFromRequest().fold(_ => Future(BadRequest("TODO!")),
        str => ShowHideAggregate.byString(str) match {
          case None         => Future(BadRequest("TODO!"))
          case Some(newVal) =>
            repo.updateShowHideAggregate(user, newVal) map {
              case 1 => Ok(Json.obj("todo" -> newVal.toString))
              case _ => BadRequest("TODO!")
            }
        })
  }

  def saveNewPassword: EssentialAction = futureWithAdmin { user =>
    implicit request =>
      pwChangeForm.bindFromRequest().fold(_ => Future(BadRequest("Es gab einen Fehler beim Einlesen ihrer Daten!")),
        pwChangeData =>
          if (pwChangeData._1.isBcrypted(user.pwHash) && pwChangeData._2 == pwChangeData._3) {
            repo.updateUserPassword(user, pwChangeData._2) map {
              case 1 => Ok(Json.obj("changed" -> true, "reason" -> ""))
              case _ => Ok(Json.obj("changed" -> false, "reason" -> "Ihr Passwort konnte nicht gespeichert werden. Waren eventuell die Daten falsch?"))
            }
          }
          else Future(Ok(Json.obj("changed" -> false, "reason" -> "Ihr Passwort konnte nicht gespeichert werden."))))
  }


}
