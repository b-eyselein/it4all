package controllers

import javax.inject._

import model.core.{Repository, Secured}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, ControllerComponents, EssentialAction}
import slick.jdbc.JdbcProfile

import scala.concurrent.ExecutionContext

class UserController @Inject()(cc: ControllerComponents, val dbConfigProvider: DatabaseConfigProvider, val repo: Repository)
                              (implicit ec: ExecutionContext)
  extends AbstractController(cc) with HasDatabaseConfigProvider[JdbcProfile] with Secured {

  def index: EssentialAction = withUser { user => implicit request => Ok(views.html.user.render("User", user)) }

  def preferences: EssentialAction = withUser { user => implicit request => Ok(views.html.preferences.render("Präferenzen", user)) }

  def saveOptions: EssentialAction = withUser { user =>
    implicit request =>
      //      user.todo = SHOW_HIDE_AGGREGATE.withName(singleStrForm("posTests").get.str)
      //      user.save()

      // FIXME: tell user that settings habe been saved!
      Ok(Json.obj("todo" -> user.todo.toString))
  }
}
