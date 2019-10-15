package controllers

import javax.inject._
import model.core.Repository
import model.feedback.FeedbackResult
import model.toolMains._
import model.{RequestBodyHelpers, Role, UpdateRoleForm}
import play.api.Logger
import play.api.data.Form
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.libs.json.Json
import play.api.mvc._
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

class AdminController @Inject()(cc: ControllerComponents, val dbConfigProvider: DatabaseConfigProvider, val repository: Repository, toolList: ToolList)(implicit ec: ExecutionContext)
  extends AbstractController(cc) with HasDatabaseConfigProvider[JdbcProfile] with Secured {

  override protected val adminRightsRequired: Boolean = true

  private val logger = Logger(classOf[AdminController])

  def changeRole: EssentialAction = futureWithUser { admin =>
    implicit request =>

      val onFormError: Form[UpdateRoleForm] => Future[Result] = { formWithErrors =>

        for (formError <- formWithErrors.errors)
          logger.error(formError.message)

        Future(BadRequest("TODO!"))
      }

      val onFromValue: UpdateRoleForm => Future[Result] = { updateRoleForm =>
        repository.updateUserRole(updateRoleForm.username, updateRoleForm.newRole) map { roleChanged =>
          if (roleChanged) Ok(Json.obj("name" -> updateRoleForm.username, "stdRole" -> updateRoleForm.newRole.entryName))
          else BadRequest("TODO!")
        }
      }

      if (admin.stdRole != Role.RoleSuperAdmin) Future(Forbidden("You do not have sufficient privileges to change roles!"))
      else RequestBodyHelpers.updateRoleForm.bindFromRequest().fold(onFormError, onFromValue)
  }

  def evaluationResults: EssentialAction = futureWithUser { user =>
    implicit request =>
      repository.futureAllEvaluatedTools.flatMap { allEvaluatedTools: Seq[String] =>

        val results = Future.sequence(allEvaluatedTools.map { evaluatedTool =>
          repository.futureAllEvaluationResultsForTool(evaluatedTool).map {
            feedbacks => FeedbackResult(evaluatedTool, feedbacks)
          }
        })

        results.map { feedbackResults =>
          Ok(views.html.evaluation.stats(user, feedbackResults))
        }
      }
  }

  def index: EssentialAction = futureWithUser { admin =>
    implicit request =>
      for {
        numOfUsers <- repository.numOfUsers
      } yield Ok(views.html.admin.adminIndex(admin, numOfUsers, toolList))
  }

  def users: EssentialAction = futureWithUser { admin =>
    implicit request => repository.allUsers map (allUsers => Ok(views.html.admin.userOverview(admin, allUsers, toolList)))
  }

}
