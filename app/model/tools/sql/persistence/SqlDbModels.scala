package model.tools.sql.persistence

import model._
import model.persistence._
import model.tools.sql._

object SqlDbModels extends ADbModels[SqlExercise, DbSqlExercise] {

  override def dbExerciseFromExercise(collId: Int, ex: SqlExercise): DbSqlExercise = {
    val tagsAsString = ex.tags.map(_.entryName).mkString(SqlConsts.tagJoinChar)

    DbSqlExercise(ex.id, ex.semanticVersion, collId, ex.title, ex.author, ex.text, ex.state, ex.exerciseType, tagsAsString, ex.hint)
  }

  def exerciseFromDbValues(dbEx: DbSqlExercise, samples: Seq[StringSampleSolution]): SqlExercise = {
    val tagsFromString: Seq[SqlExTag] = dbEx.tags.split(SqlConsts.tagJoinChar).toSeq.flatMap(SqlExTag.withNameInsensitiveOption)

    SqlExercise(dbEx.id, dbEx.semanticVersion, dbEx.title, dbEx.author, dbEx.text, dbEx.state, dbEx.exerciseType, tagsFromString, dbEx.hint, samples)
  }

}

object SqlExerciseReviewDbModels extends AExerciseReviewDbModels[SqlExPart, SqlExerciseReview, DbSqlExerciseReview] {

  override def dbReviewFromReview(username: String, collId: Int, exId: Int, part: SqlExPart, review: SqlExerciseReview): DbSqlExerciseReview =
    DbSqlExerciseReview(username, collId, exId, part, review.difficulty, review.maybeDuration)

  override def reviewFromDbReview(dbReview: DbSqlExerciseReview): SqlExerciseReview =
    SqlExerciseReview(dbReview.difficulty, dbReview.maybeDuration)

}

final case class DbSqlExercise(id: Int, semanticVersion: SemanticVersion, collectionId: Int, title: String, author: String, text: String, state: ExerciseState,
                               exerciseType: SqlExerciseType, tags: String, hint: Option[String]) extends ADbExercise

// Exercise review

final case class DbSqlExerciseReview(username: String, collId: Int, exerciseId: Int, exercisePart: SqlExPart,
                                     difficulty: Difficulty, maybeDuration: Option[Int]) extends DbExerciseReview[SqlExPart]
