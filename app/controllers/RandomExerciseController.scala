package controllers

import javax.inject.{Inject, Singleton}
import model.core.Repository
import model.toolMains.{RandomExerciseToolMain, ToolList}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.mvc.{ControllerComponents, EssentialAction}
import slick.jdbc.JdbcProfile

import scala.concurrent.ExecutionContext


@Singleton
class RandomExerciseController @Inject()(cc: ControllerComponents, dbcp: DatabaseConfigProvider, tl: ToolList, val repository: Repository)(implicit ec: ExecutionContext)
  extends AExerciseController(cc, dbcp, tl) with HasDatabaseConfigProvider[JdbcProfile] with Secured {

  // Abstract types

  override protected type ToolMainType = RandomExerciseToolMain

  override protected def getToolMain(toolType: String): Option[RandomExerciseToolMain] = toolList.getRandomExToolMainOption(toolType)

  // Routes

  def newExercise(toolType: String, exType: String): EssentialAction = withUserWithToolMain(toolType) { (user, toolMain) =>
    implicit request =>
      toolMain.exTypeFromUrl(exType) match {
        case None         => BadRequest(s"There is no exercise part >>$exType<<")
        case Some(exPart) => Ok(toolMain.newExercise(user, exPart, request.queryString))
      }
  }

  def correctLive(toolType: String, exType: String): EssentialAction = withUserWithToolMain(toolType) { (_, toolMain) =>
    implicit request =>
      toolMain.exTypeFromUrl(exType) match {
        case None         => BadRequest(s"There is no exercise part >>$exType<<")
        case Some(exPart) => Ok(toolMain.checkSolution(exPart, request))
      }
  }

  def boolPlayground: EssentialAction = withUser { user =>
    implicit request => Ok(views.html.randomExercises.bool.boolDrawing(user))
  }

}