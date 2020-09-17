package model.tools.flask

import model.core._
import model.points._
import model.tools.DockerExecutionCorrector
import model.tools.flask.FlaskTool.{FlaskExercise, solutionDirForExercise}
import model.tools.flask.FlaskToolJsonProtocol.{flaskCorrectionResultFileReads, flaskTestsConfigFormat}
import model.{FilesSolution, points}
import play.api.Logger
import play.api.libs.json.Json

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

object FlaskCorrector extends DockerExecutionCorrector {

  val flaskCorrectionDockerImage: ScalaDockerImage = ScalaDockerImage("ls6uniwue", "flask_tester", "0.0.3")

  override protected val logger: Logger = Logger(this.getClass)

  override type AbstractResult = FlaskAbstractResult

  override protected def buildInternalError(msg: String, maxPoints: points.Points): FlaskInternalErrorResult =
    FlaskInternalErrorResult(msg, maxPoints)

  def correct(
    username: String,
    solution: FilesSolution,
    exercise: FlaskExercise
  )(implicit executionContext: ExecutionContext): Future[FlaskAbstractResult] = {

    val maxPoints = exercise.content.maxPoints.points

    val solTargetDir = solutionDirForExercise(username, exercise.collectionId, exercise.exerciseId)

    // Write solution files
    val appUnderTestTargetDir = solTargetDir / "app"
    solution.files.foreach { f =>
      val targetFile = appUnderTestTargetDir / f.name

      targetFile
        .createIfNotExists(createParents = true)
        .write(f.content)
    }

    // Write helper files
    val resultFile = solTargetDir / resultFileName

    resultFile
      .createIfNotExists(createParents = true)
      .clear()

    // Write test config
    val testConfigFile = solTargetDir / testConfigFileName

    val testConfigFileContent = Json.stringify(
      flaskTestsConfigFormat.writes(exercise.content.testConfig)
    )

    testConfigFile
      .createIfNotExists(createParents = true)
      .write(testConfigFileContent)

    // write test files
    val testFileBinds = exercise.content.testFiles.map { f =>
      val targetFile = solTargetDir / f.name

      targetFile
        .createIfNotExists(createParents = true)
        .write(f.content)

      DockerBind(fromPath = targetFile, baseBindPath / f.name, isReadOnly = true)
    }

    // Create mounts

    val dockerBinds = Seq(
      DockerBind(fromPath = appUnderTestTargetDir, toPath = baseBindPath / "app", isReadOnly = true),
      DockerBind(fromPath = testConfigFile, toPath = baseBindPath / testConfigFileName, isReadOnly = true),
      DockerBind(fromPath = resultFile, toPath = baseBindPath / resultFileName)
    ) ++ testFileBinds

    DockerConnector
      .runContainer(
        imageName = flaskCorrectionDockerImage.name,
        maybeDockerBinds = dockerBinds,
        deleteContainerAfterRun = _ => false
      )
      .map {
        case Failure(exception) => onError("Error running tester container", maxPoints, Some(exception))
        case Success(RunContainerResult(_, _)) =>
          ResultsFileJsonFormat
            .readDockerExecutionResultFile(resultFile, flaskCorrectionResultFileReads)
            .fold(
              exception => onError("Error while reading result!", maxPoints, Some(exception)),
              flaskCorrectionResultFileContent => {
                val testResults = flaskCorrectionResultFileContent.results

                val points = testResults
                  .filter(_.successful)
                  .map { tr =>
                    exercise.content.testConfig.tests
                      .find(_.id == tr.testId)
                      .map(_.maxPoints)
                      .getOrElse(0)
                  }
                  .sum
                  .points

                FlaskResult(testResults, points, maxPoints)
              }
            )
      }

  }

}
