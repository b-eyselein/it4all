package model.tools.programming

import model.AbstractCorrectionResult
import model.points._

final case class ProgrammingResult(
  proficienciesUpdated: Option[Boolean] = None,
  implementationCorrectionResult: Option[ImplementationCorrectionResult] = None,
  unitTestResults: Seq[UnitTestCorrectionResult] = Seq.empty,
  points: Points,
  maxPoints: Points
) extends AbstractCorrectionResult {

  private def allResults: Seq[ProgrammingTestCorrectionResult] = unitTestResults ++ implementationCorrectionResult

  override def isCompletelyCorrect: Boolean = allResults.isEmpty || allResults.forall(_.successful)

}

sealed trait ProgrammingTestCorrectionResult {
  val testSuccessful: Boolean
  val stdout: Seq[String]
  val stderr: Seq[String]

  def successful: Boolean
}

// Normal test results

final case class ImplementationCorrectionResult(
  testSuccessful: Boolean,
  stdout: Seq[String],
  stderr: Seq[String]
) extends ProgrammingTestCorrectionResult {

  override def successful: Boolean = testSuccessful

}

// Unit Test Correction

final case class UnitTestCorrectionResult(
  testId: Int,
  description: String,
  shouldFail: Boolean,
  testSuccessful: Boolean,
  stdout: Seq[String],
  stderr: Seq[String]
) extends ProgrammingTestCorrectionResult {

  override def successful: Boolean = shouldFail != testSuccessful

}
