package model.tools.programming

import model.core.result.{CompleteResult, EvaluationResult, SuccessType}
import model.points._
import play.api.libs.json.JsValue

// Types of complete results


sealed trait ProgEvalResult extends EvaluationResult

final case class ProgCompleteResult(implResults: Seq[ExecutionResult], unitTestResults: Seq[UnitTestCorrectionResult], solutionSaved: Boolean = false)
  extends CompleteResult[ProgEvalResult] {

  //  override type SolType = String

  override def results: Seq[ProgEvalResult] = implResults

  override val points: Points = results.count(_.isSuccessful).points

  override val maxPoints: Points = results.length.points

}

// Single results

final case class ExecutionResult(success: SuccessType, id: Int, input: JsValue, awaited: JsValue, gotten: JsValue, stdout: Option[String])
  extends ProgEvalResult

// Written from docker container in result.json:

// Implementation tests

final case class ResultFileContent(resultType: String, results: Seq[ExecutionResult], errors: String)

// Unit Test Correction

final case class UnitTestTestConfig(id: Int, shouldFail: Boolean, cause: Option[String], description: String)

final case class UnitTestCorrectionResult(testConfig: UnitTestTestConfig, successful: Boolean, file: String, status: Int,
                                          stdout: Seq[String], stderr: Seq[String]) extends ProgEvalResult {

  override def success: SuccessType = ???

}

final case class UnitTestCorrectionResultFileContent(results: Seq[UnitTestCorrectionResult])
