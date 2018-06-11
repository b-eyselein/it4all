package controllers

import model.User
import model.core.CoreConsts.sessionIdField
import model.persistence.TableDefs
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

trait Secured {
  self: AbstractController =>

  protected val repository: TableDefs

  private def username(request: RequestHeader): Option[String] = request.session.get(sessionIdField)

  private def onUnauthorized(request: RequestHeader): Result = Redirect(controllers.routes.LoginController.login()).withNewSession

  private def futureOnUnauthorized(request: RequestHeader)(implicit ec: ExecutionContext): Future[Result] =
    Future(onUnauthorized(request))

  private def onInsufficientPrivileges(request: RequestHeader): Result = Redirect(routes.Application.index())

  private def futureOnInsufficientPrivileges(request: RequestHeader)(implicit ec: ExecutionContext): Future[Result] =
    Future(onInsufficientPrivileges(request))


  private def withAuth(f: => String => Request[AnyContent] => Future[Result]): EssentialAction =
    Security.Authenticated(username, onUnauthorized)(user => controllerComponents.actionBuilder.async(request => f(user)(request)))

  private def withAuth[A](bodyParser: BodyParser[A])(f: => String => Request[A] => Future[Result]): EssentialAction =
    Security.Authenticated(username, onUnauthorized)(user => controllerComponents.actionBuilder.async(bodyParser)(request => f(user)(request)))


  def withUser(f: User => Request[AnyContent] => Result)(implicit ec: ExecutionContext): EssentialAction = withAuth { username =>
    implicit request => {
      repository.userByName(username) map {
        case Some(user) => f(user)(request)
        case None       => onUnauthorized(request)
      }
    }
  }

  def futureWithUser(f: User => Request[AnyContent] => Future[Result])(implicit ec: ExecutionContext): EssentialAction = withAuth { username =>
    implicit request =>
      repository.userByName(username) flatMap {
        case Some(user) => f(user)(request)
        case None       => futureOnUnauthorized(request)
      }

  }

  def futureWithUser[A](bodyParser: BodyParser[A])(f: User => Request[A] => Future[Result])(implicit ec: ExecutionContext): EssentialAction = withAuth(bodyParser) { username =>
    implicit request =>
      repository.userByName(username) flatMap {
        case Some(user) => f(user)(request)
        case None       => futureOnUnauthorized(request)
      }
  }

  def withAdmin(f: User => Request[AnyContent] => Result)(implicit ec: ExecutionContext): EssentialAction = withAuth { username =>
    implicit request =>
      repository.userByName(username) map {
        case Some(user) =>
          if (user.isAdmin) f(user)(request)
          else onInsufficientPrivileges(request)
        case None       => onUnauthorized(request)
      }

  }

  def futureWithAdmin(f: User => Request[AnyContent] => Future[Result])(implicit ec: ExecutionContext): EssentialAction = withAuth { username =>
    implicit request =>
      repository.userByName(username) flatMap {
        case Some(user) =>
          if (user.isAdmin) f(user)(request)
          else futureOnInsufficientPrivileges(request)
        case None       => futureOnUnauthorized(request)
      }

  }

}
