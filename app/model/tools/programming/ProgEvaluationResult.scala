package model.tools.programming

import model.points._
import model.result.{AbstractCorrectionResult, InternalErrorResult, SuccessType}
import play.api.libs.json.JsValue

sealed trait ProgEvalResult {

  def success: SuccessType

}

trait ProgrammingAbstractResult extends AbstractCorrectionResult[ProgrammingAbstractResult]

final case class ProgrammingInternalErrorResult(
  msg: String,
  maxPoints: Points,
  solutionSaved: Boolean = false
) extends ProgrammingAbstractResult
    with InternalErrorResult[ProgrammingAbstractResult] {

  override def updateSolutionSaved(solutionSaved: Boolean): ProgrammingAbstractResult =
    this.copy(solutionSaved = solutionSaved)

}

final case class ProgrammingResult(
  solutionSaved: Boolean = false,
  simplifiedResults: Seq[SimplifiedExecutionResult] = Seq.empty,
  normalResult: Option[NormalExecutionResult] = None,
  unitTestResults: Seq[UnitTestCorrectionResult] = Seq.empty
) extends ProgrammingAbstractResult {

  private def results: Seq[ProgEvalResult] = simplifiedResults ++ unitTestResults

  override val points: Points = results.count(_.success == SuccessType.COMPLETE).points

  override val maxPoints: Points = results.length.points

  override def isCompletelyCorrect: Boolean = {

    val simplifiedResultOk = simplifiedResults.forall(_.success == SuccessType.COMPLETE)

    val normalResultOk = normalResult.forall(_.success == SuccessType.COMPLETE)

    val unitTestResultsOk = unitTestResults.forall(_.successful)

    simplifiedResultOk && normalResultOk && unitTestResultsOk
  }

  override def updateSolutionSaved(solutionSaved: Boolean): ProgrammingAbstractResult =
    this.copy(solutionSaved = solutionSaved)

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
