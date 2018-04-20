package controllers

import javax.inject.{Inject, Singleton}
import model.bool.{BoolLearningPath, BoolToolMain}
import model.core.Repository
import model.nary.NaryToolMain
import model.toolMains.{RandomExerciseToolMain, ToolList}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.mvc.{ControllerComponents, EssentialAction}
import slick.jdbc.JdbcProfile

import scala.concurrent.ExecutionContext


@Singleton
class RandomExerciseController @Inject()(cc: ControllerComponents, dbcp: DatabaseConfigProvider, val repository: Repository)(implicit ec: ExecutionContext)
  extends AExerciseController(cc, dbcp) with HasDatabaseConfigProvider[JdbcProfile] with Secured {

  // Abstract types

  override protected type ToolMainType = RandomExerciseToolMain

  override protected def getToolMain(toolType: String): Option[RandomExerciseToolMain] = ToolList.getRandomExToolMainOption(toolType)

  // Routes

  def index(toolType: String): EssentialAction = withUserWithToolMain(toolType) { (user, toolMain) =>
    implicit request => Ok(toolMain.index(user))
  }

  def newExercise(toolType: String, exType: String): EssentialAction = withUserWithToolMain(toolType) { (user, toolMain) =>
    implicit request =>
      toolMain.exTypeFromUrl(exType) match {
        case None         => BadRequest(s"There is no exercise part >>$exType<<")
        case Some(exPart) => Ok(toolMain.newExercise(user, exPart, request.queryString))
      }
  }

  def correctLive(toolType: String, exType: String): EssentialAction = withUserWithToolMain(toolType) { (user, toolMain) =>
    implicit request =>
      toolMain.exTypeFromUrl(exType) match {
        case None         => BadRequest(s"There is no exercise part >>$exType<<")
        case Some(exPart) => Ok(toolMain.checkSolution(user, exPart, request))
      }
  }

  def learningPath(toolType: String, id: Int): EssentialAction = withUserWithToolMain(toolType) { (user, toolMain) =>
    implicit request =>
      toolMain match {
        case _: BoolToolMain => Ok(views.html.bool.boolLearningPath(user, BoolLearningPath))
        case _: NaryToolMain => Ok("TODO!")
      }
  }

}