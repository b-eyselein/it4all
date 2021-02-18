package model.tools.rose

import model.points._
import model.result.AbstractCorrectionResult

final case class RoseResult(
  result: RoseExecutionResult,
  points: Points = (-1).points,
  maxPoints: Points = (-1).points
) extends AbstractCorrectionResult {

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
