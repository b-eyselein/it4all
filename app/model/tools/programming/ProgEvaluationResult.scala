package model.tools.programming

import model.core.result.{CompleteResult, EvaluationResult, SuccessType}
import model.points._
import play.api.libs.json.JsValue


sealed trait ProgEvalResult extends EvaluationResult

final case class ProgCompleteResult(
  simplifiedResults: Seq[SimplifiedExecutionResult],
  normalResult: Option[NormalExecutionResult],
  unitTestResults: Seq[UnitTestCorrectionResult],
  solutionSaved: Boolean = false
) extends CompleteResult[ProgEvalResult] {

  //  override type SolType = String

  override def results: Seq[ProgEvalResult] = simplifiedResults ++ unitTestResults

  override val points: Points = results.count(_.isSuccessful).points

  override val maxPoints: Points = results.length.points

}

// Simplified results

final case class SimplifiedResultFileContent(resultType: String, results: Seq[SimplifiedExecutionResult], errors: String)

final case class SimplifiedExecutionResult(success: SuccessType, id: Int, input: JsValue, awaited: JsValue, gotten: JsValue, stdout: Option[String])
  extends ProgEvalResult

// Normal test results

final case class NormalExecutionResult(success: SuccessType, logs: String) extends ProgEvalResult

// Unit Test Correction

final case class UnitTestTestConfig(id: Int, shouldFail: Boolean, cause: Option[String], description: String)

final case class UnitTestCorrectionResult(testConfig: UnitTestTestConfig, successful: Boolean, file: String, status: Int,
                                          stdout: Seq[String], stderr: Seq[String]) extends ProgEvalResult {

  override def success: SuccessType = ???

}

final case class UnitTestCorrectionResultFileContent(results: Seq[UnitTestCorrectionResult])
