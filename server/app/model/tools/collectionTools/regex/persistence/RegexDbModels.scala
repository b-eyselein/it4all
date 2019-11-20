package model.tools.collectionTools.regex.persistence

import model.Difficulty
import model.persistence._
import model.tools.collectionTools.regex._

object RegexDbModels extends ADbModels[RegexExercise, RegexExercise] {

  override def dbExerciseFromExercise(ex: RegexExercise): RegexExercise = ex

  def exerciseFromDbExercise(ex: RegexExercise): RegexExercise = ex

}

object RegexExerciseReviewDbModels extends AExerciseReviewDbModels[RegexExPart, RegexExerciseReview, DbRegexExerciseReview] {

  override def dbReviewFromReview(username: String, collId: Int, exId: Int, part: RegexExPart, review: RegexExerciseReview): DbRegexExerciseReview =
    DbRegexExerciseReview(username, collId, exId, part, review.difficulty, review.maybeDuration)

  override def reviewFromDbReview(dbReview: DbRegexExerciseReview): RegexExerciseReview =
    RegexExerciseReview(dbReview.difficulty, dbReview.maybeDuration)

}

// Exercise review

final case class DbRegexExerciseReview(
  username: String, collId: Int, exerciseId: Int, exercisePart: RegexExPart, difficulty: Difficulty, maybeDuration: Option[Int]
) extends DbExerciseReview[RegexExPart]
