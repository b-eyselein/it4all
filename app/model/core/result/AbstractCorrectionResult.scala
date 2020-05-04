package model.core.result

import model.points.Points

trait AbstractCorrectionResult {

  def solutionSaved: Boolean

  def points: Points

  def maxPoints: Points

}

trait InternalErrorResult {

  val msg: String

}
