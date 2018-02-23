package model.rose

import java.nio.file.Path
import model.User
import model.core.FileUtils
import model.docker._
import model.programming.ProgLanguage
import play.api.Logger

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

object RoseCorrector extends FileUtils {

  val ScriptName = "script.py"

  val TestDataFile = "testconfig.json"

  val NewLine = "\n"

  def correct(user: User, exercise: RoseCompleteEx, learnerSolution: String, language: ProgLanguage)(implicit ec: ExecutionContext): Future[RoseEvalResult] = {

    // Check if image exists
    val futureImageExists: Future[Boolean] = Future(DockerConnector.imageExists(language.dockerImageName) || DockerConnector.pullImage(language.dockerImageName))

    val targetDir = RoseToolObject.solutionDirForExercise(user.username, exercise.id)

    val solutionFileContent: String = exercise.imports + (NewLine * 3) + learnerSolution + (NewLine * 3) + exercise.buildSampleSolution + (NewLine * 3)

    val filesCopied: Seq[Try[Path]] = Seq("sp_main.py", "sp_validation.py") map { filePath =>
      copy(filePath, RoseToolObject.exerciseResourcesFolder, targetDir)
    }

    val filesWritten = for {
      solFile <- write(targetDir / s"solution.${language.fileEnding}", solutionFileContent)
    } yield solFile

    val dockerBinds: Seq[DockerBind] = Seq(
      new DockerBind(RoseToolObject.exerciseResourcesFolder / "base", DockerConnector.DefaultWorkingDir + "/base"),
      new DockerBind(targetDir, DockerConnector.DefaultWorkingDir)
    )


    filesWritten match {
      case Failure(e) =>
        Logger.error("Some files could not be written", e)
        Future(RoseEvalFailed)
      case Success(_) =>

        futureImageExists flatMap { _ =>
          DockerConnector.runContainer(language.dockerImageName, entryPoint = Seq("python3", "sp_main.py"), dockerBinds, deleteContainerAfterRun = false)
        } map {
          // Error while waiting for container
          case RunContainerTimeOut => RoseTimeOutResult

          // Error while running script with status code other than 0 or 124 (from timeout!)
          case RunContainerError(_, msg) => RoseSyntaxErrorResult(msg)

          case RunContainerSuccess => readAll(targetDir / "actions.json") map (content => RoseExecutionResult(content)) getOrElse RoseEvalFailed

          case exc: RunContainerException =>
            Logger.error("Error running container:", exc.error)
            RoseEvalFailed
        }
    }
  }

}
