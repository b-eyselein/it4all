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

  override val points: Points = -1 points

  override val maxPoints: Points = -1 points

}

// Single results

sealed trait ProgEvalResult extends EvaluationResult

final case class SyntaxError(error: String) extends ProgEvalResult {

  override val success: SuccessType = SuccessType.ERROR

}

final case class ExecutionResult(success: SuccessType, id: Int, input: JsValue, awaited: JsValue, result: JsValue, consoleOutput: Option[String]) extends ProgEvalResult

object ProgCompleteResultJsonProtocol extends CompleteResultJsonProtocol[ProgEvalResult, ProgCompleteResult] {

  private val executionResultWrites: Writes[ExecutionResult] = (
    (__ \ idName).write[Int] and
      (__ \ successTypeName).write[String] and
      (__ \ correctName).write[Boolean] and
      (__ \ inputName).write[JsValue] and
      (__ \ awaitedName).write[JsValue] and
      (__ \ gottenName).write[JsValue] and
      (__ \ consoleOutputName).write[Option[String]]
    ) (er => (er.id, er.success.entryName, er.isSuccessful, er.input, er.awaited, er.result, er.consoleOutput))

  private implicit val progEvalResultWrites: Writes[ProgEvalResult] = {
    case er: ExecutionResult   => executionResultWrites.writes(er)
    case SyntaxError(errorMsg) => JsString(errorMsg)
  }

  override def completeResultWrites(solutionSaved: Boolean): Writes[ProgCompleteResult] = (
    (__ \ solutionSavedName).write[Boolean] and
      (__ \ resultsName).write[Seq[ProgEvalResult]]
    ) (pcr => (solutionSaved, pcr.results))

}
