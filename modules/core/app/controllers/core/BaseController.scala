package controllers.core

import java.nio.file.{Files, Path, StandardCopyOption}

import model.StringConsts.SESSION_ID_FIELD
import model.logging.WorkingEvent
import model.user.User
import play.Logger
import play.libs.Json
import play.mvc.{Controller, Http}

import scala.util.Try

abstract class BaseController(val factory: play.data.FormFactory) extends Controller {

  def getUser: User = User.finder.byId(getUsername)

  def getUsername: String = Option(Http.Context.current().session()) match {
    case None => throw new IllegalArgumentException("There is no session set!")
    case Some(session) =>
      val name = session.get(SESSION_ID_FIELD)
      if (name == null || name.isEmpty) throw new IllegalArgumentException("No user name was given!")
      else name
  }

  val PROGRESS_LOGGER: Logger.ALogger = Logger.of("progress")

  def saveUploadedFile(savingDir: Path, pathToUploadedFile: Path, saveTo: Path): Try[Path] =
    Try(Files.createDirectories(savingDir))
      .map(_ => Files.move(pathToUploadedFile, saveTo, StandardCopyOption.REPLACE_EXISTING))


  def log(user: User, eventToLog: WorkingEvent): Unit = PROGRESS_LOGGER.debug(s"""${user.name} - ${Json.toJson(eventToLog)}""")

}