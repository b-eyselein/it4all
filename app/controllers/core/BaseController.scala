package controllers.core

import java.nio.file.{Files, Path, StandardCopyOption}

import model.User
import model.core.{Repository, WorkingEvent}
import play.Logger
import play.api.data.Form
import play.api.data.Forms._
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.mvc._
import slick.jdbc.JdbcProfile

import scala.concurrent.ExecutionContext
import scala.util.Try

// TODO: evtl. als object??
abstract class BaseController(cc: ControllerComponents, protected val dbConfigProvider: DatabaseConfigProvider, val repo: Repository)
                             (implicit ec: ExecutionContext)
  extends AbstractController(cc) with HasDatabaseConfigProvider[JdbcProfile] {

  case class StrForm(str: String)

  def singleStrForm(str: String) = Form(
    mapping(str -> nonEmptyText)(StrForm.apply)(StrForm.unapply)
  )

  val PROGRESS_LOGGER: Logger.ALogger = Logger.of("progress")

  def saveUploadedFile(savingDir: Path, pathToUploadedFile: Path, saveTo: Path): Try[Path] =
    Try(Files.createDirectories(savingDir))
      .map(_ => Files.move(pathToUploadedFile, saveTo, StandardCopyOption.REPLACE_EXISTING))

  def log(user: User, eventToLog: WorkingEvent): Unit = Unit // PROGRESS_LOGGER.debug(s"""${user.name} - ${Json.toJson(eventToLog)}""")

}