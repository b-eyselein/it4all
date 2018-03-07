package controllers.exes

import controllers.Secured
import javax.inject.{Inject, Singleton}
import model.core.Repository
import model.toolMains.ToolList
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.mvc.{AbstractController, ControllerComponents, EssentialAction}
import slick.jdbc.JdbcProfile

import scala.concurrent.ExecutionContext


@Singleton
class RandomExerciseController @Inject()(cc: ControllerComponents, val dbConfigProvider: DatabaseConfigProvider, val repository: Repository)
                                        (implicit ec: ExecutionContext)
  extends AbstractController(cc) with HasDatabaseConfigProvider[JdbcProfile] with Secured {

  def index(toolType: String): EssentialAction = withUser { user =>
    implicit request =>
      ToolList.getRandomExToolMainOption(toolType) match {
        case None           => BadRequest("TODO!")
        case Some(toolMain) => Ok(toolMain.index(user))

      }
  }

  def newExercise(toolType: String, exType: String): EssentialAction = withUser { user =>
    implicit request =>
      ToolList.getRandomExToolMainOption(toolType) match {
        case None           => BadRequest("TODO!")
        case Some(toolMain) => toolMain.exTypeFromUrl(exType) match {
          case None         => BadRequest(s"There is no exercise part >>$exType<<")
          case Some(exPart) => Ok(toolMain.newExercise(user, exPart, request.queryString))
        }
      }
  }

  def correctLive(toolType: String, exType: String): EssentialAction = withUser { user =>
    implicit request =>
      ToolList.getRandomExToolMainOption(toolType) match {
        case None           => BadRequest("TODO!")
        case Some(toolMain) => toolMain.exTypeFromUrl(exType) match {
          case None         => BadRequest(s"There is no exercise part >>$exType<<")
          case Some(exPart) => Ok(toolMain.checkSolution(user, exPart, request))
        }
      }
  }

}