package model

import model.points._

final case class CorrectionResult[R <: AbstractCorrectionResult](
  result: R,
  solutionId: Int,
  proficienciesUpdated: Option[Boolean]
)

trait AbstractCorrectionResult {

  def points: Points

  def maxPoints: Points

  def isCompletelyCorrect: Boolean

}
