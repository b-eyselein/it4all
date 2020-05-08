package model.core.result

import model.points._

trait AbstractCorrectionResult {

  def solutionSaved: Boolean

  def points: Points

  def maxPoints: Points

  def isCompletelyCorrect: Boolean

}

trait InternalErrorResult {
  self: AbstractCorrectionResult =>

  val msg: String

  override final def points: Points = zeroPoints

  override def isCompletelyCorrect: Boolean = false

}
