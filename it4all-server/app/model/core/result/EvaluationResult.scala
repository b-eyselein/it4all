package model.core.result

import model.points.Points


trait EvaluationResult {

  def success: SuccessType

}


trait CompleteResult {

  def solutionSaved: Boolean

  def points: Points //= -1 point

  def maxPoints: Points // = -1 point

}
