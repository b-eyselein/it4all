package model.result

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

final case class BasicExercisePartResult(
  username: String,
  exerciseId: Int,
  collectionId: Int,
  toolId: String,
  partId: String,
  points: Points,
  maxPoints: Points
) {

  def isCorrect: Boolean = points.quarters == maxPoints.quarters

}
