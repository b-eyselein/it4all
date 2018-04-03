package controllers

import javax.inject.Inject
import model.core.{CoreConsts, Repository}
import model.lti.BasicLtiLaunchRequest
import model.{Course, User, UserInCourse}
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

class LTIController @Inject()(cc: ControllerComponents, tables: Repository)(implicit ec: ExecutionContext) extends AbstractController(cc) {

  def honeypot: Action[AnyContent] = Action.async { request =>
    request.body.asFormUrlEncoded match {
      case None       => Future(BadRequest("TODO!"))
      case Some(data) =>

        val basicLtiRequest = BasicLtiLaunchRequest.fromRequest(data)

        val username = basicLtiRequest.ltiExt.username

        val courseId = basicLtiRequest.ltiContext.label
        val courseTitle = basicLtiRequest.ltiContext.title

        //        val roles = basicLtiRequest.roles

        val futureUserAndCourse = for {
          user <- getOrCreateUser(username)
          course <- getOrCreateCourse(courseId, courseTitle)
          userInCourse <- addUserToCourse(user, course)
        } yield (user, course, userInCourse)


        futureUserAndCourse map {
          case (user, _, _) => Redirect(routes.Application.index()).withNewSession.withSession(CoreConsts.sessionIdField -> user.username)
        }

    }
  }

  private def getOrCreateUser(username: String): Future[User] = tables.userByName(username) flatMap {
    case Some(u) => Future(u)
    case None    =>
      val newUser = User(username, "")
      val userSaved = tables.saveUser(newUser)
      userSaved.map(_ => newUser)
  }

  private def getOrCreateCourse(courseId: String, courseTitle: String): Future[Course] = tables.courseById(courseId) flatMap {
    case Some(c) => Future(c)
    case None    =>
      val newCourse = Course(courseId, courseTitle)
      val courseSaved = tables.saveCourse(newCourse)
      courseSaved.map(_ => newCourse)
  }

  private def addUserToCourse(user: User, course: Course): Future[UserInCourse] = tables.userInCourse(user, course) flatMap {
    case Some(uInC) => Future(uInC)
    case None       =>
      val userInCourse = UserInCourse(user.username, course.id)
      val userInCourseSaved = tables.addUserToCourse(userInCourse)
      userInCourseSaved map (_ => userInCourse)
  }

}
