package controllers

import javax.inject._
import model.FormMappings.UpdateRoleForm
import model.core.Repository
import model.feedback.{Feedback, FeedbackResult}
import model.hubapi.{HubJsonProtocol, HubTool}
import model.toolMains._
import model.{FormMappings, Role, User}
import play.api.Logger
import play.api.data.Form
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.libs.json.{JsArray, Json}
import play.api.libs.ws.WSClient
import play.api.mvc._
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}
import scala.language.postfixOps
import scala.util.{Failure, Success, Try}

class AdminController @Inject()(cc: ControllerComponents, val dbConfigProvider: DatabaseConfigProvider,
                                val repository: Repository, toolList: ToolList, ws: WSClient)(implicit ec: ExecutionContext)
  extends AbstractController(cc) with HasDatabaseConfigProvider[JdbcProfile] with Secured {

  private def getToolMain(toolType: String): Option[AToolMain] = toolList.toolMains.find(_.urlPart == toolType)

  // FIXME: Redirect and flash!
  private def onNoSuchTool(toolType: String): Result = BadRequest(s"There is no such tool with name $toolType")

  protected def futureWithAdminWithToolMain(toolType: String)(f: (User, AToolMain) => Request[AnyContent] => Future[Result]): EssentialAction = futureWithAdmin { admin =>
    implicit request =>
      getToolMain(toolType) match {
        case None           => Future(onNoSuchTool(toolType))
        case Some(toolMain) => f(admin, toolMain)(request)
      }
  }

  private val hubUrl = "http://localhost:5555"

  def changeRole: EssentialAction = futureWithAdmin { admin =>
    implicit request =>

      val onFormError: Form[UpdateRoleForm] => Future[Result] = { formWithErrors =>

        for (formError <- formWithErrors.errors)
          Logger.error(formError.message)

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

  def evaluationResults: EssentialAction = futureWithAdmin { user =>
    implicit request =>
      repository.futureEvaluationResultsForTools map { evaluationResultsForTools: Seq[Feedback] =>

        val results: Seq[FeedbackResult] = evaluationResultsForTools.groupBy(_.toolUrlPart) map {
          case (toolUrl, allResults) => FeedbackResult(toolUrl, allResults)
        } toSeq

        Ok(views.html.evaluation.stats(user, results))
      }
  }

  def synchronize: EssentialAction = futureWithAdmin { admin =>
    implicit request =>

      println(toolList.toolMains)

      // FIXME: current work...

      ws.url(hubUrl).withHttpHeaders("Accept" -> "application/json").get map { response =>
        Try(Json.parse(response.body)) match {
          case Failure(error) =>
            Logger.error("Could not parse request as json", error)
            BadRequest("Es gab einen Fehler bei der Bearbeitung des Requests!")

          case Success(JsArray(contents)) =>
            val toolsInHub: IndexedSeq[HubTool] = contents.flatMap(content => HubJsonProtocol.hubToolFormat.reads(content) asOpt)

            val (syncableTools, notSyncableTools) = toolList.toolMains.partition(toolMain => toolsInHub.map(_.toolId) contains toolMain.urlPart)


            Ok(views.html.admin.hub.hubTools(admin, syncableTools, notSyncableTools))


          case Success(_) =>
            BadRequest("Es gab einen Fehler bei der Bearbeitung des Requests!")
        }
      } recover {
        case _: Throwable => Redirect(routes.AdminController.index()).flashing("hub" -> "Hub ist nicht erreichbar!")
      }

  }

  def synchronizeTool(toolId: String): EssentialAction = futureWithAdminWithToolMain(toolId) { (admin, toolMain) =>
    implicit request =>
      toolMain match {
        case setm: ASingleExerciseToolMain =>
          setm.futureCompleteExes map {
            exercises => Ok(views.html.admin.hub.syncExToolMain(admin, setm, exercises))
          }
        case ctm: CollectionToolMain       =>
          ctm.futureCompleteColls map {
            collections => Ok(views.html.admin.hub.syncCollToolMain(admin, ctm, collections))
          }
      }
  }

  def index: EssentialAction = futureWithAdmin { admin =>
    implicit request =>
      for {
        numOfUsers <- repository.numOfUsers
        numOfCourses <- repository.numOfCourses
      } yield Ok(views.html.admin.adminIndex(admin, numOfUsers, numOfCourses, toolList))
  }

  def users: EssentialAction = futureWithAdmin { admin =>
    implicit request => repository.allUsers map (allUsers => Ok(views.html.admin.userOverview(admin, allUsers, toolList)))
  }

  def courses: EssentialAction = futureWithAdmin { admin =>
    implicit request => repository.allCourses map (allCourses => Ok(views.html.admin.coursesOverview(admin, allCourses, toolList)))
  }

}