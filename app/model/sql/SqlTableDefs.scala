package model.sql

import javax.inject.Inject
import model.core.overviewHelpers.{SolvedState, SolvedStates}
import model.persistence.ExerciseCollectionTableDefs
import model.sql.SqlConsts._
import model.sql.persistence.{DbSqlExercise, SqlDbModels}
import model.{SemanticVersion, User}
import play.api.Logger
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.ast.{ScalaBaseType, TypedType}
import slick.jdbc.JdbcProfile
import slick.lifted.{ForeignKeyQuery, PrimaryKey, ProvenShape}

import scala.concurrent.{ExecutionContext, Future}
import scala.language.postfixOps

class SqlTableDefs @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(override implicit val executionContext: ExecutionContext)
  extends HasDatabaseConfigProvider[JdbcProfile] with ExerciseCollectionTableDefs[SqlExercise, SqlScenario, SqlCompleteScenario, String, SqlSolution] {

  import profile.api._

  // Table types

  override protected type ExDbValues = DbSqlExercise

  override protected type ExTableDef = SqlExercisesTable

  override protected type CollTableDef = SqlScenarioesTable

  override protected type SolTableDef = SqlSolutionsTable

  // Table queries

  override protected val exTable = TableQuery[SqlExercisesTable]

  override protected val collTable = TableQuery[SqlScenarioesTable]

  override protected val solTable = TableQuery[SqlSolutionsTable]

  private val sqlSamples = TableQuery[SqlSamplesTable]

  // Helper methods

  override def exDbValuesFromExercise(compEx: SqlExercise): DbSqlExercise = SqlDbModels.dbExerciseFromExercise(compEx)

  // Reading

  override def futureExerciseById(collId: Int, id: Int): Future[Option[SqlExercise]] = for {
    maybeEx <- db.run(exTable.filter(e => e.id === id && e.collectionId === collId).result.headOption)
    samples <- db.run(sqlSamples.filter(sa => sa.exerciseId === id && sa.collId === collId).result)
  } yield maybeEx map (dbEx => SqlDbModels.exerciseFromDbValues(dbEx, samples))

  override def futureExercisesInColl(collId: Int): Future[Seq[SqlExercise]] =
    db.run(exTable.filter(_.collectionId === collId).result) flatMap { allExes =>

      Future.sequence(allExes map { dbEx =>
        db.run(sqlSamples.filter {
          sample => sample.collId === collId && sample.exerciseId === dbEx.id && sample.exSemVer === dbEx.semanticVersion
        }.result) map { samples => SqlDbModels.exerciseFromDbValues(dbEx, samples) }
      })
    }

  override def futureSolveState(user: User, collId: Int, exId: Int): Future[Option[SolvedState]] = db.run(
    solTable.filter {
      sol => sol.username === user.username && sol.collectionId === collId && sol.exerciseId === exId
    }
      .sortBy(_.id.desc)
      .result.headOption.map {
      case None           => None
      case Some(solution) =>
        if (solution.points == solution.maxPoints) Some(SolvedStates.CompletelySolved)
        else Some(SolvedStates.PartlySolved)
    }
  )


  override protected def completeExForEx(ex: DbSqlExercise): Future[SqlExercise] =
    samplesForEx(ex.collectionId, ex.id) map (samples => SqlDbModels.exerciseFromDbValues(ex, samples))

  override def completeCollForColl(coll: SqlScenario): Future[SqlCompleteScenario] =
    db.run(exTable.filter(_.collectionId === coll.id).result) flatMap (exes => Future.sequence(exes map completeExForEx)) map (exes => SqlCompleteScenario(coll, exes))

  private def samplesForEx(collId: Int, exId: Int): Future[Seq[SqlSample]] =
    db.run(sqlSamples filter (table => table.exerciseId === exId && table.collId === collId) result)

  override def futureSampleSolutions(scenarioId: Int, exerciseId: Int): Future[Seq[String]] =
    db.run(sqlSamples.filter(e => e.collId === scenarioId && e.exerciseId === exerciseId).map(_.sample).result)

  // Saving

  def saveSolution(sol: SqlSolution): Future[Boolean] = db.run(solTable insertOrUpdate sol) map (_ => true) recover { case _: Throwable => false }

  override def saveExerciseRest(compEx: SqlExercise): Future[Boolean] =
    saveSeq[SqlSample](compEx.samples, saveSqlSample)

  override def saveCompleteColl(completeScenario: SqlCompleteScenario): Future[Boolean] =
    db.run(collTable insertOrUpdate completeScenario.coll) flatMap (_ => saveSeq(completeScenario.exercises, saveSqlExercise))

  private def saveSqlExercise(sqlExercise: SqlExercise): Future[Boolean] =
    db.run(exTable insertOrUpdate SqlDbModels.dbExerciseFromExercise(sqlExercise)) flatMap { _ => saveSeq(sqlExercise.samples, saveSqlSample) }

  private def saveSqlSample(sample: SqlSample): Future[Boolean] =
    db.run(sqlSamples insertOrUpdate sample) map (_ => true) recover { case e: Throwable =>
      Logger.error("Could not save sql sample!", e)
      false
    }

  override protected def copyDBSolType(sol: SqlSolution, newId: Int): SqlSolution = sol.copy(id = newId)

  // Column types

  private implicit val sqlExTypeColumnType: BaseColumnType[SqlExerciseType] =
    MappedColumnType.base[SqlExerciseType, String](_.entryName, str => SqlExerciseType.withNameInsensitiveOption(str) getOrElse SqlExerciseType.SELECT)

  //  private implicit val SqlExTagColumnType: BaseColumnType[SqlExTag] =
  //    MappedColumnType.base[SqlExTag, String](_.entryName, str => SqlExTag.withNameInsensitiveOption(str) getOrElse SqlExTag.SQL_JOIN)

  override protected implicit val solutionTypeColumnType: TypedType[String] = ScalaBaseType.stringType

  // Tables

  class SqlScenarioesTable(tag: Tag) extends ExerciseCollectionTable(tag, "sql_scenarioes") {

    def shortName: Rep[String] = column[String](shortNameName)


    override def * : ProvenShape[SqlScenario] = (id, semanticVersion, title, author, text, state, shortName) <> (SqlScenario.tupled, SqlScenario.unapply)

  }

  class SqlExercisesTable(tag: Tag) extends ExerciseInCollectionTable(tag, "sql_exercises") {

    def exerciseType: Rep[SqlExerciseType] = column[SqlExerciseType]("exercise_type")

    def hint: Rep[String] = column[String](hintName)

    def tags: Rep[String] = column[String](tagsName)


    override def * : ProvenShape[DbSqlExercise] = (id, semanticVersion, title, author, text, state, collectionId, collSemVer, exerciseType, tags, hint.?) <> (DbSqlExercise.tupled, DbSqlExercise.unapply)

  }

  class SqlSamplesTable(tag: Tag) extends Table[SqlSample](tag, "sql_samples") {

    def id: Rep[Int] = column[Int](idName)

    def exerciseId: Rep[Int] = column[Int]("exercise_id")

    def exSemVer: Rep[SemanticVersion] = column[SemanticVersion]("ex_sem_ver")

    def collId: Rep[Int] = column[Int]("collection_id")

    def collSemVer: Rep[SemanticVersion] = column[SemanticVersion]("coll_sem_ver")

    def sample: Rep[String] = column[String]("sample")


    def pk: PrimaryKey = primaryKey("pk", (id, exerciseId, exSemVer, collId, collSemVer))

    def exerciseFk: ForeignKeyQuery[SqlExercisesTable, DbSqlExercise] = foreignKey("exercise_fk", (exerciseId, exSemVer, collId, collSemVer), exTable)(exes =>
      (exes.id, exes.semanticVersion, exes.collectionId, exes.collSemVer))


    override def * : ProvenShape[SqlSample] = (id, exerciseId, exSemVer, collId, collSemVer, sample) <> (SqlSample.tupled, SqlSample.unapply)

  }

  class SqlSolutionsTable(tag: Tag) extends CollectionExSolutionsTable(tag, "sql_solutions") {

    def solution: Rep[String] = column[String]("solution")


    override def * : ProvenShape[SqlSolution] = (id, username, exerciseId, exSemVer, collectionId, collSemVer, solution,
      points, maxPoints) <> (SqlSolution.tupled, SqlSolution.unapply)

  }

}