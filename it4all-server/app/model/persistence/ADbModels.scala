package model.persistence

import model.tools.collectionTools.{ExPart, SemanticVersion}
import model.{Difficulty, ExerciseReview}
import play.api.libs.json.{JsArray, JsValue}


final case class DbUserSolution(
  id: Int,
  exerciseId: Int,
  collectionId: Int,
  toolId: String,
  semanticVersion: SemanticVersion,
  part: ExPart,
  username: String,
  solution: JsValue
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


final case class DbLesson(
  id: Int,
  toolId: String,
  title: String,
  contentJson: JsValue
)


object AExerciseReviewDbModels {

  def dbReviewFromReview(username: String, collId: Int, exId: Int, part: ExPart, review: ExerciseReview): DbExerciseReview = ???

  def reviewFromDbReview(dbReview: DbExerciseReview): ExerciseReview = ???

}
