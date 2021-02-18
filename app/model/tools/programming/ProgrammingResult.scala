package model.tools.programming

import model.points._
import model.result.{AbstractCorrectionResult, SuccessType}
import play.api.libs.json.JsValue

final case class ProgrammingResult(
  proficienciesUpdated: Option[Boolean] = None,
  simplifiedResults: Seq[SimplifiedExecutionResult] = Seq.empty,
  normalResult: Option[NormalExecutionResult] = None,
  unitTestResults: Seq[UnitTestCorrectionResult] = Seq.empty,
  points: Points,
  maxPoints: Points
) extends AbstractCorrectionResult {

  override def isCompletelyCorrect: Boolean = {

    val simplifiedResultOk = simplifiedResults.isEmpty || simplifiedResults.forall(_.success == SuccessType.COMPLETE)

    val normalResultOk = normalResult.isEmpty || normalResult.forall(_.successful)

    val unitTestResultsOk = unitTestResults.isEmpty || unitTestResults.forall(_.successful)

    simplifiedResultOk && normalResultOk && unitTestResultsOk
  }

}

// Simplified results

final case class SimplifiedExecutionResult(
  testId: Int,
  testInput: JsValue,
  awaited: JsValue,
  gotten: JsValue,
  success: SuccessType,
  stdout: Option[String]
)

// Normal test results

final case class NormalExecutionResult(
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

final case class UnitTestCorrectionResultFileContent(
  results: Seq[UnitTestCorrectionResult]
)
