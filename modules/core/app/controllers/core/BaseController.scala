package controllers.core

import java.nio.file.{Files, Path, StandardCopyOption}

import model.StringConsts
import model.logging.WorkingEvent
import model.user.User
import play.Logger
import play.api.Configuration
import play.data.FormFactory
import play.libs.Json
import play.mvc.{Controller, Http}

import scala.util.Try

abstract class BaseController(val factory: FormFactory) extends Controller {

  def getUser: User = User.finder.byId(getUsername)

  def getUsername: String = {
    val session: Http.Session = Http.Context.current().session()

    if (session == null || session.get(StringConsts.SESSION_ID_FIELD) == null
      || session.get(StringConsts.SESSION_ID_FIELD).isEmpty)
      throw new IllegalArgumentException("No user name was given!")

    session.get(StringConsts.SESSION_ID_FIELD)
  }


  val PROGRESS_LOGGER: Logger.ALogger = Logger.of("progress")

  def saveUploadedFile(savingDir: Path, pathToUploadedFile: Path, saveTo: Path): Try[Path] =
    Try(Files.createDirectories(savingDir))
      .map(_ => Files.move(pathToUploadedFile, saveTo, StandardCopyOption.REPLACE_EXISTING))


  def log(user: User, eventToLog: WorkingEvent): Unit = PROGRESS_LOGGER.debug(s"""${user.name} - ${Json.toJson(eventToLog)}""")

}

object BaseController {

  def getRootDir(c: Configuration): String = c.get[String]("datafolder")

}