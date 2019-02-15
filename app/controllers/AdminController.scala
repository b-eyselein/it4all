package controllers

import javax.inject._
import model.FormMappings.UpdateRoleForm
import model.core.Repository
import model.feedback.{Feedback, FeedbackResult}
import model.toolMains._
import model.{FormMappings, Role, User}
import play.api.Logger
import play.api.data.Form
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.libs.json.Json
import play.api.libs.ws.WSClient
import play.api.mvc._
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}
import scala.language.postfixOps

class AdminController @Inject()(cc: ControllerComponents, val dbConfigProvider: DatabaseConfigProvider,
                                val repository: Repository, toolList: ToolList, ws: WSClient)(implicit ec: ExecutionContext)
  extends AbstractController(cc) with HasDatabaseConfigProvider[JdbcProfile] with Secured {

  override protected val adminRightsRequired: Boolean = true

  private val logger = Logger("controllers.AdminController")

  private def getToolMain(toolType: String): Option[AToolMain] = toolList.toolMains.find(_.urlPart == toolType)

  // FIXME: Redirect and flash!
  private def onNoSuchTool(toolType: String): Result = BadRequest(s"There is no such tool with name $toolType")

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
      else FormMappings.updateRoleForm.bindFromRequest().fold(onFormError, onFromValue)
  }

  def evaluationResults: EssentialAction = futureWithUser { user =>
    implicit request =>
      repository.futureEvaluationResultsForTools map { evaluationResultsForTools: Seq[Feedback] =>

        val results: Seq[FeedbackResult] = evaluationResultsForTools.groupBy(_.toolUrlPart) map {
          case (toolUrl, allResults) => FeedbackResult(toolUrl, allResults)
        } toSeq

        Ok(views.html.evaluation.stats(user, results))
      }
  }

  def index: EssentialAction = futureWithUser { admin =>
    implicit request =>
      for {
        numOfUsers <- repository.numOfUsers
        numOfCourses <- repository.numOfCourses
      } yield Ok(views.html.admin.adminIndex(admin, numOfUsers, numOfCourses, toolList))
  }

  def users: EssentialAction = futureWithUser { admin =>
    implicit request => repository.allUsers map (allUsers => Ok(views.html.admin.userOverview(admin, allUsers, toolList)))
  }

  def courses: EssentialAction = futureWithUser { admin =>
    implicit request => repository.allCourses map (allCourses => Ok(views.html.admin.coursesOverview(admin, allCourses, toolList)))
  }

}