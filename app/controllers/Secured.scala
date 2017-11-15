package controllers

import model.User
import model.core.CoreConsts.SESSION_ID_FIELD
import model.core.Repository
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

trait Secured {
  self: BaseController =>

  val repo: Repository

  private def username(request: RequestHeader): Option[String] = request.session.get(SESSION_ID_FIELD)

  private def onUnauthorized(request: RequestHeader): Result = Redirect(controllers.routes.LoginController.login())

  private def futureOnUnauthorized(request: RequestHeader)(implicit ec: ExecutionContext): Future[Result] =
    Future(Redirect(controllers.routes.LoginController.login()))

  private def withAuth(f: => String => Request[AnyContent] => Future[Result]): EssentialAction = {
    Security.Authenticated(username, onUnauthorized)(user => Action.async(request => f(user)(request)))
  }

  def withUser(f: User => Request[AnyContent] => Result)(implicit ec: ExecutionContext): EssentialAction = withAuth { username =>
    implicit request => {
      repo.userByName(username).map {
        case Some(user) => f(user)(request)
        case None       => onUnauthorized(request)
      }
    }
  }

  def futureWithUser(f: User => Request[AnyContent] => Future[Result])(implicit ec: ExecutionContext): EssentialAction = withAuth { username =>
    implicit request => {
      repo.userByName(username).flatMap {
        case Some(user) => f(user)(request)
        case None       => futureOnUnauthorized(request)
      }
    }
  }

  def withAdmin(f: User => Request[AnyContent] => Result)(implicit ec: ExecutionContext): EssentialAction = withAuth { username =>
    implicit request => {
      repo.userByName(username).map {
        case Some(user) =>
          if (user.isAdmin) f(user)(request)
          else onUnauthorized(request)
        case None       => onUnauthorized(request)
      }
    }
  }

  def futureWithAdmin(f: User => Request[AnyContent] => Future[Result])(implicit ec: ExecutionContext): EssentialAction = withAuth { username =>
    implicit request => {
      repo.userByName(username).flatMap {
        case Some(user) =>
          if (user.isAdmin) f(user)(request)
          else futureOnUnauthorized(request)
        case None       => futureOnUnauthorized(request)
      }
    }
  }

}
