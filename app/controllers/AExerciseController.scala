package controllers

import model.User
import model.toolMains.AToolMain
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.mvc._
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}


abstract class AExerciseController(cc: ControllerComponents, val dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext)
  extends AbstractController(cc) with HasDatabaseConfigProvider[JdbcProfile] with Secured  {

  protected type ToolMainType <: AToolMain

  protected def getToolMain(toolType: String): Option[ToolMainType]

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
