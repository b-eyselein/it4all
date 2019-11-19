package controllers

import model.User
import model.core.CoreConsts.sessionIdField
import model.persistence.TableDefs
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

@deprecated
trait Secured {
  self: AbstractController =>

  protected val repository: TableDefs

  protected val adminRightsRequired: Boolean


  private def username(request: RequestHeader): Option[String] = request.session.get(sessionIdField)

  private def onUnauthorized: RequestHeader => Result = _ => Redirect(controllers.routes.FrontendController.index()).withNewSession

  private def onInsufficientPrivileges(): Result = Redirect(routes.FrontendController.index()).flashing("msg" -> "You do not have sufficient privileges!")


  private def withAuth(f: => String => Request[AnyContent] => Future[Result]): EssentialAction =
    Security.Authenticated(username, onUnauthorized)(user => controllerComponents.actionBuilder.async(request => f(user)(request)))


  def withUser(f: User => Request[AnyContent] => Result)(implicit ec: ExecutionContext): EssentialAction = withAuth { username =>
    implicit request => {
      repository.userByName(username).map {
        case None       => onUnauthorized(request)
        case Some(user) =>
          if (!adminRightsRequired || user.isAdmin) f(user)(request)
          else onInsufficientPrivileges()
      }
    }
  }

  def futureWithUser(f: User => Request[AnyContent] => Future[Result])(implicit ec: ExecutionContext): EssentialAction = withAuth { username =>
    implicit request =>
      repository.userByName(username).flatMap {
        case None       => Future.successful(onUnauthorized(request))
        case Some(user) =>
          if (!adminRightsRequired || user.isAdmin) f(user)(request)
          else Future.successful(onInsufficientPrivileges())
      }

  }

}
