package model.result

import model.points._

final case class CorrectionResult[R <: AbstractCorrectionResult](
  solutionSaved: Boolean,
  proficienciesUpdated: Option[Boolean],
  result: R
)

trait AbstractCorrectionResult {

  def points: Points

  def maxPoints: Points

  def isCompletelyCorrect: Boolean

}

trait InternalErrorResult extends AbstractCorrectionResult {

  val msg: String

  override final def points: Points = zeroPoints

  override def isCompletelyCorrect: Boolean = false

}
