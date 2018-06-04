package model.rose

import model.core.result.{CompleteResult, EvaluationResult, SuccessType}
import play.api.libs.json.{JsString, JsValue, Json}
import play.twirl.api.Html

case class RoseCompleteResult(solutionSaved: Boolean, learnerSolution: String, result: RoseEvalResult) extends CompleteResult[RoseEvalResult] {

  override type SolType = String

  override def results: Seq[RoseEvalResult] = Seq(result)

  def render: JsValue = result match {
    case rer: RoseExecutionResult => Json.parse(rer.result)
    case _                        => JsString("ERROR!")
  }

}


sealed trait RoseEvalResult extends EvaluationResult

case object RoseTimeOutResult extends RoseEvalResult {

  override def success: SuccessType = SuccessType.ERROR

}

case class RoseSyntaxErrorResult(cause: String) extends RoseEvalResult {

  override def success: SuccessType = SuccessType.NONE

}

case class RoseExecutionResult(result: String) extends RoseEvalResult {

  override def success: SuccessType = ???

}

case object RoseEvalFailed extends RoseEvalResult {

  override def success: SuccessType = SuccessType.ERROR

}