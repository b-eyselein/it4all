package model.result

import model.points._
import model.{ExPart, Exercise, ExerciseContent}

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

object BasicExerciseResult {

  def forExerciseAndResult[P <: ExPart](
    username: String,
    exercise: Exercise[_ <: ExerciseContent],
    part: P,
    result: AbstractCorrectionResult
  ): BasicExerciseResult[P] =
    BasicExerciseResult(
      username,
      exercise.exerciseId,
      exercise.collectionId,
      exercise.toolId,
      part,
      result.points,
      result.maxPoints,
      result.isCompletelyCorrect
    )

}

final case class BasicExerciseResult[P <: ExPart](
  username: String,
  exerciseId: Int,
  collectionId: Int,
  toolId: String,
  part: P,
  points: Points,
  maxPoints: Points,
  isCorrect: Boolean
)
