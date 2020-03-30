package model.core.result

import model.points.Points

trait AbstractCorrectionResult {

  def solutionSaved: Boolean

  def points: Points //= -1 point

  def maxPoints: Points // = -1 point

}
