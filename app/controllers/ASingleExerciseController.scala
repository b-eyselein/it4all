package controllers

import java.nio.file.Files

import model.Enums.ExerciseState
import model.User
import model.toolMains.ASingleExerciseToolMain
import play.api.Logger
import play.api.data.Form
import play.api.data.Forms.{of, single}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.libs.json.Json
import play.api.mvc._
import play.twirl.api.Html
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

abstract class ASingleExerciseController(cc: ControllerComponents, dbcp: DatabaseConfigProvider)(implicit ec: ExecutionContext)
  extends AFixedExController(cc, dbcp) with HasDatabaseConfigProvider[JdbcProfile] {

  override type ToolMainType <: ASingleExerciseToolMain

  // Helpers

  private val stateForm: Form[ExerciseState] = Form(single("state" -> of[ExerciseState]))

  // Admin

  def adminIndex(toolType: String): EssentialAction = futureWithAdminWithToolMain(toolType) { (admin, toolMain) =>
    implicit request => toolMain.statistics map (stats => Ok(adminIndexView(admin, stats, toolMain)))
  }

  def adminExportExercises(toolType: String): EssentialAction = futureWithAdminWithToolMain(toolType) { (admin, toolMain) =>
    implicit request => toolMain.yamlString map (yaml => Ok(views.html.admin.export(admin, yaml, toolMain)))
  }

  def adminExportExercisesAsFile(toolType: String): EssentialAction = futureWithAdminWithToolMain(toolType) { (_, toolMain) =>
    implicit request =>
      toolMain.yamlString map { yaml =>
        val file = Files.createTempFile(s"export_${toolMain.urlPart}", ".yaml")

        write(file, yaml)

        Ok.sendPath(file, fileName = _ => s"export_${toolMain.urlPart}.yaml", onClose = () => Files.delete(file))
      }
  }

  def adminChangeExState(toolType: String, id: Int): EssentialAction = futureWithAdminWithToolMain(toolType) { (_, toolMain) =>
    implicit request =>

      val onFormError: Form[ExerciseState] => Future[Result] = { formWithErrors =>

        for (formError <- formWithErrors.errors)
          Logger.error(s"Form error while changinge state of exercise $id: ${formError.message}")

        Future(BadRequest("There has been an error!"))
      }

      def onFormRead(toolMain: ASingleExerciseToolMain): ExerciseState => Future[Result] = { newState =>
        toolMain.updateExerciseState(id, newState) map {
          case true  => Ok(Json.obj("id" -> id, "newState" -> newState.name))
          case false => BadRequest(Json.obj("message" -> "Could not update exercise!"))
        }
      }

      stateForm.bindFromRequest().fold(onFormError, onFormRead(toolMain))
  }

  def adminExerciseList(toolType: String): EssentialAction = futureWithAdminWithToolMain(toolType) { (admin, toolMain) =>
    implicit request => toolMain.futureCompleteExes map (exes => Ok(toolMain.adminExerciseList(admin, exes)))
  }

  def adminDeleteExercise(toolType: String, id: Int): EssentialAction = futureWithAdminWithToolMain(toolType) { (_, toolMain) =>
    implicit request =>
      toolMain.futureDeleteExercise(id) map {
        case 0 => NotFound(Json.obj("message" -> s"Die Aufgabe mit ID $id existiert nicht und kann daher nicht geloescht werden!"))
        case _ => Ok(Json.obj("id" -> id))
      }
  }

  def adminEditExerciseForm(toolType: String, id: Int): EssentialAction = futureWithAdminWithToolMain(toolType) { (admin, toolMain) =>
    implicit request =>
      toolMain.futureCompleteExById(id) map { maybeExercise =>
        val exercise = maybeExercise getOrElse toolMain.instantiateExercise(id, ExerciseState.RESERVED)
        Ok(toolMain.renderExerciseEditForm(admin, exercise, isCreation = false))
      }
  }

  def adminNewExerciseForm(toolType: String): EssentialAction = futureWithAdminWithToolMain(toolType) { (admin, toolMain) =>
    implicit request =>
      toolMain.futureHighestId map { id =>
        val exercise = toolMain.instantiateExercise(id + 1, ExerciseState.RESERVED)
        Ok(toolMain.renderExerciseEditForm(admin, exercise, isCreation = true))
      }
  }

  def adminEditExercise(toolType: String, id: Int): EssentialAction = futureWithAdminWithToolMain(toolType) { (admin, toolMain) =>
    implicit request =>

      def onFormError: Form[toolMain.ExType] => Future[Result] = { formWithError =>

        for (formError <- formWithError.errors)
          Logger.error(formError.key + " :: " + formError.message)

        Future(BadRequest("Your form has has errors!"))
      }

      def onFormSuccess: toolMain.ExType => Future[Result] = { compEx =>
        //FIXME: save ex
        toolMain.futureUpdateExercise(compEx) map { _ =>
          Ok(views.html.admin.singleExercisePreview(admin, compEx, toolMain))
        }
      }

      toolMain.readEditFromForm(request).fold(onFormError, onFormSuccess)
  }

  def adminCreateExercise(toolType: String): EssentialAction = futureWithAdminWithToolMain(toolType) { (_, _) =>
    implicit request => ???
  }

  // Admin views

  protected def adminIndexView(admin: User, stats: Html, toolMain: ToolMainType): Html

  // User

//  def index(toolType: String): EssentialAction = exerciseList(toolType, page = 1)

  def exerciseList(toolType: String, page: Int): EssentialAction = futureWithUserWithToolMain(toolType) { (user, toolMain) =>
    implicit request =>
      toolMain.dataForUserExesOverview(page) map {
        dataForUserExesOverview => Ok(views.html.core.userExercisesOverview(user, dataForUserExesOverview, toolMain, page))
      }
  }

}
