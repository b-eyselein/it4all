package controllers

import javax.inject._

import model.core.Repository
import model.feedback.FeedbackResult
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.mvc.{AbstractController, ControllerComponents, EssentialAction}
import slick.jdbc.JdbcProfile

import scala.concurrent.ExecutionContext

class AdminController @Inject()(cc: ControllerComponents, val dbConfigProvider: DatabaseConfigProvider, val repo: Repository)
                               (implicit ec: ExecutionContext)
  extends AbstractController(cc) with HasDatabaseConfigProvider[JdbcProfile] with Secured {

  def changeRole(username: String): EssentialAction = withAdmin { _ =>
    implicit request =>
      //      if (user.stdRole != Role.SUPERADMIN)
      //        Forbidden("You do not have sufficient privileges to change roles!")
      //      else {
      //        val newrole = singleStrForm("newrole").get.str
      //
      //        Option(User.finder.byId(username)) match {
      //          case None => BadRequest("TODO!")
      //          case Some(userToChange) =>
      //            userToChange.stdRole = Role.valueOf(newrole)
      //            userToChange.save()
      //
      //            Ok(Json.obj("user" -> userToChange.toString, "newRole" -> newrole))
      //        }
      //      }
      Ok("TODO!")
  }

  def evaluation: EssentialAction = withAdmin { user =>
    implicit request =>
      val results: List[FeedbackResult] = List.empty // FeedbackResult.evaluate(Feedback.finder.all.asScala.toList)
      Ok(views.html.evaluation.stats.render(user, results))
  }

  def index: EssentialAction = futureWithAdmin { user =>
    implicit request =>
      repo.numOfUsers.zip(repo.numOfCourses).map {
        case (numUsers, numCourses) => Ok(views.html.admin.adminPage.render(user, numUsers, numCourses))
      }
  }

  def users: EssentialAction = futureWithAdmin { user =>
    implicit request =>
      repo.allUsers.map(users => Ok(views.html.admin.users.render(user, users)))

  }

}