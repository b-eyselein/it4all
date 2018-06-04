package model.programming

import model.core.result.SuccessType
import model.core.result.EvaluationResult
import model.core.result.CompleteResult
import model.programming.ProgConsts._
import play.api.libs.json.{JsBoolean, JsString, JsValue, Json}
import play.twirl.api.Html

// Types of complete results

case class ProgCompleteResult(implementation: String, solutionSaved: Boolean, results: Seq[ProgEvalResult]) extends CompleteResult[ProgEvalResult] {

  override type SolType = String

  def toJson: JsValue = Json.obj(
    "solutionSaved" -> solutionSaved,
    "results" -> results.map(_.toJson)
  )

  override def learnerSolution: String = implementation

}

// Single results

trait ProgEvalResult extends EvaluationResult {

  def toJson: JsValue

}

case class SyntaxError(error: String) extends ProgEvalResult {

  override val success: SuccessType = SuccessType.ERROR

  override def toJson: JsValue = JsString(error)


}


case class ExecutionResult(success: SuccessType, id: Int, input: JsValue, awaited: JsValue, result: JsValue, consoleOutput: Option[String]) extends ProgEvalResult {

  override def toJson: JsValue = Json.obj(
    idName -> id,
    successTypeName -> success.entryName,
    correctName -> JsBoolean(success == SuccessType.COMPLETE),
    inputName -> input,
    awaitedName -> awaited,
    gottenName -> result,
    consoleOutputName -> consoleOutput.map(co => if (co.isEmpty) None else consoleOutput)
  )

}