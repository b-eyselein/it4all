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

  val Image = "beyselein/rose:latest"

  def correct(user: User, exercise: RoseCompleteEx, learnerSolution: String, language: ProgLanguage, exerciseResourcesFolder: Path, solutionTargetDir: Path)
             (implicit ec: ExecutionContext): Future[RoseEvalResult] = {

    // Check if image exists
    val futureImageExists: Future[Boolean] = Future(DockerConnector.imageExists(Image))

    val solutionFileName = s"solution.${language.fileEnding}"
    val actionsFileName = "actions.json"

    val solutionFilePath = solutionTargetDir / solutionFileName
    val actionFilePath = solutionTargetDir / actionsFileName

    val fileWriteTries: Try[(Path, Path)] = for {
      solFileTry <- write(solutionFilePath, buildSolutionFileContent(exercise, learnerSolution))
      actionFileTry <- createEmptyFile(actionFilePath)
    } yield (solFileTry, actionFileTry)

    val dockerBinds: Seq[DockerBind] = Seq(
      new DockerBind(solutionFilePath, DockerConnector.DefaultWorkingDir + "/" + solutionFileName),
      new DockerBind(actionFilePath, DockerConnector.DefaultWorkingDir + "/" + actionsFileName)
    )

    val entryPoint = Seq("python3", "sp_main.py")

    fileWriteTries match {
      case Failure(e) =>
        Logger.error("Some files could not be written:", e)
        Future(RoseEvalFailed)
      case Success(_) =>

        futureImageExists flatMap { _ =>
          DockerConnector.runContainer(Image, entryPoint, dockerBinds)
        } map {
          // Error while waiting for container
          case RunContainerTimeOut => RoseTimeOutResult

          // Error while running script with status code other than 0 or 124 (from timeout!)
          case RunContainerError(_, msg) => RoseSyntaxErrorResult(msg)

          case RunContainerSuccess => readAll(actionFilePath) map (content => RoseExecutionResult(content)) getOrElse RoseEvalFailed

          case exc: RunContainerException =>
            Logger.error("Error running container:", exc.error)
            RoseEvalFailed
        }
    }
  }

  private def buildSolutionFileContent(exercise: RoseCompleteEx, learnerSolution: String) =
    exercise.imports + (NewLine * 3) + learnerSolution + (NewLine * 3) + exercise.buildSampleSolution + (NewLine * 3)


}
