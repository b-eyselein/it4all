package controllers

import model.User
import model.core.{ExerciseFormMappings, FileUtils}
import model.learningPath.LearningPath
import model.toolMains.AToolMain
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.mvc._
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}


abstract class AExerciseController(cc: ControllerComponents, val dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext)
  extends AbstractController(cc) with HasDatabaseConfigProvider[JdbcProfile] with Secured with FileUtils with ExerciseFormMappings {

  protected type ToolMainType <: AToolMain

  protected def getToolMain(toolType: String): Option[ToolMainType]

  // Routes

  def index(toolType: String): EssentialAction = futureWithUserWithToolMain(toolType) { (user, toolMain) =>
    implicit request => toolMain.futureLearningPaths map (paths => Ok(toolMain.index(user, paths)))
  }

  def readLearningPaths(toolType: String): EssentialAction = futureWithUserWithToolMain(toolType) { (user, toolMain) =>
    implicit request =>
      val readLearningPaths: Seq[LearningPath] = toolMain.readLearningPaths

      toolMain.futureSaveLearningPaths(readLearningPaths) map {
        _ => Ok(views.html.admin.learningPathRead(user, readLearningPaths))
      }
  }

  def learningPath(toolType: String, id: Int): EssentialAction = futureWithUserWithToolMain(toolType) { (user, toolMain) =>
    implicit request =>
      toolMain.futureLearningPathById(id) map {
        case None     => BadRequest("No such learning path!")
        case Some(lp) => Ok(views.html.learningPath(user, lp, toolMain))
      }
  }

  // Helper methods

  private def onNoSuchTool(toolType: String): Result = BadRequest(s"There is no such tool with name $toolType")

  protected def withAdminWithToolMain(toolType: String)(f: (User, ToolMainType) => Request[AnyContent] => Result): EssentialAction = withAdmin { admin =>
    implicit request =>
      getToolMain(toolType) match {
        case None           => onNoSuchTool(toolType)
        case Some(toolMain) => f(admin, toolMain)(request)
      }
  }

  protected def withUserWithToolMain(toolType: String)(f: (User, ToolMainType) => Request[AnyContent] => Result): EssentialAction = withUser { user =>
    implicit request =>
      getToolMain(toolType) match {
        case None           => onNoSuchTool(toolType)
        case Some(toolMain) => f(user, toolMain)(request)
      }
  }

  protected def futureWithAdminWithToolMain(toolType: String)(f: (User, ToolMainType) => Request[AnyContent] => Future[Result]): EssentialAction = futureWithAdmin { admin =>
    implicit request =>
      getToolMain(toolType) match {
        case None           => Future(onNoSuchTool(toolType))
        case Some(toolMain) => f(admin, toolMain)(request)
      }
  }

  protected def futureWithUserWithToolMain(toolType: String)(f: (User, ToolMainType) => Request[AnyContent] => Future[Result]): EssentialAction = futureWithUser { user =>
    implicit request =>
      getToolMain(toolType) match {
        case None           => Future(onNoSuchTool(toolType))
        case Some(toolMain) => f(user, toolMain)(request)
      }
  }

}
