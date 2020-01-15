package model.tools.collectionTools.rose

import model.core.result.{CompleteResult, EvaluationResult, SuccessType}
import model.points._

final case class RoseCompleteResult(
  result: RoseExecutionResult,
  points: Points = (-1).points,
  maxPoints: Points = (-1).points,
  solutionSaved: Boolean
) extends CompleteResult


final case class RoseStart(x: Int, y: Int)

final case class RobotResult(name: String, actions_size: Int, actions: Seq[String])


final case class RoseExecutionResult(
  correct: Boolean,
  start: RoseStart,
  sample: RobotResult,
  user: RobotResult
) extends EvaluationResult {

  override def success: SuccessType = ???

}

