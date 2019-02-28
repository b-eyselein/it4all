package model.tools.sql.persistence

import model.persistence._
import model.tools.sql._
import model.{Difficulty, ExerciseState, HasBaseValues, Points, SemanticVersion}

object SqlDbModels extends ADbModels[SqlExercise, DbSqlExercise, SqlSampleSolution, DbSqlSampleSolution, SqlUserSolution, DbSqlUserSolution] {

  override def dbExerciseFromExercise(collId: Int, ex: SqlExercise): DbSqlExercise = {
    val tagsAsString = ex.tags.map(_.entryName).mkString(SqlConsts.tagJoinChar)

    DbSqlExercise(ex.id, ex.semanticVersion, ex.title, ex.author, ex.text, ex.state, collId, ex.exerciseType, tagsAsString, ex.hint)
  }

  def exerciseFromDbValues(dbEx: DbSqlExercise, samples: Seq[SqlSampleSolution]): SqlExercise = {
    val tagsFromString: Seq[SqlExTag] = dbEx.tags.split(SqlConsts.tagJoinChar).toSeq.flatMap(SqlExTag.withNameInsensitiveOption)

    SqlExercise(dbEx.id, dbEx.semanticVersion, dbEx.title, dbEx.author, dbEx.text, dbEx.state, dbEx.exerciseType, tagsFromString, dbEx.hint, samples)
  }

  override def dbSampleSolFromSampleSol(exId: Int, exSemVer: SemanticVersion, collId: Int, sample: SqlSampleSolution): DbSqlSampleSolution =
    DbSqlSampleSolution(sample.id, exId, exSemVer, collId, sample.sample)

  override def sampleSolFromDbSampleSol(dbSample: DbSqlSampleSolution): SqlSampleSolution =
    SqlSampleSolution(dbSample.id, dbSample.sample)

  override def dbUserSolFromUserSol(exId: Int, exSemVer: SemanticVersion, collId: Int, username: String, solution: SqlUserSolution): DbSqlUserSolution =
    DbSqlUserSolution(solution.id, exId, exSemVer, collId, username, solution.part, solution.solution, solution.points, solution.maxPoints)

  override def userSolFromDbUserSol(dbSolution: DbSqlUserSolution): SqlUserSolution =
    SqlUserSolution(dbSolution.id, dbSolution.part, dbSolution.solution, dbSolution.points, dbSolution.maxPoints)

}

object SqlExerciseReviewDbModels extends AExerciseReviewDbModels[SqlExPart, SqlExerciseReview, DbSqlExerciseReview] {

  override def dbReviewFromReview(username: String, collId: Int, exId: Int, part: SqlExPart, review: SqlExerciseReview): DbSqlExerciseReview =
    DbSqlExerciseReview(username, collId, exId, part, review.difficulty, review.maybeDuration)

  override def reviewFromDbReview(dbReview: DbSqlExerciseReview): SqlExerciseReview =
    SqlExerciseReview(dbReview.difficulty, dbReview.maybeDuration)

}

final case class DbSqlExercise(id: Int, semanticVersion: SemanticVersion, title: String, author: String, text: String, state: ExerciseState,
                               collectionId: Int, exerciseType: SqlExerciseType, tags: String, hint: Option[String]) extends HasBaseValues

final case class DbSqlSampleSolution(id: Int, exId: Int, exSemVer: SemanticVersion, collId: Int, sample: String)
  extends ADbSampleSol[String]

final case class DbSqlUserSolution(id: Int, exId: Int, exSemVer: SemanticVersion, collId: Int, username: String,
                                   part: SqlExPart, solution: String, points: Points, maxPoints: Points)
  extends ADbUserSol[String]


// Exercise review

final case class DbSqlExerciseReview(username: String, collId: Int, exerciseId: Int, exercisePart: SqlExPart,
                                     difficulty: Difficulty, maybeDuration: Option[Int]) extends DbExerciseReview[SqlExPart]
