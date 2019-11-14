package model.tools.sql.persistence

import model._
import model.persistence._
import model.tools.sql.SqlConsts.tagJoinChar
import model.tools.sql._

object SqlDbModels extends ADbModels[SqlExercise, DbSqlExercise] {

  override def dbExerciseFromExercise(ex: SqlExercise): DbSqlExercise = {
//    val tagsAsString = ex.tags.map(_.entryName).mkString(tagJoinChar)

    DbSqlExercise(ex.id, ex.collId, ex.semanticVersion, ex.title, ex.author, ex.text, ex.state, ex.exerciseType, ex.tags, ex.hint)
  }

  def exerciseFromDbValues(dbEx: DbSqlExercise, samples: Seq[StringSampleSolution]): SqlExercise = {
    //    val tagsFromString: Seq[SqlExerciseTag] = dbEx.tags.split(tagJoinChar).toSeq.flatMap(SqlExerciseTag.withNameInsensitiveOption)

    SqlExercise(dbEx.id, dbEx.collectionId, dbEx.semanticVersion, dbEx.title, dbEx.author, dbEx.text, dbEx.state, dbEx.exerciseType, dbEx.tags, dbEx.hint, samples)
  }

}

object SqlExerciseReviewDbModels extends AExerciseReviewDbModels[SqlExPart, SqlExerciseReview, DbSqlExerciseReview] {

  override def dbReviewFromReview(username: String, collId: Int, exId: Int, part: SqlExPart, review: SqlExerciseReview): DbSqlExerciseReview =
    DbSqlExerciseReview(username, collId, exId, part, review.difficulty, review.maybeDuration)

  override def reviewFromDbReview(dbReview: DbSqlExerciseReview): SqlExerciseReview =
    SqlExerciseReview(dbReview.difficulty, dbReview.maybeDuration)

}

final case class DbSqlExercise(
  id: Int, collectionId: Int, semanticVersion: SemanticVersion, title: String, author: String, text: String, state: ExerciseState,
  exerciseType: SqlExerciseType, tags: Seq[SqlExerciseTag], hint: Option[String]
) extends ADbExercise

// Exercise review

final case class DbSqlExerciseReview(
  username: String, collId: Int, exerciseId: Int, exercisePart: SqlExPart,
  difficulty: Difficulty, maybeDuration: Option[Int]
) extends DbExerciseReview[SqlExPart]
