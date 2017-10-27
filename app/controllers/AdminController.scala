package controllers

import javax.inject._

import controllers.core.BaseController
import model.core.{Repository, Secured}
import model.feedback.FeedbackResult
import play.api.db.slick.DatabaseConfigProvider
import play.api.mvc.{ControllerComponents, EssentialAction}

import scala.concurrent.ExecutionContext

class AdminController @Inject()(cc: ControllerComponents, dbcp: DatabaseConfigProvider, r: Repository)
                               (implicit ec: ExecutionContext)
  extends BaseController(cc, dbcp, r) with Secured {

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
      repo.numOfUsers.zip(repo.numOfCourses).map(nums => Ok(views.html.admin.adminPage.render(user, nums._1, nums._2)))
  }

  def users: EssentialAction = futureWithAdmin { user =>
    implicit request =>
      repo.allUsers.map(users => Ok(views.html.admin.users.render(user, users)))

  }

}