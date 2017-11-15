package controllers.core

import java.io.PrintWriter
import java.nio.file.{Files, Path, Paths, StandardCopyOption}
import java.sql.SQLSyntaxErrorException

import controllers.Secured
import controllers.core.BaseExerciseController._
import model._
import model.core.CoreConsts._
import model.core._
import model.core.tools.ExToolObject
import net.jcazevedo.moultingyaml._
import play.Logger
import play.api.data.Form
import play.api.data.Forms._
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, ControllerComponents, EssentialAction}
import play.twirl.api.Html
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}
import scala.io.Source
import scala.language.implicitConversions
import scala.util.Try

object BaseExerciseController {

  val STEP = 10
}

abstract class BaseExerciseController[B <: HasBaseValues]
(cc: ControllerComponents, val dbConfigProvider: DatabaseConfigProvider, val repo: Repository, val toolObject: ExToolObject)
(implicit ec: ExecutionContext)
  extends AbstractController(cc) with HasDatabaseConfigProvider[JdbcProfile] with Secured {

  // Reading solution from Request

  type SolutionType <: Solution

  def solForm: Form[SolutionType]

  // Reading Yaml

  type CompEx <: CompleteEx[B]

  implicit val yamlFormat: YamlFormat[CompEx]

  // Database queries

  import profile.api._

  protected type TQ <: repo.HasBaseValuesTable[B]

  protected def tq: TableQuery[TQ]

  protected def numOfExes: Future[Int] = db.run(tq.size.result)

  protected def completeExById(id: Int): Future[Option[CompEx]] = ???

  protected def completeExes: Future[Seq[CompEx]] = ???

  protected def statistics: Future[Html] = numOfExes map (num => Html(s"<li>Es existieren insgesamt $num Aufgaben</li>"))

  // Reading from form

  case class StrForm(str: String)

  def singleStrForm(str: String) = Form(mapping(str -> nonEmptyText)(StrForm.apply)(StrForm.unapply))

  val PROGRESS_LOGGER: Logger.ALogger = Logger.of("progress")

  def saveUploadedFile(savingDir: Path, pathToUploadedFile: Path, saveTo: Path): Try[Path] =
    Try(Files.createDirectories(savingDir))
      .map(_ => Files.move(pathToUploadedFile, saveTo, StandardCopyOption.REPLACE_EXISTING))

  def log(user: User, eventToLog: WorkingEvent): Unit = Unit // PROGRESS_LOGGER.debug(s"""${user.languageName} - ${Json.toJson(eventToLog)}""")

  // Admin

  //  override def exercisesRoute: Call = controllers.programming.routes.ProgController.exercises()
  //
  //  override def newExFormRoute: Call = controllers.programming.routes.ProgController.newExerciseForm()
  //
  //
  //  override def uploadFileRoute: Call = controllers.programming.routes.ProgController.uploadFile()

  def adminIndex: EssentialAction = futureWithAdmin { user =>
    implicit request => statistics map (stats => Ok(views.html.admin.adminMain.render(user, stats, toolObject, new Html(""))))
  }


  def importExercises: EssentialAction = futureWithAdmin { admin =>
    implicit request =>
      val file = Paths.get("conf", "resources", toolObject.exType + ".yaml").toFile
      val read = Source.fromFile(file).mkString.parseYamls map (_.convertTo[CompEx])
      saveRead(read) map (_ => Ok(views.html.admin.preview.render(admin, read, toolObject))) recover {
        // FIXME: Failures!
        case sqlError: SQLSyntaxErrorException =>
          sqlError.printStackTrace
          BadRequest(sqlError.getMessage)
        case throwable                         =>
          println("\nERROR: ")
          throwable.printStackTrace()
          BadRequest(throwable.getMessage)
      }
  }

  protected def saveRead(read: Seq[CompEx]): Future[Seq[Int]] = ???

  def exportExercises: EssentialAction = futureWithAdmin { admin =>
    implicit request =>
      // TODO: scalarStyle = Folded if fixed...
      completeExes map (exes => Ok(views.html.admin.export.render(admin, exes map (_.toYaml print Auto) mkString "---\n", toolObject)))
  }

  def exportExercisesAsFile: EssentialAction = futureWithAdmin { admin =>
    implicit request =>
      val file = Files.createTempFile(s"export_${toolObject.exType}", ".yaml")

      completeExes map (exes => {

        val writer = new PrintWriter(file.toFile)
        writer.write(exes map (_.toYaml.prettyPrint) mkString "---\n")
        writer.close()

        Ok.sendPath(file, fileName = _ => s"export_${toolObject.exType}.yaml", onClose = () => Files.delete(file))
      })
  }


  protected val savingDir: Path = Paths.get(toolObject.rootDir, ADMIN_FOLDER, toolObject.exType)

  def changeExState(id: Int): EssentialAction = withAdmin { _ =>
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

  def deleteExercise(id: Int): EssentialAction = futureWithAdmin { _ =>
    implicit request =>
      db.run(tq.filter(_.id === id).delete) map {
        case 0 => NotFound(Json.obj("message" -> s"Die Aufgabe mit ID $id existiert nicht und kann daher nicht geloescht werden!"))
        case 1 => Ok(Json.obj("id" -> id))
        case _ => BadRequest(Json.obj("message" -> s"Es gab einen internen Fehler beim Loeschen der Aufgabe mit der ID $id"))
      }
  }

  def editExercise(id: Int): EssentialAction = withAdmin { _ =>
    implicit request =>
      //    exerciseReader.initFromForm(id, factory.form().bindFromRequest()) match {
      //      case error: ReadingError =>
      //        BadRequest(views.html.jsonReadingError.render(admin, error))
      //      case _: ReadingFailure => BadRequest("There has been an error...")
      //      case result: ReadingResult[E] =>
      //
      //        result.read.foreach(res => exerciseReader.save(res.read))
      //        Ok(views.html.admin.preview.render(admin, toolObject, result.read))
      //    }
      Ok("TODO!")
  }

  def editExerciseForm(id: Int): EssentialAction = futureWithAdmin { admin =>
    implicit request => completeExById(id) map (ex => Ok(views.html.admin.editExForm(admin, toolObject, ex, renderEditRest(ex))))
  }

  def exercises: EssentialAction = futureWithAdmin { admin =>
    implicit request => completeExes map (exes => Ok(views.html.admin.exerciseList.render(admin, exes, toolObject)))
  }

  // FIXME: make abstract...
  protected def renderExListRest(b: B): Html = new Html("")

  def newExerciseForm: EssentialAction = withAdmin { admin =>
    // FIXME
    implicit request =>
      // FIXME: ID of new exercise?
      Ok(views.html.admin.editExForm.render(admin, toolObject, None, renderEditRest(None)))
  }

  def newExercise: EssentialAction = withAdmin { admin =>
    implicit request =>
      Ok("TODO: Not yet used...")
  }

  // User

  def index(page: Int): EssentialAction = futureWithUser { user =>
    implicit request =>
      completeExes map (allExes => {
        val exes = allExes slice(Math.max(0, (page - 1) * STEP), Math.min(page * STEP, allExes.size))
        Ok(renderExes(user, exes, allExes.size))
      })
  }

  // FIXME: refactor...
  protected def renderExes(user: User, exes: Seq[CompEx], allExesSize: Int): Html =
    views.html.core.exesList.render(user, exes, renderExesListRest, toolObject, allExesSize / STEP + 1)

  protected def renderExesListRest: Html = new Html("")

  // Helper methods

  def renderEditRest(exercise: Option[CompEx]): Html = new Html("")

  def renderExercises(exercises: List[CompEx]): Html = new Html("") //views.html.admin.exercisesTable.render(exercises, toolObject)

  //  def renderExesCreated(admin: User, exercises: List[B]): Html = views.html.admin.preview.render(admin, exercises, toolObject)

}