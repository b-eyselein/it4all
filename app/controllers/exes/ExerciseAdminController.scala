package controllers.exes


import java.nio.file.Files

import better.files.File._
import controllers.{AFixedExController, Secured}
import javax.inject.{Inject, Singleton}
import model.ExerciseState
import model.core._
import model.programming.ProgToolMain
import model.toolMains.{ASingleExerciseToolMain, ToolList}
import model.uml._
import model.web.WebToolMain
import play.api.Logger
import play.api.data.Form
import play.api.data.Forms.single
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.json.Json
import play.api.libs.ws._
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Failure

@Singleton
class ExerciseAdminController @Inject()(cc: ControllerComponents, dbcp: DatabaseConfigProvider, val repository: Repository, ws: WSClient, tl: ToolList,
                                        progToolMain: ProgToolMain, umlToolMain: UmlToolMain, webToolMain: WebToolMain)
                                       (implicit ec: ExecutionContext) extends AFixedExController(cc, dbcp, tl) with Secured with play.api.i18n.I18nSupport {

  // FIXME: old class ASingleExerciseController

  override protected type ToolMainType = ASingleExerciseToolMain

  override protected val adminRightsRequired: Boolean = true


  // Helpers

  override protected def getToolMain(toolType: String): Option[ASingleExerciseToolMain] = toolList.getSingleExerciseToolMainOption(toolType)

  private val stateForm: Form[ExerciseState] = Form(single("state" -> ExerciseState.formField))

  // Admin

  def adminImportExercises(toolType: String): EssentialAction = futureWithAdminWithToolMain(toolType) { (admin, toolMain) =>
    implicit request =>
      // FIXME: refactor!!!!!!!!!

      val (readSuccesses: Seq[toolMain.ExType], readFailures: Seq[Failure[toolMain.ExType]]) = CommonUtils.splitTriesNew(toolMain.readImports)

      toolMain.futureSaveRead(readSuccesses) map { saveResults: Seq[(toolMain.ExType, Boolean)] =>
        val readAndSaveResult = ReadAndSaveResult(saveResults map (sr => new ReadAndSaveSuccess[toolMain.ExType](sr._1, sr._2)), readFailures)

        for (failure <- readAndSaveResult.failures) {
          Logger.error("There has been an error reading a yaml object: ", failure.exception)
        }

        Ok(toolMain.previewReadAndSaveResult(admin, readAndSaveResult, toolList))
      }
  }


  def adminExportExercises(toolType: String): EssentialAction = futureWithAdminWithToolMain(toolType) { (admin, toolMain) =>
    implicit request => toolMain.yamlString map (yaml => Ok(views.html.admin.export(admin, yaml, toolMain, toolList)))
  }

  def adminExportExercisesAsFile(toolType: String): EssentialAction = futureWithAdminWithToolMain(toolType) { (_, toolMain) =>
    implicit request =>
      toolMain.yamlString map { yaml =>
        val file = Files.createTempFile(s"export_${toolMain.urlPart}", ".yaml")

        file.write(yaml)

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

      val onFormRead: ExerciseState => Future[Result] = { newState =>
        toolMain.updateExerciseState(id, newState) map {
          case true  => Ok(Json.obj("id" -> id, "newState" -> newState.entryName))
          case false => BadRequest(Json.obj("message" -> "Could not update exercise!"))
        }
      }

      stateForm.bindFromRequest().fold(onFormError, onFormRead)
  }

  def adminExerciseList(toolType: String): EssentialAction = futureWithAdminWithToolMain(toolType) { (admin, toolMain) =>
    implicit request => toolMain.futureAllExercises map (exes => Ok(toolMain.adminExerciseList(admin, exes, toolList)))
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
      toolMain.futureExerciseById(id) map {
        case None           => onNoSuchExercise(id)
        case Some(exercise) => Ok(toolMain.renderAdminExerciseEditForm(admin, exercise, isCreation = false, toolList))
      }
  }

  def adminNewExerciseForm(toolType: String): EssentialAction = futureWithAdminWithToolMain(toolType) { (admin, toolMain) =>
    implicit request =>
      toolMain.futureHighestId map { id =>
        val exercise = toolMain.instantiateExercise(id + 1, admin.username, ExerciseState.RESERVED)
        Ok(toolMain.renderAdminExerciseEditForm(admin, exercise, isCreation = true, toolList))
      }
  }

  def adminEditExercise(toolType: String, id: Int): EssentialAction = futureWithAdminWithToolMain(toolType) { (admin, toolMain) =>
    implicit request =>

      //      def onFormError: Form[toolMain.ExType] => Future[Result] = { formWithError =>
      //
      //        for (formError <- formWithError.errors)
      //          Logger.error(formError.key + " :: " + formError.message)
      //
      //        Future(BadRequest("Your form has has errors!"))
      //      }

      //      def onFormSuccess: toolMain.ExType => Future[Result] = { compEx =>
      // //        FIXME: save ex
      //        toolMain.futureUpdateExercise(compEx) map { _ =>
      //          Ok(views.html.admin.singleExercisePreview(admin, compEx, toolMain))
      //        }
      //      }

      //      toolMain.compExForm.bindFromRequest().fold(onFormError, onFormSuccess)
      ???
  }

  def adminCreateExercise(toolType: String): EssentialAction = futureWithAdminWithToolMain(toolType) { (_, _) =>
    implicit request => ???
  }

}
