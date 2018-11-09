package model.rose

import better.files.File._
import better.files._
import model.User
import model.docker._
import model.programming.ProgLanguage
import play.api.Logger

import scala.concurrent.{ExecutionContext, Future}

object RoseCorrector {

  val ScriptName  : String = "script.py"
  val TestDataFile: String = "testconfig.json"

  val NewLine: String = "\n"

  val Image: String = "beyselein/rose:latest"

  def correct(user: User, exercise: RoseCompleteEx, learnerSolution: String, language: ProgLanguage, exerciseResourcesFolder: File, solutionTargetDir: File)
             (implicit ec: ExecutionContext): Future[RoseEvalResult] = {

    // Check if image exists
    val futureImageExists: Future[Boolean] = Future(DockerConnector.imageExists(Image))

    val solutionFileName = s"solution.${language.fileEnding}"
    val actionsFileName = "actions.json"
    val optionsFileName = "options.json"

    val solutionFilePath: File = solutionTargetDir / solutionFileName
    val actionFilePath: File = solutionTargetDir / actionsFileName
    val optionsFilePath: File = solutionTargetDir / optionsFileName


    // FIXME: write exercise options file...
    solutionFilePath.write(buildSolutionFileContent(exercise, learnerSolution, language))
    actionFilePath.createIfNotExists()
    optionsFilePath.write(buildOptionFileContent(exercise))

    val dockerBinds: Seq[DockerBind] = Seq(
      DockerBind(solutionFilePath, DockerConnector.DefaultWorkingDir / solutionFileName),
      DockerBind(actionFilePath, DockerConnector.DefaultWorkingDir / actionsFileName),
      DockerBind(optionsFilePath, DockerConnector.DefaultWorkingDir / optionsFileName)
    )

    val entryPoint = Seq("python3", "sp_main.py")

    //    fileWriteTries match {
    //      case Failure(e) =>
    //        Logger.error("Some files could not be written:", e)
    //        Future(RoseEvalFailed)
    //      case Success(_) =>

    futureImageExists flatMap { _ =>
      DockerConnector.runContainer(Image, Some(entryPoint), dockerBinds
        //            , deleteContainerAfterRun = false
      )
    } map {
      // Error while waiting for container
      case RunContainerTimeOut(_) => RoseTimeOutResult

      // Error while running script with status code other than 0 or 124 (from timeout!)
      case RunContainerError(_, msg) => RoseSyntaxErrorResult(msg)

      case RunContainerSuccess => RoseExecutionResult(actionFilePath.contentAsString) //map (content => RoseExecutionResult(content)) getOrElse RoseEvalFailed

      case exc: RunContainerException =>
        Logger.error("Error running container:", exc.error)
        RoseEvalFailed
    }
    //    }
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
