package model.rose

import model.core.result.{CompleteResult, CompleteResultJsonProtocol, EvaluationResult, SuccessType}
import play.api.libs.json.Writes

final case class RoseCompleteResult(learnerSolution: String, result: RoseEvalResult) extends CompleteResult[RoseEvalResult] {

  override type SolType = String

  override def results: Seq[RoseEvalResult] = Seq(result)

}


sealed trait RoseEvalResult extends EvaluationResult

case object RoseTimeOutResult extends RoseEvalResult {

  override def success: SuccessType = SuccessType.ERROR

}

final case class RoseSyntaxErrorResult(cause: String) extends RoseEvalResult {

  override def success: SuccessType = SuccessType.NONE

}

final case class RoseExecutionResult(result: String) extends RoseEvalResult {

  override def success: SuccessType = ???

}

case object RoseEvalFailed extends RoseEvalResult {

  override def success: SuccessType = SuccessType.ERROR

}

object RoseCompleteResultJsonProtocol extends CompleteResultJsonProtocol[RoseEvalResult, RoseCompleteResult] {

  override def completeResultWrites(solutionSaved: Boolean): Writes[RoseCompleteResult] = ???

}