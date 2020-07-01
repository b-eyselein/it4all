package model.tools.rose

import model.result.{AbstractCorrectionResult, InternalErrorResult}
import model.points._

sealed trait RoseAbstractResult extends AbstractCorrectionResult

final case class RoseInternalErrorResult(
  msg: String,
  solutionSaved: Boolean,
  maxPoints: Points
) extends RoseAbstractResult
    with InternalErrorResult

final case class RoseResult(
  result: RoseExecutionResult,
  points: Points = (-1).points,
  maxPoints: Points = (-1).points,
  solutionSaved: Boolean
) extends RoseAbstractResult {

  override def isCompletelyCorrect: Boolean = ???

}

final case class RoseStart(x: Int, y: Int)

final case class RobotResult(name: String, actions_size: Int, actions: Seq[String])

final case class RoseExecutionResult(
  correct: Boolean,
  start: RoseStart,
  sample: RobotResult,
  user: RobotResult
)
