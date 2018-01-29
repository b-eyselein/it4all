package model.rose

import java.nio.file.{Files, Path}

import controllers.exes.idPartExes.{ProgToolObject, RoseToolObject}
import model.User
import model.core.FileUtils
import model.programming.ProgrammingCorrector.{FilePermissions, copy, readResultFile, write}
import model.programming._
import play.api.Logger

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

object RoseCorrector extends FileUtils {

  val ScriptName = "script.py"

  val TestDataFile = "testconfig.json"

  def correct(user: User, exercise: RoseCompleteEx, learnerSolution: String, language: ProgLanguage)(implicit ec: ExecutionContext): Future[Seq[ProgEvalResult]] = {

    // Check if image exists
    val futureImageExists: Future[Boolean] = Future(DockerConnector.imageExists(language.dockerImageName) || DockerConnector.pullImage(language.dockerImageName))

    val targetDir = RoseToolObject.solutionDirForExercise(user.username, exercise.id)

    val scriptTargetPath = targetDir / ScriptName

    val script = if (learnerSolution endsWith "\n") learnerSolution else learnerSolution + "\n"

    val filesWritten: Try[(Path, Path, Path)] = for {
      solFile <- write(targetDir / s"solution.${language.fileEnding}", script)
      scriptFile <- copy(RoseToolObject.exerciseResourcesFolder / ScriptName, scriptTargetPath)
      permissionsSet <- Try(Files.setPosixFilePermissions(scriptTargetPath, FilePermissions))
    } yield (solFile, scriptFile, permissionsSet)

    filesWritten match {
      case Failure(e) =>
        Logger.error("Some files could not be written", e)
        Future(Seq.empty)
      case Success(_) =>
        futureImageExists flatMap {
          _ => DockerConnector.runContainer(language, targetDir)
        } map {
          // Error while waiting for container
          case RunContainerException(_) => Seq(ProgEvalFailed)

          case RunContainerTimeOut => Seq(TimeOut)

          // Error while running script with status code other than 0 or 124 (from timeout!)
          case RunContainerError(_, msg) => Seq(SyntaxError(reason = msg))

          case RunContainerSuccess => Seq.empty // readResultFile(targetDir)
        }
    }
  }


}
