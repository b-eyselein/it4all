package controllers

import javax.inject.Inject
import model.core.{CoreConsts, Repository}
import model.lti.BasicLtiLaunchRequest
import model.{LtiUser, User}
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

class LTIController @Inject()(cc: ControllerComponents, tables: Repository)(implicit ec: ExecutionContext) extends AbstractController(cc) {

  def honeypot: Action[AnyContent] = Action.async { request =>
    request.body.asFormUrlEncoded match {
      case None       => Future(BadRequest("TODO!"))
      case Some(data) =>

        val basicLtiRequest = BasicLtiLaunchRequest.fromRequest(data)

        val username = basicLtiRequest.ltiExt.username


        getOrCreateUser(username) map { user =>
          Redirect(routes.Application.index()).withNewSession.withSession(CoreConsts.sessionIdField -> user.username)
        }

    }
  }

  private def getOrCreateUser(username: String): Future[User] = tables.userByName(username) flatMap {
    case Some(u) => Future(u)
    case None    =>
      val newUser = LtiUser(username)
      val userSaved = tables.saveUser(newUser)
      userSaved.map(_ => newUser)
  }

}
