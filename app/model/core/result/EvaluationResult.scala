package model.core.result

import model.points.Points
import play.api.libs.json.Writes


object EvaluationResult {

  def allResultsSuccessful[T <: EvaluationResult](results: Seq[T]): Boolean = results.nonEmpty && results.forall(_.isSuccessful)

}

trait EvaluationResult {

  def success: SuccessType

  def isSuccessful: Boolean = success == SuccessType.COMPLETE

}

trait CompleteResult[E <: EvaluationResult] extends EvaluationResult {

  type SolType

  def points: Points //= -1 point

  def maxPoints: Points // = -1 point

  def learnerSolution: SolType

  def results: Seq[E]

  override def success: SuccessType = SuccessType.ofBool(EvaluationResult.allResultsSuccessful(results))

}

trait CompleteResultJsonProtocol[E <: EvaluationResult, CR <: CompleteResult[E]] {

  def completeResultWrites(solutionSaved: Boolean): Writes[CR]

}
