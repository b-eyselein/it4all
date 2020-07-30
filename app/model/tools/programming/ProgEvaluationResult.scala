package model.tools.programming

import model.points._
import model.result.{AbstractCorrectionResult, InternalErrorResult, SuccessType}
import play.api.libs.json.JsValue

sealed trait ProgEvalResult {

  def success: SuccessType

}

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

    val normalResultOk = normalResult.isEmpty || normalResult.forall(_.success == SuccessType.COMPLETE)

    val unitTestResultsOk = unitTestResults.isEmpty || unitTestResults.forall(_.successful)

    simplifiedResultOk && normalResultOk && unitTestResultsOk
  }

}

// Simplified results

final case class SimplifiedExecutionResult(
  success: SuccessType,
  id: Int,
  input: JsValue,
  awaited: JsValue,
  gotten: JsValue,
  stdout: Option[String]
) extends ProgEvalResult

// Normal test results

final case class NormalExecutionResult(
  success: SuccessType,
  logs: String
) extends ProgEvalResult

// Unit Test Correction

final case class UnitTestCorrectionResult(
  testConfig: UnitTestTestConfig,
  successful: Boolean,
  file: String,
  status: Int,
  stdout: Seq[String],
  stderr: Seq[String]
) extends ProgEvalResult {

  override def success: SuccessType = if (successful) SuccessType.COMPLETE else SuccessType.NONE

}

final case class UnitTestCorrectionResultFileContent(results: Seq[UnitTestCorrectionResult])
