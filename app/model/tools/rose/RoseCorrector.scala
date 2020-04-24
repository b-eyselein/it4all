package model.tools.rose

import better.files.File._
import better.files._
import model.core.{DockerBind, DockerConnector, ScalaDockerImage}
import model.points._
import model.tools.programming.ProgLanguage
import model.tools.{AbstractCorrector, Exercise}
import play.api.Logger
import play.api.libs.json.{JsError, JsSuccess, Json}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

object RoseCorrector extends AbstractCorrector {

  override type AbstractResult = RoseAbstractResult

  override protected val logger: Logger = Logger(RoseCorrector.getClass)

  override protected def buildInternalError(
    msg: String,
    solutionSaved: Boolean,
    maxPoints: Points
  ): RoseInternalErrorResult = RoseInternalErrorResult(msg, solutionSaved, maxPoints)

  val roseCorrectionDockerImageName: ScalaDockerImage = ScalaDockerImage("beyselein", "rose")

  private val NewLine: String = "\n"

  private val actionsFileName = "actions.json"
  private val optionsFileName = "options.json"

  def correct(
    learnerSolution: String,
    exercise: Exercise[String, RoseExerciseContent],
    language: ProgLanguage,
    solutionTargetDir: File,
    solutionSaved: Boolean
  )(implicit ec: ExecutionContext): Future[RoseAbstractResult] = exercise.content.sampleSolutions.headOption match {
    case None => Future.successful(RoseInternalErrorResult(solutionSaved, (-1).points))
    case Some(completeSampleSolution) =>
      val sampleSolution = completeSampleSolution.sample

      // Check if image exists
      val futureImageExists = Future(DockerConnector.imageExists(roseCorrectionDockerImageName.name))

      val solutionFileName = s"solution_robot.${language.fileEnding}"
      val sampleFileName   = s"sample_robot.${language.fileEnding}"

      val solutionFilePath: File = solutionTargetDir / solutionFileName
      val sampleFilePath: File   = solutionTargetDir / sampleFileName

      val actionFilePath: File  = solutionTargetDir / actionsFileName
      val optionsFilePath: File = solutionTargetDir / optionsFileName

      // FIXME: write exercise options file...
      solutionFilePath.createFileIfNotExists(createParents = true).write(learnerSolution)
      sampleFilePath.createFileIfNotExists(createParents = true).write(buildSampleFileContent(sampleSolution))

      actionFilePath.createIfNotExists()
      optionsFilePath.createFileIfNotExists(createParents = true).write(buildOptionFileContent())

      val dockerBinds: Seq[DockerBind] = Seq(
        DockerBind(solutionFilePath, DockerConnector.DefaultWorkingDir / solutionFileName),
        DockerBind(sampleFilePath, DockerConnector.DefaultWorkingDir / sampleFileName),
        DockerBind(actionFilePath, DockerConnector.DefaultWorkingDir / actionsFileName),
        DockerBind(optionsFilePath, DockerConnector.DefaultWorkingDir / optionsFileName)
      )

      futureImageExists.flatMap {
        case false => Future.successful(onError("The docker image does not exist!", solutionSaved))
        case true =>
          DockerConnector
            .runContainer(roseCorrectionDockerImageName.name, maybeDockerBinds = dockerBinds)
            .map {
              case Failure(exception) =>
                onError("Error while running docker container", solutionSaved, maybeException = Some(exception))
              case Success(runContainerResult) =>
                runContainerResult.statusCode match {
                  case 0 =>
                    Try(Json.parse(actionFilePath.contentAsString))
                      .fold(
                        error =>
                          onError("Error while reading result file", solutionSaved, maybeException = Some(error)),
                        jsValue =>
                          RoseToolJsonProtocol.roseExecutionResultFormat.reads(jsValue) match {
                            case JsSuccess(value, _) =>
                              RoseCompleteResult(value, (-1).points, (-1).points, solutionSaved)
                            case JsError(errors) =>
                              onError(
                                s"Error while reading json\n${errors.map(_.toString).mkString("\n")}",
                                solutionSaved
                              )

                          }
                      )
                  case _ => onError("Error while running container", solutionSaved)
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

  private def buildOptionFileContent(): String =
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
