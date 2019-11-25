package model.persistence

import model._
import model.tools.collectionTools.ExPart
import play.api.libs.json.JsValue


final case class DbUserSolution(
  id: Int, exerciseId: Int, collectionId: Int, toolId: String, semanticVersion: SemanticVersion, part: ExPart,
  username: String, solution: JsValue
)

final case class DbExerciseReview(
  exerciseId: Int,
  collectionId: Int,
  toolId: String,
  semanticVersion: SemanticVersion,
  part: ExPart,
  username: String,
  difficulty: Difficulty,
  maybeDuration: Option[Int]
)


object AExerciseReviewDbModels {

  def dbReviewFromReview(username: String, collId: Int, exId: Int, part: ExPart, review: ExerciseReview): DbExerciseReview = ???

  def reviewFromDbReview(dbReview: DbExerciseReview): ExerciseReview = ???

}
