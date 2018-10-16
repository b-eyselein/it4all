package controllers

import javax.inject._
import model.core.Repository
import model.toolMains.ToolList
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.mvc.{AbstractController, ControllerComponents, EssentialAction}
import play.api.routing.JavaScriptReverseRouter
import slick.jdbc.JdbcProfile

import scala.concurrent.ExecutionContext

class Application @Inject()(cc: ControllerComponents, val dbConfigProvider: DatabaseConfigProvider, toolList: ToolList, val repository: Repository)(implicit ec: ExecutionContext)
  extends AbstractController(cc) with HasDatabaseConfigProvider[JdbcProfile] with Secured {

  def index: EssentialAction = withUser { user => implicit request => Ok(views.html.index(user, toolList.toolMains)) }

  def blocklyTest: EssentialAction = withUser { user =>
    implicit request => Ok(views.html.blocklyTest(user))
  }

}
