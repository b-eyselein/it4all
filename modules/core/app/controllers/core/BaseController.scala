package controllers.core

import java.io.IOException
import java.nio.file.{ Files, Path, Paths, StandardCopyOption }

import scala.util.{ Failure, Success, Try }

import model.{ CommonUtils, StringConsts }
import model.exercise.Exercise
import model.logging.WorkingEvent
import model.user.User
import play.Logger
import play.data.FormFactory
import play.libs.Json
import play.mvc.{ Controller, Http }

abstract class BaseController(val factory: FormFactory) extends Controller {

}

object BaseController {

  val PROGRESS_LOGGER: Logger.ALogger = Logger.of("progress")

  val BASE_DATA_PATH = "/data"

  val SAMPLE_SUB_DIRECTORY = "samples"
  val SOLUTIONS_SUB_DIRECTORY = "solutions"

  def saveUploadedFile(savingDir: Path, pathToUploadedFile: Path, saveTo: Path): Try[Path] = {
    try {
      if (!savingDir.toFile().exists())
        Files.createDirectories(savingDir)
      Success(Files.move(pathToUploadedFile, saveTo, StandardCopyOption.REPLACE_EXISTING))
    } catch {
      case e: IOException ⇒ Failure(e)
    }
  }

  def getSampleDir(exerciseType: String) = Paths.get(BASE_DATA_PATH, SAMPLE_SUB_DIRECTORY, exerciseType)

  def getSolDirForUser(username: String) = Paths.get(BASE_DATA_PATH, SOLUTIONS_SUB_DIRECTORY, username)

  def getUser = User.finder.byId(getUsername)

  def getSolDirForUser: Path = getSolDirForUser(getUsername)

  def getSampleDirForExercise(exerciseType: String, exercise: Exercise) = Paths.get(getSampleDir(exerciseType).toString(), String.valueOf(exercise.getId()))

  def getSolDirForExercise(username: String, exerciseType: String, exercise: Exercise) =
    Paths.get(getSolDirForUser(username).toString(), exerciseType, String.valueOf(exercise.getId()))

  def getSolFileForExercise(username: String, exerciseType: String, exercise: Exercise, fileName: String,
                            fileExtension: String) =
    Paths.get(getSolDirForExercise(username, exerciseType, exercise).toString(), fileName + "." + fileExtension)

  def getUsername = {
    val session: Http.Session = Http.Context.current().session()

    if (session == null || session.get(StringConsts.SESSION_ID_FIELD) == null
      || session.get(StringConsts.SESSION_ID_FIELD).isEmpty())
      throw new IllegalArgumentException("No user name was given!")

    session.get(StringConsts.SESSION_ID_FIELD)
  }

  def log(user: User, eventToLog: WorkingEvent) {
    PROGRESS_LOGGER.debug(user.name + " - " + Json.toJson(eventToLog))
  }

}
