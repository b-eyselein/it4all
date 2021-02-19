package model.tools.programming

import model.core.ScalaDockerImage
import model.points._
import model.tools.DockerExecutionCorrector

import scala.util.matching.Regex

trait ProgrammingAbstractCorrector extends DockerExecutionCorrector {

  val programmingCorrectionDockerImage: ScalaDockerImage = ScalaDockerImage("ls6uniwue", "py_prog_corrector", "0.3.0")

  override protected val dockerImage: ScalaDockerImage = programmingCorrectionDockerImage

  protected val testDataFileName = "test_data.json"
  protected val testMainFileName = "simplified_test_main.py"

  protected val implFileRegex: Regex = """.*_\d*\.py""".r

  protected def maxPoints(utp: UnitTestPart): Points = utp.unitTestTestConfigs.size.points

}
