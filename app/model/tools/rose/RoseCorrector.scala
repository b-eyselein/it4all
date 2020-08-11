package model.tools.rose

import better.files._
import model.SampleSolution
import model.core.{DockerBind, DockerConnector, ScalaDockerImage}
import model.points._
import model.tools.AbstractCorrector
import model.tools.rose.RoseTool.RoseExercise
import play.api.Logger
import play.api.libs.json.{JsError, JsSuccess, Json}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

object RoseCorrector extends AbstractCorrector {

  override type AbstractResult = RoseAbstractResult

  override protected val logger: Logger = Logger(RoseCorrector.getClass)

  override protected def buildInternalError(
    msg: String,
    maxPoints: Points
  ): RoseInternalErrorResult = RoseInternalErrorResult(msg, maxPoints)

  val roseCorrectionDockerImageName: ScalaDockerImage = ScalaDockerImage("beyselein", "rose")

  private val NewLine: String = "\n"

  private val actionsFileName = "actions.json"
  private val optionsFileName = "options.json"

  private val solutionFileName = s"solution_robot.py"
  private val sampleFileName   = s"sample_robot.py"

  def correct(
    learnerSolution: String,
    exercise: RoseExercise,
    solutionTargetDir: File
  )(implicit ec: ExecutionContext): Future[RoseAbstractResult] = {

    val maxPoints = ???

    val dockerBindPath = DockerConnector.DefaultWorkingDir

    exercise.content.sampleSolutions.headOption match {
      case None => Future.successful(onError("No sample solution for rose exercise!", maxPoints))
      case Some(SampleSolution(_, sample)) =>
        val solutionFilePath: File = solutionTargetDir / solutionFileName
        solutionFilePath
          .createFileIfNotExists(createParents = true)
          .write(learnerSolution)

        val sampleFilePath: File = solutionTargetDir / sampleFileName
        sampleFilePath
          .createFileIfNotExists(createParents = true)
          .write(buildSampleFileContent(sample))

        val actionFilePath: File = solutionTargetDir / actionsFileName
        actionFilePath.createIfNotExists()

        val optionsFilePath: File = solutionTargetDir / optionsFileName
        optionsFilePath
          .createFileIfNotExists(createParents = true)
          .write(buildOptionFileContent())

        val dockerBinds: Seq[DockerBind] = Seq(
          DockerBind(solutionFilePath, dockerBindPath / solutionFileName),
          DockerBind(sampleFilePath, dockerBindPath / sampleFileName),
          DockerBind(actionFilePath, dockerBindPath / actionsFileName),
          DockerBind(optionsFilePath, dockerBindPath / optionsFileName)
        )

        // Check if image exists
        Future(DockerConnector.imageExists(roseCorrectionDockerImageName.name))
          .flatMap {
            case false => Future.successful(onError("The docker image does not exist!", maxPoints))
            case true =>
              DockerConnector
                .runContainer(roseCorrectionDockerImageName.name, maybeDockerBinds = dockerBinds)
                .map {
                  case Failure(exception) => onError("Error while running docker container", maxPoints, Some(exception))
                  case Success(runContainerResult) =>
                    runContainerResult.statusCode match {
                      case 0 =>
                        Try(Json.parse(actionFilePath.contentAsString))
                          .fold(
                            error => onError("Error while reading result file", maxPoints, Some(error)),
                            jsValue =>
                              RoseToolJsonProtocol.roseExecutionResultFormat.reads(jsValue) match {
                                case JsError(errors) =>
                                  onError(
                                    s"Error while reading json:\n${errors.map(_.toString).mkString("\n")}",
                                    maxPoints
                                  )
                                case JsSuccess(value, _) =>
                                  val points = ???
                                  RoseResult(value, points, maxPoints)
                              }
                          )
                      case _ => onError("Error while running container", maxPoints)
                    }
                }
          }
    }
  }

  private def indent(str: String, indentDepth: Int = 4): String =
    str
      .split(NewLine)
      .map(s => " " * indentDepth * 2 + s)
      .mkString(NewLine)

  private def buildSampleFileContent(sampleSolution: String): String = {
    val baseDeclaration =
      """from base.robot import Robot
        |
        |
        |class SampleRobot(Robot):
        |    def run(self, width: int, height: int):
        |""".stripMargin

    baseDeclaration + indent(sampleSolution)
  }

  private def buildOptionFileContent(): String =
    Json.stringify(
      Json.obj(
        "start"       -> Json.obj("x" -> 0, "y" -> 0),
        "field"       -> Json.obj("width" -> 8, "height" -> 10),
        "run_options" -> Json.arr(7, 3)
      )
    )

}
