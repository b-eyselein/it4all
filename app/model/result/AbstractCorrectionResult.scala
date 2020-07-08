package model.result

import model.points._

trait AbstractCorrectionResult[R <: AbstractCorrectionResult[R]] {
  self: R =>

  def solutionSaved: Boolean

  def points: Points

  def maxPoints: Points

  def isCompletelyCorrect: Boolean

  def updateSolutionSaved(solutionSaved: Boolean): R

}

trait InternalErrorResult[R <: AbstractCorrectionResult[R]] {
  self: AbstractCorrectionResult[R] =>

  val msg: String

  override final def points: Points = zeroPoints

  override def isCompletelyCorrect: Boolean = false

}
