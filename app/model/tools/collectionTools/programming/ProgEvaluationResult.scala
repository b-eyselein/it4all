package model.tools.collectionTools.programming

import model.core.result.{AbstractCorrectionResult, SuccessType}
import model.points._
import play.api.libs.json.JsValue

sealed trait ProgEvalResult {

  def success: SuccessType

}

final case class ProgCompleteResult(
  solutionSaved: Boolean,
  simplifiedResults: Seq[SimplifiedExecutionResult] = Seq.empty,
  normalResult: Option[NormalExecutionResult] = None,
  unitTestResults: Seq[UnitTestCorrectionResult] = Seq.empty
) extends AbstractCorrectionResult {

  private def results: Seq[ProgEvalResult] = simplifiedResults ++ unitTestResults

  override val points: Points = results.count(_.success == SuccessType.COMPLETE).points

  override val maxPoints: Points = results.length.points

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
