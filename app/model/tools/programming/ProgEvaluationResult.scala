package model.tools.programming

import model.points._
import model.result.{AbstractCorrectionResult, InternalErrorResult, SuccessType}
import play.api.libs.json.JsValue

sealed trait ProgEvalResult

trait ProgrammingAbstractResult extends AbstractCorrectionResult

final case class ProgrammingInternalErrorResult(
  msg: String,
  maxPoints: Points
) extends ProgrammingAbstractResult
    with InternalErrorResult

final case class ProgrammingResult(
  proficienciesUpdated: Option[Boolean] = None,
  simplifiedResults: Seq[SimplifiedExecutionResult] = Seq.empty,
  normalResult: Option[NormalExecutionResult] = None,
  unitTestResults: Seq[UnitTestCorrectionResult] = Seq.empty,
  points: Points,
  maxPoints: Points
) extends ProgrammingAbstractResult {

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
) extends ProgEvalResult

final case class SimplifiedExecutionResultFileContent(
  results: Seq[SimplifiedExecutionResult]
)

// Normal test results

final case class NormalExecutionResult(
  successful: Boolean,
  logs: String
) extends ProgEvalResult

// Unit Test Correction

final case class UnitTestCorrectionResult(
  testId: Int,
  description: String,
  successful: Boolean,
  stdout: Seq[String],
  stderr: Seq[String]
) extends ProgEvalResult

final case class UnitTestCorrectionResultFileContent(
  results: Seq[UnitTestCorrectionResult]
)
