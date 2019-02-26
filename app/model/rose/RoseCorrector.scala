package model.rose

import better.files.File._
import better.files._
import model.User
import model.docker._
import model.programming.ProgLanguage
import modules.DockerPullsStartTask
import play.api.Logger

import scala.concurrent.{ExecutionContext, Future}

object RoseCorrector {

  private val NewLine: String = "\n"

  private val actionsFileName = "actions.json"
  private val optionsFileName = "options.json"

  def correct(user: User, exercise: RoseExercise, learnerSolution: String, language: ProgLanguage, exerciseResourcesFolder: File, solutionTargetDir: File)
             (implicit ec: ExecutionContext): Future[RoseEvalResult] = exercise.sampleSolutions.headOption match {
    case None                 => ???
    case Some(sampleSolution) =>

      // Check if image exists
      val futureImageExists: Future[Boolean] = Future(DockerConnector.imageExists(DockerPullsStartTask.roseImage))

      val solutionFileName = s"solution_robot.${language.fileEnding}"
      val sampleFileName = s"sample_robot.${language.fileEnding}"

      val solutionFilePath: File = solutionTargetDir / solutionFileName
      val sampleFilePath: File = solutionTargetDir / sampleFileName

      val actionFilePath: File = solutionTargetDir / actionsFileName
      val optionsFilePath: File = solutionTargetDir / optionsFileName

      // FIXME: write exercise options file...
      solutionFilePath.createFileIfNotExists(createParents = true).write(learnerSolution)
      sampleFilePath.createFileIfNotExists(createParents = true).write(buildSampleFileContent(sampleSolution.sample))

      actionFilePath.createIfNotExists()
      optionsFilePath.createFileIfNotExists(createParents = true).write(buildOptionFileContent(exercise))

      val dockerBinds: Seq[DockerBind] = Seq(
        DockerBind(solutionFilePath, DockerConnector.DefaultWorkingDir / solutionFileName),
        DockerBind(sampleFilePath, DockerConnector.DefaultWorkingDir / sampleFileName),

        DockerBind(actionFilePath, DockerConnector.DefaultWorkingDir / actionsFileName),
        DockerBind(optionsFilePath, DockerConnector.DefaultWorkingDir / optionsFileName)
      )

      //    val entryPoint = Seq("python3", "sp_main.py")

      futureImageExists flatMap { _ =>
        DockerConnector.runContainer(
          imageName = DockerPullsStartTask.roseImage,
          maybeEntryPoint = None /* Some(entryPoint)*/ ,
          maybeDockerBinds = Some(dockerBinds) /*,
          deleteContainerAfterRun = false */)
      } map {
        // Error while waiting for container
        case RunContainerTimeOut(_) => RoseTimeOutResult

        // Error while running script with status code other than 0 or 124 (from timeout!)
        case RunContainerError(_, msg) => RoseSyntaxErrorResult(msg)

        case RunContainerSuccess => RoseExecutionResult(actionFilePath.contentAsString)

        case exc: RunContainerException =>
          Logger.error("Error running container:", exc.error)
          RoseEvalFailed
      }
  }

  private def indent(str: String, depth: Int): String = str.split(NewLine).map(" " * 4 * depth + _).mkString(NewLine)

  private def buildSampleFileContent(sampleSolution: String): String = {
    val baseDeclaration =
      """from base.robot import Robot
        |
        |
        |class SampleRobot(Robot):
        |    def run(self, width: int, height: int):
        |""".stripMargin

    baseDeclaration + indent(sampleSolution, 2)
  }

  private def buildOptionFileContent(exercise: RoseExercise): String =
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
