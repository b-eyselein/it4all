package model.tools.sql.persistence

import javax.inject.Inject
import model.{ExParts, StringSampleSolution}
import model.persistence._
import model.tools.sql.SqlConsts._
import model.tools.sql._
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile
import slick.lifted.ProvenShape

import scala.concurrent.{ExecutionContext, Future}

class SqlTableDefs @Inject()(override protected val dbConfigProvider: DatabaseConfigProvider)(override implicit val executionContext: ExecutionContext)
  extends HasDatabaseConfigProvider[JdbcProfile]
    with StringSolutionExerciseTableDefs[SqlExPart, SqlExercise, SqlExerciseReview] {

  import profile.api._

  // Table types

  override protected type DbExType = SqlExercise

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

  override protected val exParts: ExParts[SqlExPart] = SqlExParts

  override protected val dbModels               = SqlDbModels
  override protected val exerciseReviewDbModels = SqlExerciseReviewDbModels

  // Reading

  override protected def completeExForEx(collId: Int, ex: SqlExercise): Future[SqlExercise] = Future.successful(ex)

  // Saving

  override def saveExerciseRest(collId: Int, compEx: SqlExercise): Future[Boolean] = {
    val dbSamples = compEx.sampleSolutions.map(s => StringSolutionDbModels.dbSampleSolFromSampleSol(compEx.id, compEx.semanticVersion, collId, s))

    for {
      samplesSaved <- saveSeq[DbStringSampleSolution](dbSamples, s => db.run(sampleSolutionsTableQuery insertOrUpdate s))
    } yield samplesSaved
  }

  // Column types

  private val sqlExerciseTypeColumnType: BaseColumnType[SqlExerciseType] = jsonColumnType(SqlExerciseType.jsonFormat)

  private val sqlExerciseTagSeqColumnType: BaseColumnType[Seq[SqlExerciseTag]] = jsonSeqColumnType(SqlExerciseTag.jsonFormat)

  override protected implicit val partTypeColumnType: BaseColumnType[SqlExPart] = jsonColumnType(exParts.jsonFormat)

  // Tables

  class SqlScenarioesTable(tag: Tag) extends ExerciseCollectionsTable(tag, "sql_scenarioes")

  class SqlExercisesTable(tag: Tag) extends ExerciseInCollectionTable(tag, "sql_exercises") {

    implicit val setct: BaseColumnType[SqlExerciseType] = sqlExerciseTypeColumnType

    implicit val setsct: BaseColumnType[Seq[SqlExerciseTag]] = sqlExerciseTagSeqColumnType

    implicit val sssct: BaseColumnType[Seq[StringSampleSolution]] = stringSampleSolutionColumnType


    def exerciseType: Rep[SqlExerciseType] = column[SqlExerciseType]("exercise_type_json")

    def hint: Rep[String] = column[String](hintName)

    def tags: Rep[Seq[SqlExerciseTag]] = column[Seq[SqlExerciseTag]]("tags_json")

    def sampleSolutions: Rep[Seq[StringSampleSolution]] = column[Seq[StringSampleSolution]]("sample_solutions_json")


    override def * : ProvenShape[SqlExercise] = (id, collectionId, semanticVersion, title, author, text, state,
      exerciseType, tags, hint.?, sampleSolutions) <> (SqlExercise.tupled, SqlExercise.unapply)

  }


  class SqlSampleSolutionsTable(tag: Tag) extends AStringSampleSolutionsTable(tag, "sql_sample_solutions")

  class SqlUserSolutionsTable(tag: Tag) extends AStringUserSolutionsTable(tag, "sql_user_solutions")


  class SqlExerciseReviewsTable(tag: Tag) extends ExerciseReviewsTable(tag, "sql_exercise_reviews") {

    override def * : ProvenShape[DbSqlExerciseReview] = (username, collectionId, exerciseId, exercisePart, difficulty, maybeDuration.?) <> (DbSqlExerciseReview.tupled, DbSqlExerciseReview.unapply)

  }

}
