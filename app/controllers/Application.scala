package controllers

import javax.inject._
import model.core.Repository
import model.ideTest.IdeFilesTest
import model.toolMains.ToolList
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.libs.json.JsArray
import play.api.mvc.{AbstractController, ControllerComponents, EssentialAction}
import slick.jdbc.JdbcProfile

import scala.concurrent.ExecutionContext

class Application @Inject()(cc: ControllerComponents, val dbConfigProvider: DatabaseConfigProvider, toolList: ToolList, val repository: Repository)(implicit ec: ExecutionContext)
  extends AbstractController(cc) with HasDatabaseConfigProvider[JdbcProfile] with Secured {

  def index: EssentialAction = withUser { user => implicit request => Ok(views.html.index(user, toolList.toolMains)) }

  def blocklyTest: EssentialAction = withUser { user =>
    implicit request => Ok(views.html.blocklyTest(user))
  }

  def ideTest: EssentialAction = withAdmin { admin =>
    implicit request =>
      Ok(views.html.ideTest(admin, IdeFilesTest.files, IdeFilesTest.files.headOption))
  }

  def ideFiles: EssentialAction = withAdmin { _ =>
    implicit request =>
      // TODO: read files from request...
      Ok(JsArray(IdeFilesTest.files map (_.toJson)))
  }

}
