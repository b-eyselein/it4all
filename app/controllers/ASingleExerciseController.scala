package controllers

import java.nio.file.Files

import model.ExerciseState
import model.core.FileUtils
import model.toolMains.{ASingleExerciseToolMain, ToolList}
import play.api.Logger
import play.api.data.Form
import play.api.data.Forms.single
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.libs.json.Json
import play.api.mvc._
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

abstract class ASingleExerciseController(cc: ControllerComponents, dbcp: DatabaseConfigProvider, tl: ToolList)(implicit ec: ExecutionContext)
  extends AFixedExController(cc, dbcp, tl) with HasDatabaseConfigProvider[JdbcProfile] with FileUtils {

  override type ToolMainType <: ASingleExerciseToolMain

  // Helpers

  private val stateForm: Form[ExerciseState] = Form(single("state" -> ExerciseState.formField))

  // Admin

  def adminExportExercises(toolType: String): EssentialAction = futureWithAdminWithToolMain(toolType) { (admin, toolMain) =>
    implicit request => toolMain.yamlString map (yaml => Ok(views.html.admin.export(admin, yaml, toolMain, toolList)))
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
          case true  => Ok(Json.obj("id" -> id, "newState" -> newState.entryName))
          case false => BadRequest(Json.obj("message" -> "Could not update exercise!"))
        }
      }

      stateForm.bindFromRequest().fold(onFormError, onFormRead(toolMain))
  }

  def adminExerciseList(toolType: String): EssentialAction = futureWithAdminWithToolMain(toolType) { (admin, toolMain) =>
    implicit request => toolMain.futureCompleteExes map (exes => Ok(toolMain.adminExerciseList(admin, exes, toolList)))
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
        Ok(toolMain.renderExerciseEditForm(admin, exercise, isCreation = false, toolList))
      }
  }

  def adminNewExerciseForm(toolType: String): EssentialAction = futureWithAdminWithToolMain(toolType) { (admin, toolMain) =>
    implicit request =>
      toolMain.futureHighestId map { id =>
        val exercise = toolMain.instantiateExercise(id + 1, ExerciseState.RESERVED)
        Ok(toolMain.renderExerciseEditForm(admin, exercise, isCreation = true, toolList))
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

  // Views

  def exerciseList(toolType: String, page: Int): EssentialAction = futureWithUserWithToolMain(toolType) { (user, toolMain) =>
    implicit request =>
      toolMain.dataForUserExesOverview(user, page) map {
        dataForUserExesOverview => Ok(views.html.exercises.userExercisesOverview(user, dataForUserExesOverview, toolMain, page))
      }
  }

}
