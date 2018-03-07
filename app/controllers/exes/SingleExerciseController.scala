package controllers.exes

import java.nio.file.Files

import controllers.Secured
import model.Enums.ExerciseState
import model.core.FileUtils
import model.toolMains.{ASingleExerciseToolMain, ToolList}
import play.api.Logger
import play.api.data.Forms._
import play.api.data.format.Formatter
import play.api.data.{Form, FormError}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, ControllerComponents, EssentialAction, Result}
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

abstract class SingleExerciseController(cc: ControllerComponents, val dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext)
  extends AbstractController(cc) with HasDatabaseConfigProvider[JdbcProfile] with Secured with FileUtils {

  // Helpers

  private implicit object ExerciseStateFormatter extends Formatter[ExerciseState] {

    override def bind(key: String, data: Map[String, String]): Either[Seq[FormError], ExerciseState] = data.get(key) match {
      case None           => Left(Seq(FormError(key, "No value found!")))
      case Some(valueStr) => ExerciseState.byString(valueStr) match {
        case Some(state) => Right(state)
        case None        => Left(Seq(FormError(key, s"Value '$valueStr' is no legal value!")))
      }
    }

    override def unbind(key: String, value: ExerciseState): Map[String, String] = Map(key -> value.name)

  }

  val stateForm: Form[ExerciseState] = Form(single("state" -> of[ExerciseState]))

  // Admin

  def adminIndex(tool: String): EssentialAction = futureWithAdmin { user =>
    implicit request =>
      ToolList.getFixedExToolOption(tool) match {
        case None           => Future(BadRequest(""))
        case Some(toolMain) => toolMain.statistics map (stats => Ok(views.html.admin.exerciseAdminMain(user, stats, toolMain)))
      }
  }

  def adminImportExercises(tool: String): EssentialAction = futureWithAdmin { admin =>
    implicit request =>
      ToolList.getFixedExToolOption(tool) match {
        case None           => Future(BadRequest(""))
        case Some(toolMain) =>
          readAll(toolMain.resourcesFolder / (toolMain.urlPart + ".yaml")) match {
            case Failure(e)               =>
              Logger.error("Import " + toolMain.urlPart + "-Aufgaben:", e)
              Future(BadRequest("Es gab einen Fehler beim Import der Datei: " + e.getMessage))
            case Success(yamlFileContent) =>
              toolMain.readAndSave(yamlFileContent.mkString) map { readAndSaveResult =>

                for (failure <- readAndSaveResult.failures) {
                  Logger.error("There has been an error reading a yaml object: ", failure.exception)
                }

                Ok(views.html.admin.exercisePreview(admin, readAndSaveResult, toolMain))
              }
          }
      }
  }

  def adminExportExercises(tool: String): EssentialAction = futureWithAdmin { admin =>
    implicit request =>
      ToolList.getFixedExToolOption(tool) match {
        case None           => Future(BadRequest(""))
        case Some(toolMain) => toolMain.yamlString map (yaml => Ok(views.html.admin.export(admin, yaml, toolMain)))
      }
  }

  def adminExportExercisesAsFile(tool: String): EssentialAction = futureWithAdmin { _ =>
    implicit request =>
      ToolList.getFixedExToolOption(tool) match {
        case None           => Future(BadRequest(""))
        case Some(toolMain) =>
          toolMain.yamlString map (yaml => {
            val file = Files.createTempFile(s"export_${toolMain.urlPart}", ".yaml")

            write(file, yaml)

            Ok.sendPath(file, fileName = _ => s"export_${toolMain.urlPart}.yaml", onClose = () => Files.delete(file))
          })
      }
  }

  def adminChangeExState(tool: String, id: Int): EssentialAction = futureWithAdmin { _ =>
    implicit request =>

      val onFormError: Form[ExerciseState] => Future[Result] = { formWithErrors =>

        for (formError <- formWithErrors.errors)
          Logger.error(s"Form error while changinge state of exercise $id: ${formError.message}")

        Future(BadRequest("There has been an error!"))
      }

      val onFormRead: ExerciseState => Future[Result] = { newState =>
        ToolList.getFixedExToolOption(tool) match {
          case None           => Future(BadRequest(s"There is no such tool >>$tool<<"))
          case Some(toolMain) => toolMain.updateExerciseState(id, newState) map {
            case true  => Ok(Json.obj("id" -> id, "newState" -> newState.name))
            case false => BadRequest(Json.obj("message" -> "Could not update exercise!"))
          }
        }
      }

      stateForm.bindFromRequest().fold(onFormError, onFormRead)
  }

  def adminExerciseList(tool: String): EssentialAction = futureWithAdmin { admin =>
    implicit request =>
      ToolList.getFixedExToolOption(tool) match {
        case None           => Future(BadRequest(""))
        case Some(toolMain) =>
          toolMain.futureCompleteExes map (exes => Ok(views.html.admin.adminExerciseListView(admin, exes map (_.wrapped), toolMain)))
      }
  }

  def adminDeleteExercise(tool: String, id: Int): EssentialAction = futureWithAdmin { _ =>
    implicit request =>
      ToolList.getFixedExToolOption(tool) match {
        case None           => Future(BadRequest(""))
        case Some(toolMain) =>
          toolMain.delete(id) map {
            case 0 => NotFound(Json.obj("message" -> s"Die Aufgabe mit ID $id existiert nicht und kann daher nicht geloescht werden!"))
            case _ => Ok(Json.obj("id" -> id))
          }
      }
  }

  def adminEditExerciseForm(tool: String, id: Int): EssentialAction = futureWithAdmin { admin =>
    implicit request =>
      ToolList.getSingleExerciseToolMainOption(tool) match {
        case None           => Future(BadRequest(""))
        case Some(toolMain) => toolMain.futureCompleteExById(id) map { maybeExercise =>
          val exercise = maybeExercise getOrElse toolMain.instantiateExercise(id, ExerciseState.RESERVED)
          Ok(toolMain.renderExerciseEditForm(admin, exercise, isCreation = false))
        }
      }
  }

  def adminNewExerciseForm(tool: String): EssentialAction = futureWithAdmin { admin =>
    implicit request =>
      ToolList.getSingleExerciseToolMainOption(tool) match {
        case None           => Future(BadRequest(s"Tool >>$tool<< not found!"))
        case Some(toolMain) => toolMain.futureHighestId map { id =>
          val exercise = toolMain.instantiateExercise(id + 1, ExerciseState.RESERVED)
          Ok(toolMain.renderExerciseEditForm(admin, exercise, isCreation = true))
        }
      }
  }

  def adminEditExercise(tool: String, id: Int): EssentialAction = futureWithAdmin { admin =>
    implicit request =>
      ToolList.getSingleExerciseToolMainOption(tool) match {
        case None           => Future(BadRequest(s"Tool >>$tool<< not found!"))
        case Some(toolMain) =>

          def onFormError: Form[toolMain.CompExType] => Future[Result] = { formWithError =>

            for (formError <- formWithError.errors)
              Logger.error(formError.key + " :: " + formError.message)

            Future(BadRequest("Your form has has errors!"))
          }

          def onFormSuccess: toolMain.CompExType => Future[Result] = { compEx =>
            //FIXME: save ex
            toolMain.futureUpdateExercise(compEx) map { _ =>
              Ok(toolMain.previewExercise(admin, compEx))
            }
          }

          toolMain.readEditFromForm(request).fold(onFormError, onFormSuccess)
      }
  }

  def adminCreateExercise(tool: String): EssentialAction = withAdmin { admin =>
    implicit request => ???
  }

  // User

  def index(tool: String): EssentialAction = exerciseList(tool, page = 1)

  def exerciseList(tool: String, page: Int): EssentialAction = futureWithUser { user =>
    implicit request =>
      ToolList.getSingleExerciseToolMainOption(tool) match {
        case None                                    => Future(Redirect(controllers.routes.Application.index()))
        case Some(toolMain: ASingleExerciseToolMain) => toolMain.dataForUserExesOverview(page) map {
          dataForUserExesOverview => Ok(views.html.core.userExercisesOverview(user, dataForUserExesOverview, toolMain, page))
        }
      }
  }

}
