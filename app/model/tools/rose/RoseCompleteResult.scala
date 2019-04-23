package model.tools.rose

import model.core.result.{CompleteResult, CompleteResultJsonProtocol, EvaluationResult, SuccessType}
import model.points._
import play.api.libs.json.Writes

final case class RoseCompleteResult(learnerSolution: String, result: RoseEvalResult) extends CompleteResult[RoseEvalResult] {

  override type SolType = String

  override def results: Seq[RoseEvalResult] = Seq(result)

  override def points: Points = (-1).points

  override def maxPoints: Points = (-1).points

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

