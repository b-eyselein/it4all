package model.tools.programming

import better.files._
import model.ExerciseFile
import model.core.ScalaDockerImage
import model.points._
import model.tools.DockerExecutionCorrector

import scala.util.matching.Regex

trait ProgrammingAbstractCorrector extends DockerExecutionCorrector {

  override type AbstractResult = ProgrammingAbstractResult

  override protected def buildInternalError(msg: String, maxPoints: Points): ProgrammingInternalErrorResult =
    ProgrammingInternalErrorResult(msg, maxPoints)

  val programmingCorrectionDockerImage: ScalaDockerImage = ScalaDockerImage("ls6uniwue", "py_prog_corrector", "0.1.3")

  protected val testDataFileName = "test_data.json"
  protected val testMainFileName = "test_main.py"

  protected val implFileRegex: Regex = """.*_\d*\.py""".r

  protected def writeExerciseFileToDirectory(ef: ExerciseFile, targetDir: File): File = {
    val targetPath = targetDir / ef.name

    targetPath
      .createIfNotExists(createParents = true)
      .write(ef.content)
  }

  protected def maxPoints(utp: NormalUnitTestPart): Points = utp.unitTestTestConfigs.size.points

  protected def maxPoints(utp: SimplifiedUnitTestPart): Points = utp.sampleTestData.size.points

}
