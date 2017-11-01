package controllers.core

import java.nio.file.{Files, Path, Paths, StandardCopyOption}

import model.User
import model.core._
import model.core.tools.ExToolObject
import play.Logger
import play.api.data.Form
import play.api.data.Forms._
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.libs.json.{Json, Reads}
import play.api.mvc.{AbstractController, ControllerComponents, EssentialAction}
import play.twirl.api.Html
import slick.jdbc.JdbcProfile

import scala.collection.JavaConverters._
import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

object BaseExerciseController {
  val STEP = 10
}

abstract class BaseExerciseController[B <: HasBaseValues]
(cc: ControllerComponents, val dbConfigProvider: DatabaseConfigProvider, val repo: Repository, val toolObject: ExToolObject)(implicit ec: ExecutionContext)
  extends AbstractController(cc) with HasDatabaseConfigProvider[JdbcProfile] with Secured {

  val jsonFile: Path = Paths.get(toolObject.resourcesFolder.toString, "exercises.json")

  implicit def reads: Reads[B]

  import profile.api._

  type TQ <: repo.HasBaseValuesTable[B]

  def tq: TableQuery[TQ]

  def numOfExes: Future[Int] = db.run(tq.size.result)

  def allExes: Future[Seq[B]] = db.run(tq.result)

  def exById(id: Int): Future[Option[B]] = db.run(tq.findBy(_.id).apply(id).result.headOption)

  private def stdStatistics: Future[Html] = numOfExes.zip(statistics).map {
    case (num, stats) => Html(s"<li>Es existieren insgesamt $num Aufgaben $stats</li>")
  }


  case class StrForm(str: String)

  def singleStrForm(str: String) = Form(mapping(str -> nonEmptyText)(StrForm.apply)(StrForm.unapply))

  val PROGRESS_LOGGER: Logger.ALogger = Logger.of("progress")

  def saveUploadedFile(savingDir: Path, pathToUploadedFile: Path, saveTo: Path): Try[Path] =
    Try(Files.createDirectories(savingDir))
      .map(_ => Files.move(pathToUploadedFile, saveTo, StandardCopyOption.REPLACE_EXISTING))

  def log(user: User, eventToLog: WorkingEvent): Unit = Unit // PROGRESS_LOGGER.debug(s"""${user.name} - ${Json.toJson(eventToLog)}""")

  def importExercises: EssentialAction = futureWithAdmin { admin =>
    implicit request =>
      Try(Files.readAllLines(jsonFile).asScala.mkString("\n")).fold(
        fileError => Future(BadRequest(fileError.toString)),
        readDoc => Json.parse(readDoc).validate[List[B]].fold(
          jsonError => /* TODO: Fehlerseite... */ Future(BadRequest(views.html.core.jsonReadingError(admin, readDoc, jsonError))),
          exes => db.run(tq ++= exes).map {
            case Some(_) => Ok(views.html.admin.preview(admin, exes, toolObject))
            case None => BadRequest("")
          }
        )
      )
  }

  protected def statistics: Future[Html] = Future(Html(""))

  protected val savingDir: Path = Paths.get(toolObject.rootDir, StringConsts.ADMIN_FOLDER, toolObject.exType)

  def adminIndex: EssentialAction = futureWithAdmin { user =>
    implicit request =>
      stdStatistics.map(stats => Ok(views.html.admin.exerciseAdminMain.render(user, stats, toolObject, new Html(""))))
  }

  def getJSONSchemaFile: EssentialAction = withAdmin { _ => implicit request => Ok("" /*Json.prettyPrint(exerciseReader.jsonSchema)*/)
  }

  //  protected def processReadingResult(abstractResult: AbstractReadingResult, render: List[SingleReadingResult[E]] => Html): Result =
  //    abstractResult match {
  //      case error: ReadingError =>
  //        BadRequest(views.html.jsonReadingError.render(user, error))
  //
  //      case _: ReadingFailure => BadRequest("There has been an error...")
  //
  //      case result: ReadingResult[E] =>
  //        result.read.foreach(read => {
  //          exerciseReader.save(read.read)
  //          read.fileResults = exerciseReader.checkFiles(read.read)
  //        })
  //        Ok(views.html.admin.preview.render(user, toolObject, result.read))
  //    }

  def uploadFile /*(render: List[SingleReadingResult[E]] => Html)*/ : EssentialAction = withAdmin { _ =>
    implicit request =>
      //    val data: MultipartFormData[File] = Controller.request.body.asMultipartFormData()
      //    Option(data.getFile(StringConsts.BODY_FILE_NAME)) match {
      //      case None => Results.badRequest("Fehler!")
      //      case Some(uploadedFile) =>
      //        val jsonFile = Paths.get(savingDir.toString, uploadedFile.getFilename)
      //        saveUploadedFile(savingDir, uploadedFile.getFile.toPath, jsonFile) match {
      //          case Success(jsonTargetPath) => processReadingResult(exerciseReader.readFromJsonFile(jsonTargetPath), render)
      //          case Failure(error) => Results.badRequest("There has been an error uploading your file...")
      //        }
      //    }
      Ok("TODO!")
  }


  //  import profile.api._

  def changeExState(id: Int): EssentialAction = withAdmin { _ =>
    implicit request =>
      //      finder.byId(id) match {
      //        case None => BadRequest(Json.obj("message" -> "No such file exists..."))
      //
      //        case Some(exercise) =>
      //          exercise.state = ExerciseStateHelper.byName(request.body.asFormUrlEncoded.get("state").mkString).getOrElse(RESERVED)
      //          //          exercise.save()
      //          Ok(Json.obj("id" -> id, "newState" -> exercise.state.toString))
      //      }
      Ok("TODO")
  }

  def deleteExercise(id: Int): EssentialAction = futureWithAdmin { _ =>
    implicit request =>
      exById(id).map {
        //        def exById(id: Int): Future[Option[B]] = db.run(tq.findBy(_.id).apply(id).result.headOption)

        case None =>
          BadRequest(Json.obj("message" -> s"Die Aufgabe mit ID $id existiert nicht und kann daher nicht geloescht werden!"))
        case Some(ex) =>
          //          val query = tq.filter(_.id == id).delete
          //          if (toDelete.delete()) {
          //            Ok(Json.obj("id" -> id))
          //          } else {
          BadRequest(Json.obj("message" -> s"Es gab einen internen Fehler beim Loeschen der Aufgabe mit der ID $id"))
        //          }
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
    implicit request => exById(id).map(ex => Ok(views.html.admin.editExForm(admin, toolObject, ex, renderEditRest(ex))))
  }

  def exercises: EssentialAction = futureWithAdmin { admin =>
    implicit request => allExes.map(exes => Ok(views.html.admin.exerciseList.render(admin, exes, toolObject)))
  }

  def exportExercises: EssentialAction = futureWithAdmin { admin =>
    implicit request => allExes.map(exes => Ok(views.html.admin.export.render(admin, "" /* Json.prettyPrint(exes)*/)))
  }

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

  // Helper methods

  def renderEditRest(exercise: Option[B]): Html = new Html("")

  def renderExercises(exercises: List[B]): Html = new Html("") //views.html.admin.exercisesTable.render(exercises, toolObject)

  def renderExesCreated(admin: User, exercises: List[B]): Html = views.html.admin.preview.render(admin, exercises, toolObject)

}