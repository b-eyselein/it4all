package controllers.core

import java.nio.file.{Files, Path, Paths}

import model.core._
import model.core.tools.ExToolObject
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.libs.json.{Json, Reads}
import play.api.mvc.{ControllerComponents, EssentialAction}
import play.twirl.api.Html
import slick.jdbc.JdbcProfile
import scala.collection.JavaConverters._
import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

abstract class BaseAdminController[B <: HasBaseValues]
(cc: ControllerComponents, dbcp: DatabaseConfigProvider, r: Repository, val toolObject: ExToolObject)(implicit ec: ExecutionContext)
  extends BaseController(cc, dbcp, r) with HasDatabaseConfigProvider[JdbcProfile] with Secured {

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

  def importExercises: EssentialAction = futureWithAdmin { admin =>
    implicit request =>
      Try(Files.readAllLines(jsonFile).asScala.mkString("\n")).fold(
        fileError => Future(BadRequest(fileError.toString)),
        readDoc => Json.parse(readDoc).validate[List[B]].fold(
          jsonError => /* TODO: Fehlerseite... */ Future(BadRequest(views.html.core.jsonReadingError(admin, readDoc, jsonError))),
          exes => db.run(tq ++= exes).map {
            case Some(affectedRows) => Ok(views.html.admin.preview(admin, exes, toolObject))
            case None               => BadRequest("")
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

}