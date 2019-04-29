package model.tools.programming

import model.core.result.{CompleteResult, CompleteResultJsonProtocol, EvaluationResult, SuccessType}
import model.tools.programming.ProgConsts._
import play.api.libs.functional.syntax._
import play.api.libs.json._
import model.points._

// Types of complete results

final case class ProgCompleteResult(implementation: String, results: Seq[ProgEvalResult]) extends CompleteResult[ProgEvalResult] {

  override type SolType = String

  override def learnerSolution: String = implementation

  override val points: Points = (-1).points

  override val maxPoints: Points = (-1).points

}

// Single results

sealed trait ProgEvalResult extends EvaluationResult

final case class SyntaxError(error: String) extends ProgEvalResult {

  override val success: SuccessType = SuccessType.ERROR

}

// Written from docker container in result.json:

final case class ExecutionResult(success: SuccessType, id: Int, input: JsValue, awaited: JsValue, result: JsValue, consoleOutput: Option[String]) extends ProgEvalResult

final case class ResultFileContent(resultType: String, results: Seq[ExecutionResult], errors: String)
