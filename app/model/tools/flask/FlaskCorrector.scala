package model.tools.flask

import better.files.File
import model.core._
import model.points._
import model.tools.DockerExecutionCorrector
import model.tools.flask.FlaskTool.FlaskExercise
import model.tools.flask.FlaskToolJsonProtocol.{flaskTestResultReads, flaskTestsConfigFormat}
import model.{ContentExerciseFile, IFilesSolution}
import play.api.libs.json.{Json, Reads}

import scala.concurrent.{ExecutionContext, Future}

object FlaskCorrector extends DockerExecutionCorrector {

  val flaskCorrectionDockerImage: ScalaDockerImage = ScalaDockerImage("ls6uniwue", "flask_tester", "0.0.4")

  override protected val dockerImage: ScalaDockerImage = flaskCorrectionDockerImage

  private def convertResult(exercise: FlaskExercise, testResults: Seq[FlaskTestResult]): FlaskResult = {
    println(testResults)

    FlaskResult(
      testResults,
      points = testResults
        .filter(_.successful)
        .map { tr =>
          exercise.content.testConfig.tests
            .find { _.id == tr.testId }
            .map { _.maxPoints }
            .getOrElse(0)
        }
        .sum
        .points,
      maxPoints = exercise.content.maxPoints.points
    )
  }

  def correct(
    solution: IFilesSolution,
    exercise: FlaskExercise,
    solTargetDir: File
  )(implicit executionContext: ExecutionContext): Future[FlaskResult] = {

    // Write solution files
    val appUnderTestTargetDir = solTargetDir / "app"
    solution.files.foreach(_.writeOrCopyToDirectory(appUnderTestTargetDir))

    // Write helper files
    val resultFile = solTargetDir / resultFileName
    resultFile
      .createIfNotExists(createParents = true)
      .clear()

    // Write test config
    val testConfigExerciseFile = ContentExerciseFile(
      testConfigFileName,
      content = Json.stringify(
        flaskTestsConfigFormat.writes(exercise.content.testConfig)
      ),
      editable = false
    )
    val testConfigFile = testConfigExerciseFile.writeOrCopyToDirectory(solTargetDir)

    // write test files
    val testFileBinds = exercise.content.testFiles.map { f =>
      DockerBind(fromPath = f.writeOrCopyToDirectory(solTargetDir), toPath = baseBindPath / f.name, isReadOnly = true)
    }

    // Create mounts

    val dockerBinds = Seq(
      DockerBind(fromPath = appUnderTestTargetDir, toPath = baseBindPath / "app", isReadOnly = true),
      DockerBind(fromPath = testConfigFile, toPath = baseBindPath / testConfigFileName, isReadOnly = true),
      DockerBind(fromPath = resultFile, toPath = baseBindPath / resultFileName)
    ) ++ testFileBinds

    runContainer(
      dockerBinds,
      Reads.seq(flaskTestResultReads),
      resultFile,
      convertResult = convertResult(exercise, _)
    )

  }

}
