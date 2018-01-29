package model.rose

import java.nio.file.{Files, Path}

import controllers.exes.idPartExes.RoseToolObject
import model.User
import model.core.FileUtils
import model.programming.ProgrammingCorrector.FilePermissions
import model.programming._
import play.api.Logger

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

object RoseCorrector extends FileUtils {

  val ScriptName = "script.py"

  val TestDataFile = "testconfig.json"

  val NewLine = "\n"

  def correct(user: User, exercise: RoseCompleteEx, learnerSolution: String, language: ProgLanguage)(implicit ec: ExecutionContext): Future[Seq[ProgEvalResult]] = {

    // Check if image exists
    val futureImageExists: Future[Boolean] = Future(DockerConnector.imageExists(language.dockerImageName) || DockerConnector.pullImage(language.dockerImageName))

    val targetDir = RoseToolObject.solutionDirForExercise(user.username, exercise.id)

    val scriptTargetPath = targetDir / ScriptName

    val solutionFileContent: String = exercise.imports + (NewLine * 3) + learnerSolution + (NewLine * 3) + exercise.buildSampleSolution + (NewLine * 3)

    val filesWritten: Try[(Path, Path, Path)] = for {
      solFile <- write(targetDir / "solution" / s"solution.${language.fileEnding}", solutionFileContent)
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

  def copyAllScriptFiles(sourceDir: Path, targetDir: Path): Try[Seq[Path]] = {
    val res: Array[Try[Path]] = sourceDir.toFile.listFiles.map(_.toPath.toAbsolutePath) map (path => copy(path, targetDir / path.getFileName))

    ???
  }

}
