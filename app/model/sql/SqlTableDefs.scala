package model.sql

import javax.inject.Inject
import model.SemanticVersion
import model.persistence.ExerciseCollectionTableDefs
import model.sql.SqlConsts._
import play.api.Logger
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.ast.{ScalaBaseType, TypedType}
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}
import scala.language.postfixOps

class SqlTableDefs @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(override implicit val executionContext: ExecutionContext)
  extends HasDatabaseConfigProvider[JdbcProfile] with ExerciseCollectionTableDefs[SqlExercise, SqlCompleteEx, SqlScenario, SqlCompleteScenario, String, SqlSolution] {

  import profile.api._

  // Table types

  override protected type ExTableDef = SqlExercisesTable

  override protected type CollTableDef = SqlScenarioesTable

  override protected type SolTableDef = SqlSolutionsTable

  // Table queries

  override protected val exTable = TableQuery[SqlExercisesTable]

  override protected val collTable = TableQuery[SqlScenarioesTable]

  override protected val solTable = TableQuery[SqlSolutionsTable]

  private val sqlSamples = TableQuery[SqlSampleTable]

  // Reading

  override def completeExForEx(ex: SqlExercise): Future[SqlCompleteEx] = samplesForEx(ex.collectionId, ex.id) map (samples => SqlCompleteEx(ex, samples))

  override def completeCollForColl(coll: SqlScenario): Future[SqlCompleteScenario] =
    db.run(exTable.filter(_.collectionId === coll.id).result) flatMap (exes => Future.sequence(exes map completeExForEx)) map (exes => SqlCompleteScenario(coll, exes))

  private def samplesForEx(collId: Int, exId: Int): Future[Seq[SqlSample]] =
    db.run(sqlSamples filter (table => table.exerciseId === exId && table.collId === collId) result)

  // Saving

  def saveSolution(sol: SqlSolution): Future[Boolean] = db.run(solTable insertOrUpdate sol) map (_ => true) recover { case _: Throwable => false }

  override def saveExerciseRest(compEx: SqlCompleteEx): Future[Boolean] =
    saveSeq[SqlSample](compEx.samples, saveSqlSample)

  override def saveCompleteColl(completeScenario: SqlCompleteScenario): Future[Boolean] =
    db.run(collTable insertOrUpdate completeScenario.coll) flatMap (_ => saveSeq(completeScenario.exercises, saveSqlCompleteEx))

  private def saveSqlCompleteEx(completeEx: SqlCompleteEx): Future[Boolean] =
    db.run(exTable insertOrUpdate completeEx.ex) flatMap { _ => saveSeq(completeEx.samples, saveSqlSample) }

  private def saveSqlSample(sample: SqlSample): Future[Boolean] =
    db.run(sqlSamples insertOrUpdate sample) map (_ => true) recover { case e: Throwable =>
      Logger.error("Could not save sql sample!", e)
      false
    }

  // Column types

  private implicit val SqlExTypeColumnType: BaseColumnType[SqlExerciseType] =
    MappedColumnType.base[SqlExerciseType, String](_.entryName, str => SqlExerciseType.withNameInsensitiveOption(str) getOrElse SqlExerciseType.SELECT)

  private implicit val SqlExTagColumnType: BaseColumnType[SqlExTag] =
    MappedColumnType.base[SqlExTag, String](_.entryName, str => SqlExTag.withNameInsensitiveOption(str) getOrElse SqlExTag.SQL_JOIN)

  override protected implicit val solutionTypeColumnType: TypedType[String] = ScalaBaseType.stringType

  // Tables

  class SqlScenarioesTable(tag: Tag) extends ExerciseCollectionTable(tag, "sql_scenarioes") {

    def shortName = column[String](shortNameName)


    override def * = (id, semanticVersion, title, author, text, state, shortName).mapTo[SqlScenario]

  }

  class SqlExercisesTable(tag: Tag) extends ExerciseInCollectionTable(tag, "sql_exercises") {

    def exerciseType = column[SqlExerciseType]("exercise_type")

    def hint = column[String](hintName)

    def tags = column[String](tagsName)


    override def * = (id, semanticVersion, title, author, text, state, collectionId, collSemVer, exerciseType, tags, hint.?).mapTo[SqlExercise]

  }

  class SqlSampleTable(tag: Tag) extends Table[SqlSample](tag, "sql_samples") {

    def id = column[Int](idName)

    def exerciseId = column[Int]("exercise_id")

    def exSemVer = column[SemanticVersion]("ex_sem_ver")

    def collId = column[Int]("collection_id")

    def collSemVer = column[SemanticVersion]("coll_sem_ver")

    def sample = column[String]("sample")


    def pk = primaryKey("pk", (id, exerciseId, exSemVer, collId, collSemVer))

    def exerciseFk = foreignKey("exercise_fk", (exerciseId, exSemVer, collId, collSemVer), exTable)(exes =>
      (exes.id, exes.semanticVersion, exes.collectionId, exes.collSemVer))


    override def * = (id, exerciseId, exSemVer, collId, collSemVer, sample).mapTo[SqlSample]

  }

  class SqlSolutionsTable(tag: Tag) extends CollectionExSolutionsTable(tag, "sql_solutions") {

    def solution = column[String]("solution")


    override def * = (username, exerciseId, exSemVer, collectionId, collSemVer, solution, points, maxPoints).mapTo[SqlSolution]

  }

}