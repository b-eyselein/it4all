package model.tools.sql.persistence

import javax.inject.Inject
import model.persistence.ExerciseCollectionTableDefs
import model.tools.sql.SqlConsts._
import model.tools.sql._
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.ast.{ScalaBaseType, TypedType}
import slick.jdbc.JdbcProfile
import slick.lifted.{PrimaryKey, ProvenShape}

import scala.concurrent.{ExecutionContext, Future}
import scala.language.postfixOps

class SqlTableDefs @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(override implicit val executionContext: ExecutionContext)
  extends HasDatabaseConfigProvider[JdbcProfile]
    with ExerciseCollectionTableDefs[SqlExPart, SqlExercise, SqlScenario, String, SqlSampleSolution, SqlUserSolution, SqlExerciseReview] {

  import profile.api._

  // Table types

  override protected type DbExType = DbSqlExercise

  override protected type ExTableDef = SqlExercisesTable

  override protected type CollTableDef = SqlScenarioesTable


  override protected type DbSampleSolType = DbSqlSampleSolution

  override protected type DbSampleSolTable = SqlSamplesTable


  override protected type DbUserSolType = DbSqlUserSolution

  override protected type DbUserSolTable = SqlSolutionsTable


  override protected type DbReviewType = DbSqlExerciseReview

  override protected type ReviewsTable = SqlExerciseReviewsTable

  // Table queries

  override protected val exTable     : TableQuery[SqlExercisesTable]       = TableQuery[SqlExercisesTable]
  override protected val collTable   : TableQuery[SqlScenarioesTable]      = TableQuery[SqlScenarioesTable]
  override protected val solTable    : TableQuery[SqlSolutionsTable]       = TableQuery[SqlSolutionsTable]
  override protected val reviewsTable: TableQuery[SqlExerciseReviewsTable] = TableQuery[SqlExerciseReviewsTable]

  private val sqlSamples = TableQuery[SqlSamplesTable]

  // Helper methods

  override protected val dbModels               = SqlDbModels
  override protected val exerciseReviewDbModels = SqlExerciseReviewDbModels

  override protected def copyDbUserSolType(sol: DbSqlUserSolution, newId: Int): DbSqlUserSolution = sol.copy(id = newId)

  override def exDbValuesFromExercise(collId: Int, compEx: SqlExercise): DbSqlExercise = dbModels.dbExerciseFromExercise(collId, compEx)

  // Reading

  protected def futureSamplesForExercise(collId: Int, exId: Int): Future[Seq[SqlSampleSolution]] =
    db.run(sqlSamples
      .filter {
        sample => sample.collectionId === collId && sample.exerciseId === exId
      }
      .result
      .map(_ map dbModels.sampleSolFromDbSampleSol))


  override def futureExerciseById(collId: Int, exId: Int): Future[Option[SqlExercise]] = for {
    maybeEx <- db.run(exTable.filter(e => e.id === exId && e.collectionId === collId).result.headOption)
    samples <- futureSamplesForExercise(collId, exId)
  } yield maybeEx map (dbEx => dbModels.exerciseFromDbValues(dbEx, samples))

  override def futureExercisesInColl(collId: Int): Future[Seq[SqlExercise]] =
    db.run(exTable.filter(_.collectionId === collId).result) flatMap { allExes =>

      Future.sequence(allExes map { dbEx =>
        futureSamplesForExercise(collId, dbEx.id) map {
          samples => dbModels.exerciseFromDbValues(dbEx, samples)
        }
      })
    }

  //  override def futureSolveState(user: User, collId: Int, exId: Int): Future[Option[SolvedState]] = db.run(
  //    solTable.filter {
  //      sol => sol.username === user.username && sol.collectionId === collId && sol.exerciseId === exId
  //    }
  //      .sortBy(_.id.desc)
  //      .result.headOption.map {
  //      case None           => None
  //      case Some(solution) =>
  //        if (solution.points == solution.maxPoints) Some(SolvedStates.CompletelySolved)
  //        else Some(SolvedStates.PartlySolved)
  //    }
  //  )


  override protected def completeExForEx(collId: Int, ex: DbSqlExercise): Future[SqlExercise] = for {
    samples <- futureSamplesForExercise(collId, ex.id)
  } yield dbModels.exerciseFromDbValues(ex, samples)


  override def futureSampleSolutionsForExPart(scenarioId: Int, exerciseId: Int, sqlExPart: SqlExPart): Future[Seq[String]] =
    db.run(sqlSamples.filter {
      e => e.collectionId === scenarioId && e.exerciseId === exerciseId
    }.map(_.sample).result)

  // Saving

  override def saveExerciseRest(collId: Int, compEx: SqlExercise): Future[Boolean] = {
    val dbSamples = compEx.samples map (s => dbModels.dbSampleSolFromSampleSol(compEx.id, compEx.semanticVersion, collId, s))

    for {
      samplesSaved <- saveSeq[DbSqlSampleSolution](dbSamples, s => db.run(sqlSamples insertOrUpdate s))
    } yield samplesSaved
  }

  //  private def saveSqlExercise(sqlExercise: SqlExercise): Future[Boolean] =
  //    db.run(exTable insertOrUpdate dbModels.dbExerciseFromExercise(sqlExercise)) flatMap { _ => saveSeq(sqlExercise.samples, saveSqlSample) }

  // Column types

  override protected implicit val partTypeColumnType: BaseColumnType[SqlExPart] =
    MappedColumnType.base[SqlExPart, String](_.entryName, SqlExParts.withNameInsensitive)


  private implicit val sqlExTypeColumnType: BaseColumnType[SqlExerciseType] =
    MappedColumnType.base[SqlExerciseType, String](_.entryName, str => SqlExerciseType.withNameInsensitiveOption(str) getOrElse SqlExerciseType.SELECT)

  //  private implicit val SqlExTagColumnType: BaseColumnType[SqlExTag] =
  //    MappedColumnType.base[SqlExTag, String](_.entryName, str => SqlExTag.withNameInsensitiveOption(str) getOrElse SqlExTag.SQL_JOIN)

  override protected implicit val solTypeColumnType: TypedType[String] = ScalaBaseType.stringType

  // Tables

  class SqlScenarioesTable(tag: Tag) extends ExerciseCollectionTable(tag, "sql_scenarioes") {

    override def * : ProvenShape[SqlScenario] = (id, title, author, text, state, shortName) <> (SqlScenario.tupled, SqlScenario.unapply)

  }

  class SqlExercisesTable(tag: Tag) extends ExerciseInCollectionTable(tag, "sql_exercises") {

    def exerciseType: Rep[SqlExerciseType] = column[SqlExerciseType]("exercise_type")

    def hint: Rep[String] = column[String](hintName)

    def tags: Rep[String] = column[String](tagsName)


    override def * : ProvenShape[DbSqlExercise] = (id, semanticVersion, title, author, text, state, collectionId, exerciseType, tags, hint.?) <> (DbSqlExercise.tupled, DbSqlExercise.unapply)

  }

  class SqlSamplesTable(tag: Tag) extends ASampleSolutionsTable(tag, "sql_samples") {

    def pk: PrimaryKey = primaryKey("pk", (id, exerciseId, exSemVer, collectionId))


    override def * : ProvenShape[DbSqlSampleSolution] = (id, exerciseId, exSemVer, collectionId, sample) <> (DbSqlSampleSolution.tupled, DbSqlSampleSolution.unapply)

  }

  class SqlSolutionsTable(tag: Tag) extends AUserSolutionsTable(tag, "sql_solutions") {

    override def * : ProvenShape[DbSqlUserSolution] = (id, exerciseId, exSemVer, collectionId, username, part, solution,
      points, maxPoints) <> (DbSqlUserSolution.tupled, DbSqlUserSolution.unapply)

  }

  class SqlExerciseReviewsTable(tag: Tag) extends ExerciseReviewsTable(tag, "sql_exercise_reviews") {

    override def * : ProvenShape[DbSqlExerciseReview] = (username, collectionId, exerciseId, exercisePart, difficulty, maybeDuration.?) <> (DbSqlExerciseReview.tupled, DbSqlExerciseReview.unapply)

  }

}
