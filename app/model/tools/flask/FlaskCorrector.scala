package model.tools.flask

import model.FilesSolution
import model.core._
import model.points._
import model.tools.DockerExecutionCorrector
import model.tools.flask.FlaskTool.{FlaskExercise, solutionDirForExercise}
import model.tools.flask.FlaskToolJsonProtocol.{flaskCorrectionResultReads, flaskTestsConfigFormat}
import play.api.libs.json.{Json, Reads}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

object FlaskCorrector extends DockerExecutionCorrector {

  val flaskCorrectionDockerImage: ScalaDockerImage = ScalaDockerImage("ls6uniwue", "flask_tester", "0.0.4")

  override protected val dockerImage: ScalaDockerImage = flaskCorrectionDockerImage

  def correct(
    username: String,
    solution: FilesSolution,
    exercise: FlaskExercise
  )(implicit executionContext: ExecutionContext): Future[Try[FlaskAbstractResult]] = {

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

    runContainer(
      dockerBinds,
      Reads.seq(flaskCorrectionResultReads),
      resultFile
    )(testResults =>
      FlaskResult(
        testResults,
        points = testResults
          .filter(_.successful)
          .map { tr =>
            exercise.content.testConfig.tests
              .find(_.id == tr.testId)
              .map(_.maxPoints)
              .getOrElse(0)
          }
          .sum
          .points,
        maxPoints = exercise.content.maxPoints.points
      )
    )

  }

}
