package controllers.core


import java.nio.file.{Files, Path, StandardCopyOption}

import model.StringConsts.SESSION_ID_FIELD
import model.logging.WorkingEvent
import model.user.User
import play.Logger
import play.api.mvc._

import scala.util.Try

abstract class BaseController(cc: ControllerComponents) extends AbstractController(cc) {

  def getUser(session: Session): User = User.finder.byId(getUsername(session))

  def getUsername(session: Session): String = session.get(SESSION_ID_FIELD) match {
    case None => throw new IllegalArgumentException("There is no user name!")
    case Some(name) => name
  }


  val PROGRESS_LOGGER: Logger.ALogger = Logger.of("progress")

  def saveUploadedFile(savingDir: Path, pathToUploadedFile: Path, saveTo: Path): Try[Path] =
    Try(Files.createDirectories(savingDir))
      .map(_ => Files.move(pathToUploadedFile, saveTo, StandardCopyOption.REPLACE_EXISTING))

  def log(user: User, eventToLog: WorkingEvent): Unit = PROGRESS_LOGGER.debug(s"""${user.name} - ${Json.toJson(eventToLog)}""")

}