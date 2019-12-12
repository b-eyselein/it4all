package model.core.result

import model.points.Points


trait EvaluationResult {

  def success: SuccessType

}


trait CompleteResult[E <: EvaluationResult] extends EvaluationResult {

  def solutionSaved: Boolean

  def points: Points //= -1 point

  def maxPoints: Points // = -1 point

  def results: Seq[E]


  override def success: SuccessType = ??? //  SuccessType.ofBool(EvaluationResult.allResultsSuccessful(results))

}
