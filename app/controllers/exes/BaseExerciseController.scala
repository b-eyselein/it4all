package controllers.exes

import java.nio.file.Files

import controllers.Secured
import controllers.exes.fileExes.AFileExToolMain
import controllers.exes.idPartExes.AIdPartExToolMain
import model._
import model.core.CoreConsts._
import model.core._
import play.Logger
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.libs.json.Json
import play.api.mvc._
import play.twirl.api.Html
import slick.jdbc.JdbcProfile

import scala.collection.mutable
import scala.concurrent.{ExecutionContext, Future}
import scala.language.{implicitConversions, postfixOps}
import scala.util.{Failure, Success}

object BaseExerciseController {

  val ToolMains: mutable.Map[String, AToolMain[_ <: Exercise, _ <: CompleteEx[_ <: Exercise]]] = mutable.Map.empty

  val FileToolMains: mutable.Map[String, AFileExToolMain[_ <: Exercise, _ <: FileCompleteEx[_ <: Exercise]]] = mutable.Map.empty

  val IdPartExToolMains: mutable.Map[String, AIdPartExToolMain[_, _, _ <: Exercise, _ <: CompleteEx[_ <: Exercise]]] = mutable.Map.empty
}

// FIXME: move class ExerciseOptions to better class...
case class ExerciseOptions(tool: String, aceMode: String, minLines: Int, maxLines: Int, updatePrev: Boolean)

abstract class BaseExerciseController(cc: ControllerComponents, val dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext)
  extends AbstractController(cc) with HasDatabaseConfigProvider[JdbcProfile] with Secured with FileUtils {

  val PROGRESS_LOGGER: Logger.ALogger = Logger.of("progress")

  def log(user: User, eventToLog: WorkingEvent): Unit = Unit // PROGRESS_LOGGER.debug(s"""${user.username} - ${Json.toJson(eventToLog)}""")

  // Admin

  def adminIndex(tool: String): EssentialAction = futureWithAdmin { user =>
    implicit request =>
      val toolMain = BaseExerciseController.ToolMains(tool)
      toolMain.statistics map (stats => Ok(views.html.admin.exerciseAdminMain(user, stats, toolMain)))
  }

  def adminImportExercises(tool: String): EssentialAction = futureWithAdmin { admin =>
    implicit request =>
      val toolMain = BaseExerciseController.ToolMains(tool)
      readAll(toolMain.resourcesFolder / (toolMain.exType + ".yaml")) match {
        case Failure(e)               =>
          Logger.error("Import " + toolMain.exType + "-Aufgaben:", e)
          Future(BadRequest("Es gab einen Fehler beim Import der Datei: " + e.getMessage))
        case Success(yamlFileContent) =>
          val (successes, save) = toolMain.readAndSave(yamlFileContent.mkString)
          save map (_ => Ok(views.html.admin.exercisePreview(admin, successes, toolMain)))
      }
  }

  def adminExportExercises(tool: String): EssentialAction = futureWithAdmin { admin =>
    implicit request =>
      val toolMain = BaseExerciseController.ToolMains(tool)
      BaseExerciseController.ToolMains(tool).yamlString map (yaml => Ok(views.html.admin.export(admin, yaml, toolMain)))
  }

  def adminExportExercisesAsFile(tool: String): EssentialAction = futureWithAdmin { _ =>
    implicit request =>
      val toolMain = BaseExerciseController.ToolMains(tool)
      toolMain.yamlString map (yaml => {
        val file = Files.createTempFile(s"export_${toolMain.exType}", ".yaml")

        write(file, yaml)

        Ok.sendPath(file, fileName = _ => s"export_${toolMain.exType}.yaml", onClose = () => Files.delete(file))
      })
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
      val toolMain = BaseExerciseController.ToolMains(tool)
      toolMain.delete(id) map {
        case 0 => NotFound(Json.obj("message" -> s"Die Aufgabe mit ID $id existiert nicht und kann daher nicht geloescht werden!"))
        case _ => Ok(Json.obj("id" -> id))
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
    implicit request => BaseExerciseController.ToolMains(tool).renderEditForm(id, admin) map (html => Ok(html))
  }

  def adminExerciseList(tool: String): EssentialAction = futureWithAdmin { admin =>
    implicit request =>
      val toolMain = BaseExerciseController.ToolMains(tool)
      toolMain.futureCompleteExes map (exes => Ok(views.html.admin.exerciseListView(admin, exes, toolMain)))
  }

  def adminNewExerciseForm(tool: String): EssentialAction = withAdmin { admin =>
    // FIXME
    implicit request =>
      val toolMain = BaseExerciseController.ToolMains(tool)
      // FIXME: ID of new exercise?
      Ok(views.html.admin.exerciseEditForm(admin, toolMain, None, toolMain.renderEditRest(None)))
  }

  def adminCreateExercise(tool: String): EssentialAction = withAdmin { admin =>
    implicit request => ???
  }

  // User

  def index(tool: String): EssentialAction = exerciseList(tool, page = 1)

  def exerciseList(tool: String, page: Int): EssentialAction = futureWithUser { user =>
    implicit request =>
      val toolMain = BaseExerciseController.ToolMains(tool)
      toolMain.futureCompleteExes map (allExes => {
        val exes = allExes slice(Math.max(0, (page - 1) * STEP), Math.min(page * STEP, allExes.size))
        Ok(views.html.core.exesList(user, exes, renderExesListRest, toolMain, allExes.size / STEP + 1))
      })
  }

  /**
    * Used for rendering things such as playgrounds
    *
    * @return Html - with link to other "exercises"
    */
  protected def renderExesListRest: Html = new Html("")

}