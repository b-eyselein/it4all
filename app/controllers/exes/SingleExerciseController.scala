package controllers.exes

import java.nio.file.Files

import controllers.Secured
import model.Enums.ExerciseState
import model.core.{FileUtils, ReadAndSaveResult}
import model.toolMains.{ASingleExerciseToolMain, ToolList}
import play.api.Logger
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, ControllerComponents, EssentialAction}
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

abstract class SingleExerciseController(cc: ControllerComponents, val dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext)
  extends AbstractController(cc) with HasDatabaseConfigProvider[JdbcProfile] with Secured with FileUtils {

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
              val futureReadResult: Future[ReadAndSaveResult] = toolMain.readAndSave(yamlFileContent.mkString)

              futureReadResult map (readAndSaveResult => Ok(views.html.admin.exercisePreview(admin, readAndSaveResult, toolMain)))
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

  def adminChangeExState(tool: String, id: Int): EssentialAction = withAdmin { _ =>
    implicit request =>
      //      val newState: ExerciseState = Option(ExerciseState.valueOf(singleStrForm("state").bindFromRequest.get.str)).getOrElse(ExerciseState.RESERVED)
      //
      //
      //      val updateAction = (for {ex <- tq if ex.id === id} yield ex.state).update(newState)
      //
      //      db.run(updateAction).map {
      //        case 1     => Ok("TODO!")
      //        case other => BadRequest("")
      //      }
      //      completeExById(id).map {
      //        case None           => BadRequest(Json.obj("message" -> "No such file exists..."))
      //        case Some(exercise) =>
      //          exercise.state =
      //          //          //          exercise.save()
      //          //          Ok(Json.obj("id" -> id, "newState" -> exercise.state.toString))
      Ok("TODO")
    //      }
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

  def adminEditExercise(tool: String, id: Int): EssentialAction = withAdmin { _ =>
    implicit request =>
      //    exerciseReader.initFromForm(id, factory.form().bindFromRequest()) match {
      //      case error: ReadingError =>
      //        BadRequest(views.html.jsonReadingError(admin, error))
      //      case _: ReadingFailure => BadRequest("There has been an error...")
      //      case result: ReadingResult[E] =>
      //
      //        result.read.foreach(res => exerciseReader.save(res.read))
      //        Ok(exercisePreview(admin, toolObject, result.read))
      //    }
      Ok("TODO!")
  }

  def adminEditExerciseForm(tool: String, id: Int): EssentialAction = futureWithAdmin { admin =>
    implicit request =>
      ToolList.getFixedExToolOption(tool) match {
        case None           => Future(BadRequest(""))
        case Some(toolMain) => toolMain.renderEditForm(id, admin) map (html => Ok(html))
      }
  }

  def adminExerciseList(tool: String): EssentialAction = futureWithAdmin { admin =>
    implicit request =>
      ToolList.getFixedExToolOption(tool) match {
        case None           => Future(BadRequest(""))
        case Some(toolMain) =>
          toolMain.futureCompleteExes map (exes => Ok(views.html.admin.adminExerciseListView(admin, exes map (_.wrapped), toolMain)))
      }
  }

  def adminNewExerciseForm(tool: String): EssentialAction = futureWithAdmin { admin =>
    implicit request =>
      ToolList.getSingleExerciseToolMainOption(tool) match {
        case None           => Future(BadRequest(s"Tool >>$tool<< not found!"))
        case Some(toolMain) =>
          toolMain.futureHighestId map { id =>
            // FIXME: Save new exercise?
            val newEx = toolMain.instantiateExercise(id + 1, ExerciseState.RESERVED)
            Ok(views.html.admin.exerciseEditForm(admin, toolMain, newEx, toolMain.renderEditRest(newEx), isCreation = true))
          }
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
