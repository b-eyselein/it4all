package model.tools.rose

import better.files._
import model.core.{DockerBind, DockerConnector, ScalaDockerImage}
import model.tools.DockerExecutionCorrector
import model.tools.rose.RoseTool.RoseExercise
import play.api.libs.json.Json

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Try}

object RoseCorrector extends DockerExecutionCorrector {

  val roseCorrectionDockerImageName: ScalaDockerImage = ScalaDockerImage("beyselein", "rose")

  override protected val dockerImage: ScalaDockerImage = roseCorrectionDockerImageName

  private val NewLine: String = "\n"

  private val actionsFileName = "actions.json"
  private val optionsFileName = "options.json"

  private val solutionFileName = s"solution_robot.py"
  private val sampleFileName   = s"sample_robot.py"

  def correct(
    learnerSolution: String,
    exercise: RoseExercise,
    solutionTargetDir: File
  )(implicit ec: ExecutionContext): Future[Try[RoseAbstractResult]] =
    exercise.content.sampleSolutions.headOption match {
      case None => Future.successful(Failure(new Exception("No sample solution for rose exercise!")))
      case Some(sample) =>
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

        val dockerBindPath = DockerConnector.DefaultWorkingDir

        val dockerBinds: Seq[DockerBind] = Seq(
          DockerBind(solutionFilePath, dockerBindPath / solutionFileName),
          DockerBind(sampleFilePath, dockerBindPath / sampleFileName),
          DockerBind(actionFilePath, dockerBindPath / actionsFileName),
          DockerBind(optionsFilePath, dockerBindPath / optionsFileName)
        )

        // Check if image exists
        runContainer(
          dockerBinds,
          RoseToolJsonProtocol.roseExecutionResultFormat,
          actionFilePath
        )(jsValue => RoseResult(jsValue, points = ???, maxPoints = ???))
    }

  private def indent(str: String, indentDepth: Int = 4): String = str
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
