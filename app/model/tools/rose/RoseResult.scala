package model.tools.rose

import model.points._
import model.result.{AbstractCorrectionResult, InternalErrorResult}

sealed trait RoseAbstractResult extends AbstractCorrectionResult[RoseAbstractResult]

final case class RoseInternalErrorResult(
  msg: String,
  maxPoints: Points,
  solutionSaved: Boolean = false
) extends RoseAbstractResult
    with InternalErrorResult[RoseAbstractResult] {

  override def updateSolutionSaved(solutionSaved: Boolean): RoseAbstractResult =
    this.copy(solutionSaved = solutionSaved)

}

final case class RoseResult(
  result: RoseExecutionResult,
  points: Points = (-1).points,
  maxPoints: Points = (-1).points,
  solutionSaved: Boolean = false
) extends RoseAbstractResult {

  override def isCompletelyCorrect: Boolean = ???

  override def updateSolutionSaved(solutionSaved: Boolean): RoseAbstractResult =
    this.copy(solutionSaved = solutionSaved)

}

final case class RoseStart(x: Int, y: Int)

final case class RobotResult(name: String, actions_size: Int, actions: Seq[String])

final case class RoseExecutionResult(
  correct: Boolean,
  start: RoseStart,
  sample: RobotResult,
  user: RobotResult
)
