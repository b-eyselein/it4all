package model.tools.collectionTools.rose

import better.files.File._
import better.files._
import model.User
import model.core.{DockerBind, DockerConnector, ScalaDockerImage}
import model.points._
import model.tools.collectionTools.programming.ProgLanguage
import play.api.libs.json.{JsError, JsSuccess, Json}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

object RoseCorrector {

  val roseCorrectionDockerImageName: ScalaDockerImage = ScalaDockerImage("beyselein", "rose")

  private val NewLine: String = "\n"

  private val actionsFileName = "actions.json"
  private val optionsFileName = "options.json"

  def correct(
    user: User,
    exercise: RoseExerciseContent,
    learnerSolution: String,
    sampleSolution: String,
    language: ProgLanguage,
    solutionTargetDir: File,
    solutionSaved: Boolean
  )(implicit ec: ExecutionContext): Future[Try[RoseCompleteResult]] = {

    // Check if image exists
    val futureImageExists = Future(DockerConnector.imageExists(roseCorrectionDockerImageName.name))

    val solutionFileName = s"solution_robot.${language.fileEnding}"
    val sampleFileName   = s"sample_robot.${language.fileEnding}"

    val solutionFilePath: File = solutionTargetDir / solutionFileName
    val sampleFilePath  : File = solutionTargetDir / sampleFileName

    val actionFilePath : File = solutionTargetDir / actionsFileName
    val optionsFilePath: File = solutionTargetDir / optionsFileName

    // FIXME: write exercise options file...
    solutionFilePath.createFileIfNotExists(createParents = true).write(learnerSolution)
    sampleFilePath.createFileIfNotExists(createParents = true).write(buildSampleFileContent(sampleSolution))

    actionFilePath.createIfNotExists()
    optionsFilePath.createFileIfNotExists(createParents = true).write(buildOptionFileContent(exercise))

    val dockerBinds: Seq[DockerBind] = Seq(
      DockerBind(solutionFilePath, DockerConnector.DefaultWorkingDir / solutionFileName),
      DockerBind(sampleFilePath, DockerConnector.DefaultWorkingDir / sampleFileName),

      DockerBind(actionFilePath, DockerConnector.DefaultWorkingDir / actionsFileName),
      DockerBind(optionsFilePath, DockerConnector.DefaultWorkingDir / optionsFileName)
    )

    //    val entryPoint = Seq("python3", "sp_main.py")

    futureImageExists.flatMap {
      case false => Future.successful(Failure(new Exception("The docker image does not exist!")))
      case true  =>
        DockerConnector
          .runContainer(
            roseCorrectionDockerImageName.name,
            maybeEntryPoint = None /* Some(entryPoint)*/ ,
            maybeDockerBinds = dockerBinds /*,
          deleteContainerAfterRun = false */
          )
          .map {
            case Failure(exception)          => Failure(exception)
            case Success(runContainerResult) =>

              runContainerResult.statusCode match {
                case 0 =>
                  Try(Json.parse(actionFilePath.contentAsString)).map { jsValue =>

                    RoseToolJsonProtocol.roseExecutionResultFormat.reads(jsValue) match {
                      case JsError(errors)     => ???
                      case JsSuccess(value, _) => RoseCompleteResult(value, (-1).points, (-1).points, solutionSaved)
                    }

                  }
                case _ => ???
              }
          }
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

  private def buildOptionFileContent(exercise: RoseExerciseContent): String =
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