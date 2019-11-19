package model.tools.sql.persistence

import model._
import model.persistence._
import model.tools.sql._

object SqlDbModels extends ADbModels[SqlExercise, SqlExercise] {

  override def dbExerciseFromExercise(ex: SqlExercise): SqlExercise = ex

}

object SqlExerciseReviewDbModels extends AExerciseReviewDbModels[SqlExPart, SqlExerciseReview, DbSqlExerciseReview] {

  override def dbReviewFromReview(username: String, collId: Int, exId: Int, part: SqlExPart, review: SqlExerciseReview): DbSqlExerciseReview =
    DbSqlExerciseReview(username, collId, exId, part, review.difficulty, review.maybeDuration)

  override def reviewFromDbReview(dbReview: DbSqlExerciseReview): SqlExerciseReview =
    SqlExerciseReview(dbReview.difficulty, dbReview.maybeDuration)

}

// Exercise review

final case class DbSqlExerciseReview(
  username: String, collId: Int, exerciseId: Int, exercisePart: SqlExPart,
  difficulty: Difficulty, maybeDuration: Option[Int]
) extends DbExerciseReview[SqlExPart]
