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

import scala.language.postfixOps

class Application @Inject()(cc: ControllerComponents, val dbConfigProvider: DatabaseConfigProvider, toolList: ToolList, val repository: Repository)(implicit ec: ExecutionContext)
  extends AbstractController(cc) with HasDatabaseConfigProvider[JdbcProfile] with Secured with play.api.i18n.I18nSupport {

  def index: EssentialAction = withUser { user => implicit request => Ok(views.html.index(user, toolList.toolMains)) }

  def blocklyTest: EssentialAction = withUser { user =>
    implicit request => Ok(views.html.blocklyTest(user))
  }

  def ideTest: EssentialAction = withAdmin { admin =>
    implicit request =>
      Ok(views.html.ideTest(admin, IdeFilesTest.files.keys toSeq, IdeFilesTest.files.headOption.map(_._2)))
  }

  def ideFiles: EssentialAction = withAdmin { _ =>
    implicit request =>
      // TODO: read files from request...s
      //      println(request.body)

      Ok(JsArray(IdeFilesTest.files.values map (_.toJson) toSeq))
  }

}
