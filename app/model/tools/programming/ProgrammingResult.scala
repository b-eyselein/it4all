package model.tools.programming

import model.points._
import model.result.AbstractCorrectionResult

final case class ProgrammingResult(
  proficienciesUpdated: Option[Boolean] = None,
  implementationCorrectionResult: Option[ImplementationCorrectionResult] = None,
  unitTestResults: Seq[UnitTestCorrectionResult] = Seq.empty,
  points: Points,
  maxPoints: Points
) extends AbstractCorrectionResult {

  override def isCompletelyCorrect: Boolean = {

    val normalResultOk = implementationCorrectionResult.isEmpty || implementationCorrectionResult.forall(_.successful)

    val unitTestResultsOk = unitTestResults.isEmpty || unitTestResults.forall(_.successful)

    normalResultOk && unitTestResultsOk
  }

}

// Normal test results

final case class ImplementationCorrectionResult(
  successful: Boolean,
  stdout: Seq[String],
  stderr: Seq[String]
)

// Unit Test Correction

final case class UnitTestCorrectionResult(
  testId: Int,
  description: String,
  successful: Boolean,
  shouldFail: Boolean,
  stdout: Seq[String],
  stderr: Seq[String]
)
