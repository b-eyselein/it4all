package model.tools.sql.persistence

import model.persistence.DbModels
import model.tools.sql._
import model.{ExerciseState, HasBaseValues, SemanticVersion}

object SqlDbModels extends DbModels {


  override type Exercise = SqlExercise

  override type DbExercise = DbSqlExercise

  override def dbExerciseFromExercise(ex: SqlExercise): DbSqlExercise = {
    val tagsAsString = ex.tags.map(_.entryName).mkString(SqlConsts.tagJoinChar)

    DbSqlExercise(ex.id, ex.semanticVersion, ex.title, ex.author, ex.text, ex.state, ex.collectionId, ex.exerciseType, tagsAsString, ex.hint)
  }


  def exerciseFromDbValues(dbEx: DbSqlExercise, samples: Seq[SqlSample]): SqlExercise = {
    val tagsFromString: Seq[SqlExTag] = dbEx.tags.split(SqlConsts.tagJoinChar).toSeq.flatMap(SqlExTag.withNameInsensitiveOption)

    SqlExercise(dbEx.id, dbEx.semanticVersion, dbEx.title, dbEx.author, dbEx.text, dbEx.state,
      dbEx.collectionId, dbEx.exerciseType, tagsFromString, dbEx.hint, samples)
  }


}

final case class DbSqlExercise(id: Int, semanticVersion: SemanticVersion, title: String, author: String, text: String, state: ExerciseState,
                               collectionId: Int, exerciseType: SqlExerciseType, tags: String, hint: Option[String]) extends HasBaseValues

