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
    val optionsFileName = "options.json"

    val solutionFilePath = solutionTargetDir / solutionFileName
    val actionFilePath = solutionTargetDir / actionsFileName
    val optionsFilePath = solutionTargetDir / optionsFileName


    val fileWriteTries: Try[(Path, Path)] = for {
      // FIXME: write exercise options file...
      solFileTry <- write(solutionFilePath, buildSolutionFileContent(exercise, learnerSolution, language))
      actionFileTry <- createEmptyFile(actionFilePath)
      optionsFiletry <- write(optionsFilePath, buildOptionFileContent(exercise))
    } yield (solFileTry, actionFileTry)

    val dockerBinds: Seq[DockerBind] = Seq(
      new DockerBind(solutionFilePath, DockerConnector.DefaultWorkingDir + "/" + solutionFileName),
      new DockerBind(actionFilePath, DockerConnector.DefaultWorkingDir + "/" + actionsFileName),
      new DockerBind(optionsFilePath, DockerConnector.DefaultWorkingDir + "/" + optionsFileName)
    )

    val entryPoint = Seq("python3", "sp_main.py")

    fileWriteTries match {
      case Failure(e) =>
        Logger.error("Some files could not be whttp://localhost:63342/api/file/?file=/home/bjorn/workspace/it4all/app/model/rose/RoseExYamlProtocol.scala&line=29ritten:", e)
        Future(RoseEvalFailed)
      case Success(_) =>

        futureImageExists flatMap { _ =>
          DockerConnector.runContainer(Image, entryPoint, dockerBinds, deleteContainerAfterRun = false)
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

  private def buildSolutionFileContent(exercise: RoseCompleteEx, learnerSolution: String, language: ProgLanguage): String =
    exercise.imports + (NewLine * 3) + learnerSolution + (NewLine * 3) + exercise.buildSampleSolution(language) + (NewLine * 3)

  private def buildOptionFileContent(exercise: RoseCompleteEx): String =
    """|{
       |  "start": {
       |    "x": 0,
       |    "y": 0
       |  },
       |  "field": {
       |    "width": 8,
       |    "height": 10
       |  },
       |  "run_options": [
       |    7,
       |    3
       |  ]
       |}""".stripMargin

}
