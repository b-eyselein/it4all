package model.core.result

import model.points.Points
import play.api.libs.json.Writes


object EvaluationResult {

  def allResultsSuccessful[T <: EvaluationResult](results: Seq[T]): Boolean = results.nonEmpty && results.forall(_.success == SuccessType.COMPLETE)

}

trait EvaluationResult {

  def success: SuccessType

}

trait CompleteResult[E <: EvaluationResult] extends EvaluationResult {

  def solutionSaved: Boolean

  def points: Points //= -1 point

  def maxPoints: Points // = -1 point

  def results: Seq[E]


  override def success: SuccessType = SuccessType.ofBool(EvaluationResult.allResultsSuccessful(results))

}

trait CompleteResultJsonProtocol[E <: EvaluationResult, CR <: CompleteResult[E]] {

  val completeResultWrites: Writes[CR]

}
