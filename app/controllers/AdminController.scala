package controllers

import javax.inject._
import model.Enums.Role
import model.FormMappings
import model.FormMappings.UpdateRoleForm
import model.core.Repository
import model.feedback.{Feedback, FeedbackResult}
import play.api.Logger
import play.api.data.Form
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, ControllerComponents, EssentialAction, Result}
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}
import scala.language.postfixOps

class AdminController @Inject()(cc: ControllerComponents, val dbConfigProvider: DatabaseConfigProvider, val repository: Repository)(implicit ec: ExecutionContext)
  extends AbstractController(cc) with HasDatabaseConfigProvider[JdbcProfile] with Secured {

  def changeRole: EssentialAction = futureWithAdmin { admin =>
    implicit request =>

      val onFormError: Form[UpdateRoleForm] => Future[Result] = { formWithErrors =>

        for (formError <- formWithErrors.errors)
          Logger.error(formError.message)

        Future(BadRequest("TODO!"))
      }

      val onFromValue: UpdateRoleForm => Future[Result] = { updateRoleForm =>
        repository.updateUserRole(updateRoleForm.username, updateRoleForm.newRole) map { roleChanged =>
          if (roleChanged) Ok(Json.obj("name" -> updateRoleForm.username, "stdRole" -> updateRoleForm.newRole.name))
          else BadRequest("TODO!")
        }
      }

      if (admin.stdRole != Role.RoleSuperAdmin)
        Future(Forbidden("You do not have sufficient privileges to change roles!"))
      else
        FormMappings.updateRoleForm.bindFromRequest().fold(onFormError, onFromValue)
  }

  def evaluationResults: EssentialAction = futureWithAdmin { user =>
    implicit request =>
      repository.futureEvaluationResultsForTools map { evaluationResultsForTools: Seq[Feedback] =>

        val results: Seq[FeedbackResult] = evaluationResultsForTools.groupBy(_.toolUrlPart) map {
          case (toolUrl, allResults) => FeedbackResult(toolUrl, allResults)
        } toSeq

        Ok(views.html.evaluation.stats(user, results))
      }
  }

  def index: EssentialAction = futureWithAdmin { user =>
    implicit request =>
      for {
        numOfUsers <- repository.numOfUsers
        numOfCourses <- repository.numOfCourses
      } yield Ok(views.html.admin.adminIndex(user, numOfUsers, numOfCourses))
  }

  def users: EssentialAction = futureWithAdmin { admin =>
    implicit request => repository.allUsers map (allUsers => Ok(views.html.admin.users(admin, allUsers)))
  }

  def courses: EssentialAction = futureWithAdmin { admin =>
    implicit request => repository.allCourses map (allCourses => Ok(views.html.admin.courses(admin, allCourses)))
  }

}