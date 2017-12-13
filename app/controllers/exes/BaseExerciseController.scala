package controllers.exes

import java.nio.file.{Files, Paths}
import java.sql.SQLSyntaxErrorException

import controllers.Secured
import model.core.CoreConsts._
import model.core._
import model.core.tools.ExToolObject
import model.{CompleteEx, Exercise, User}
import net.jcazevedo.moultingyaml._
import play.Logger
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.libs.json.Json
import play.api.mvc._
import play.twirl.api.Html
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}
import scala.io.Source
import scala.language.implicitConversions

abstract class BaseExerciseController[Ex <: Exercise]
(cc: ControllerComponents, val dbConfigProvider: DatabaseConfigProvider, val repo: Repository, val toolObject: ExToolObject)(implicit ec: ExecutionContext)
  extends AbstractController(cc) with HasDatabaseConfigProvider[JdbcProfile] with Secured with FileUtils {

  // Reading solution from requests

  type SolType

  def readSolutionFromPostRequest(implicit request: Request[AnyContent]): Option[SolType]

  def readSolutionFromPutRequest(implicit request: Request[AnyContent]): Option[SolType]

  //  case class StrForm(str: String)

  //  def singleStrForm(str: String) = Form(mapping(str -> nonEmptyText)(StrForm.apply)(StrForm.unapply))

  // Reading Yaml

  type CompEx <: CompleteEx[Ex]

  implicit val yamlFormat: YamlFormat[CompEx]

  // Database queries

  import profile.api._

  protected type TQ <: repo.HasBaseValuesTable[Ex]

  protected def tq: TableQuery[TQ]

  protected def numOfExes: Future[Int] = db.run(tq.size.result)

  protected def futureCompleteExById(id: Int): Future[Option[CompEx]] = ???

  protected def futureCompleteExes: Future[Seq[CompEx]] = ???

  protected def statistics: Future[Html] = numOfExes map (num => Html(s"<li>Es existieren insgesamt $num Aufgaben</li>"))

  protected def saveRead(read: Seq[CompEx]): Future[Seq[Any]] = ???

  val PROGRESS_LOGGER: Logger.ALogger = Logger.of("progress")

  def log(user: User, eventToLog: WorkingEvent): Unit = Unit // PROGRESS_LOGGER.debug(s"""${user.username} - ${Json.toJson(eventToLog)}""")

  // Admin

  def adminIndex: EssentialAction = futureWithAdmin { user =>
    implicit request => statistics map (stats => Ok(views.html.admin.exerciseAdminMain(user, stats, toolObject)))
  }

  def adminImportExercises: EssentialAction = futureWithAdmin { admin =>
    implicit request =>
      val file = Paths.get("conf", "resources", toolObject.exType + ".yaml").toFile
      val read = Source.fromFile(file).mkString.parseYamls map (_.convertTo[CompEx])
      saveAndPreviewExercises(admin, read)
  }

  /**
    * FIXME: save and import in same method? => in direct subclasses!?!
    *
    * @param admin current user, has to be admin
    * @param read  Seq of read (exercises)
    * @return
    */
  def saveAndPreviewExercises(admin: User, read: Seq[CompEx]): Future[Result] =
    saveRead(read) map (_ => Ok(previewExercises(admin, read))) recover {
      // FIXME: Failures!
      case sqlError: SQLSyntaxErrorException =>
        sqlError.printStackTrace()
        BadRequest(sqlError.getMessage)
      case throwable                         =>
        println("\nERROR: ")
        throwable.printStackTrace()
        BadRequest(throwable.getMessage)
    }

  def adminExportExercises: EssentialAction = futureWithAdmin { admin =>
    implicit request =>
      futureCompleteExes map (exes => Ok(views.html.admin.export(admin, yamlString(exes), toolObject)))
  }

  def adminExportExercisesAsFile: EssentialAction = futureWithAdmin { admin =>
    implicit request =>
      val file = Files.createTempFile(s"export_${toolObject.exType}", ".yaml")
      futureCompleteExes map (exes => {

        write(file, yamlString(exes))

        Ok.sendPath(file, fileName = _ => s"export_${toolObject.exType}.yaml", onClose = () => Files.delete(file))
      })
  }

  def adminChangeExState(id: Int): EssentialAction = withAdmin { _ =>
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

  def adminDeleteExercise(id: Int): EssentialAction = futureWithAdmin { _ =>
    implicit request =>
      db.run(tq.filter(_.id === id).delete) map {
        case 0 => NotFound(Json.obj("message" -> s"Die Aufgabe mit ID $id existiert nicht und kann daher nicht geloescht werden!"))
        case _ => Ok(Json.obj("id" -> id))
      }
  }

  def adminEditExercise(id: Int): EssentialAction = withAdmin { _ =>
    implicit request =>
      //    exerciseReader.initFromForm(id, factory.form().bindFromRequest()) match {
      //      case error: ReadingError =>
      //        BadRequest(views.html.jsonReadingError(admin, error))
      //      case _: ReadingFailure => BadRequest("There has been an error...")
      //      case result: ReadingResult[E] =>
      //
      //        result.read.foreach(res => exerciseReader.save(res.read))
      //        Ok(views.html.admin.exercisePreview(admin, toolObject, result.read))
      //    }
      Ok("TODO!")
  }

  def adminEditExerciseForm(id: Int): EssentialAction = futureWithAdmin { admin =>
    implicit request => futureCompleteExById(id) map (ex => Ok(views.html.admin.exerciseEditForm(admin, toolObject, ex, renderEditRest(ex))))
  }

  def adminExerciseList: EssentialAction = futureWithAdmin { admin =>
    implicit request => futureCompleteExes map (exes => Ok(views.html.admin.exerciseList(admin, exes, toolObject)))
  }

  def adminNewExerciseForm: EssentialAction = withAdmin { admin =>
    // FIXME
    implicit request =>
      // FIXME: ID of new exercise?
      Ok(views.html.admin.exerciseEditForm(admin, toolObject, None, renderEditRest(None)))
  }

  def adminCreateExercise: EssentialAction = withAdmin { admin =>
    implicit request => ???
  }

  // Views and other helper methods for admin

  protected def previewExercises(admin: User, read: Seq[CompEx]): Html = views.html.admin.exercisePreview(admin, read, toolObject)

  // FIXME: scalarStyle = Folded if fixed...
  private def yamlString(exes: Seq[CompEx]): String = "%YAML 1.2\n---\n" + (exes map (_.toYaml.print(Auto /*, Folded*/)) mkString "---\n")

  // User

  def index: EssentialAction = exerciseList(page = 1)

  def exerciseList(page: Int): EssentialAction = futureWithUser { user =>
    implicit request =>
      futureCompleteExes map (allExes => {
        val exes = allExes slice(Math.max(0, (page - 1) * STEP), Math.min(page * STEP, allExes.size))
        Ok(renderExes(user, exes, allExes.size))
      })
  }

  // FIXME: refactor...
  protected def renderExes(user: User, exes: Seq[CompEx], allExesSize: Int): Html =
    views.html.core.exesList(user, exes, renderExesListRest, toolObject, allExesSize / STEP + 1)


  /**
    * Used for rendering things such as playgrounds
    *
    * @return Html - with link to other "exercises"
    */
  protected def renderExesListRest: Html = new Html("")

  // Helper methods

  protected def renderEditRest(exercise: Option[CompEx]): Html = ???

}