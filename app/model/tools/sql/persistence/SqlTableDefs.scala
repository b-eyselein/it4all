package model.tools.sql.persistence

import javax.inject.Inject
import model.persistence._
import model.tools.sql.SqlConsts._
import model.tools.sql._
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile
import slick.lifted.ProvenShape

import scala.concurrent.{ExecutionContext, Future}
import scala.language.postfixOps

class SqlTableDefs @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(override implicit val executionContext: ExecutionContext)
  extends HasDatabaseConfigProvider[JdbcProfile]
    with StringSolutionExerciseTableDefs[SqlExPart, SqlExercise, SqlScenario, SqlExerciseReview] {

  import profile.api._

  // Table types

  override protected type DbExType = DbSqlExercise

  override protected type ExTableDef = SqlExercisesTable

  override protected type CollTableDef = SqlScenarioesTable


  override protected type DbSampleSolTable = SqlSampleSolutionsTable

  override protected type DbUserSolTable = SqlUserSolutionsTable


  override protected type DbReviewType = DbSqlExerciseReview

  override protected type ReviewsTable = SqlExerciseReviewsTable

  // Table queries

  override protected val exTable  : TableQuery[SqlExercisesTable]  = TableQuery[SqlExercisesTable]
  override protected val collTable: TableQuery[SqlScenarioesTable] = TableQuery[SqlScenarioesTable]

  override protected val sampleSolutionsTableQuery                                  = TableQuery[SqlSampleSolutionsTable]
  override protected val userSolutionsTableQuery: TableQuery[SqlUserSolutionsTable] = TableQuery[SqlUserSolutionsTable]

  override protected val reviewsTable: TableQuery[SqlExerciseReviewsTable] = TableQuery[SqlExerciseReviewsTable]

  // Helper methods

  override protected val dbModels               = SqlDbModels
  override protected val exerciseReviewDbModels = SqlExerciseReviewDbModels

  override def exDbValuesFromExercise(collId: Int, compEx: SqlExercise): DbSqlExercise = dbModels.dbExerciseFromExercise(collId, compEx)

  // Reading

  override protected def completeExForEx(collId: Int, ex: DbSqlExercise): Future[SqlExercise] = for {
    samples <- futureSamplesForExercise(collId, ex.id)
  } yield dbModels.exerciseFromDbValues(ex, samples)

  // Saving

  override def saveExerciseRest(collId: Int, compEx: SqlExercise): Future[Boolean] = {
    val dbSamples = compEx.samples map (s => StringSolutionDbModels.dbSampleSolFromSampleSol(compEx.id, compEx.semanticVersion, collId, s))

    for {
      samplesSaved <- saveSeq[DbStringSampleSolution](dbSamples, s => db.run(sampleSolutionsTableQuery insertOrUpdate s))
    } yield samplesSaved
  }

  // Column types

  override protected implicit val partTypeColumnType: BaseColumnType[SqlExPart] =
    MappedColumnType.base[SqlExPart, String](_.entryName, SqlExParts.withNameInsensitive)

  private implicit val sqlExTypeColumnType: BaseColumnType[SqlExerciseType] =
    MappedColumnType.base[SqlExerciseType, String](_.entryName, str => SqlExerciseType.withNameInsensitiveOption(str) getOrElse SqlExerciseType.SELECT)

  // Tables

  class SqlScenarioesTable(tag: Tag) extends ExerciseCollectionTable(tag, "sql_scenarioes") {

    override def * : ProvenShape[SqlScenario] = (id, title, author, text, state, shortName) <> (SqlScenario.tupled, SqlScenario.unapply)

  }

  class SqlExercisesTable(tag: Tag) extends ExerciseInCollectionTable(tag, "sql_exercises") {

    def exerciseType: Rep[SqlExerciseType] = column[SqlExerciseType]("exercise_type")

    def hint: Rep[String] = column[String](hintName)

    def tags: Rep[String] = column[String](tagsName)


    override def * : ProvenShape[DbSqlExercise] = (id, semanticVersion, collectionId, title, author, text, state, exerciseType, tags, hint.?) <> (DbSqlExercise.tupled, DbSqlExercise.unapply)

  }


  class SqlSampleSolutionsTable(tag: Tag) extends AStringSampleSolutionsTable(tag, "sql_samples")

  class SqlUserSolutionsTable(tag: Tag) extends AStringUserSolutionsTable(tag, "sql_solutions")


  class SqlExerciseReviewsTable(tag: Tag) extends ExerciseReviewsTable(tag, "sql_exercise_reviews") {

    override def * : ProvenShape[DbSqlExerciseReview] = (username, collectionId, exerciseId, exercisePart, difficulty, maybeDuration.?) <> (DbSqlExerciseReview.tupled, DbSqlExerciseReview.unapply)

  }

}
