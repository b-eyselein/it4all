package model.result

import model.points._
import model.{Exercise, ExerciseContent}

final case class CorrectionResult[R <: AbstractCorrectionResult](
  solutionSaved: Boolean,
  resultSaved: Boolean,
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

object BasicExercisePartResult {

  def forExerciseAndResult(
    username: String,
    exercise: Exercise[_ <: ExerciseContent],
    partId: String,
    result: AbstractCorrectionResult
  ): BasicExercisePartResult = BasicExercisePartResult(
    username,
    exercise.exerciseId,
    exercise.collectionId,
    exercise.toolId,
    partId,
    result.points,
    result.maxPoints
  )

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
