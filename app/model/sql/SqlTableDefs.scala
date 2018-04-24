package model.sql

import javax.inject.Inject
import model.Enums.ExerciseState
import model._
import model.persistence.ExerciseCollectionTableDefs
import model.sql.SqlConsts._
import model.sql.SqlEnums.{SqlExTag, SqlExerciseType}
import play.api.Logger
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.twirl.api.Html
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}
import scala.language.postfixOps
import scala.util.Try


// Classes for use

case class SqlCompleteScenario(override val coll: SqlScenario, override val exercises: Seq[SqlCompleteEx]) extends CompleteCollection {

  override type Ex = SqlExercise

  override type CompEx = SqlCompleteEx

  override type Coll = SqlScenario

  override def exercisesWithFilter(filter: String): Seq[SqlCompleteEx] = SqlExerciseType.byString(filter) map (exType => getExercisesByType(exType)) getOrElse Seq.empty

  def getExercisesByType(exType: SqlExerciseType): Seq[SqlCompleteEx] = exercises filter (_.ex.exerciseType == exType)

  override def renderRest: Html = new Html(
    s"""<div class="row">
       |  <div class="col-md-2"><b>Kurzname</b></div>
       |  <div class="col-md-4">${coll.shortName}</div>
       |</div>""".stripMargin)

}

case class SqlCompleteEx(ex: SqlExercise, samples: Seq[SqlSample]) extends CompleteExInColl[SqlExercise] {

  override def tags: Seq[SqlExTag] = (ex.tags split tagJoinChar).toSeq flatMap SqlExTag.byString

  override def preview: Html = // FIXME: move to toolMain!
    views.html.collectionExercises.sql.sqlExPreview(this)

}

// case classes for db

case class SqlScenario(id: Int, title: String, author: String, text: String, state: ExerciseState, shortName: String) extends ExerciseCollection[SqlExercise, SqlCompleteEx] {

  def this(baseValues: (Int, String, String, String, ExerciseState), shortName: String) =
    this(baseValues._1, baseValues._2, baseValues._3, baseValues._4, baseValues._5, shortName)

  val imageUrl: String = shortName + ".png"

}

case class SqlExercise(id: Int, title: String, author: String, text: String, state: ExerciseState,
                       collectionId: Int, exerciseType: SqlExerciseType, tags: String, hint: Option[String]) extends ExInColl {

  def this(baseValues: (Int, String, String, String, ExerciseState), collectionId: Int, exerciseType: SqlExerciseType, tags: String, hint: Option[String]) =
    this(baseValues._1, baseValues._2, baseValues._3, baseValues._4, baseValues._5, collectionId, exerciseType, tags, hint)

}

case class SqlSample(id: Int, exerciseId: Int, scenarioId: Int, sample: String)

case class SqlSolution(username: String, collectionId: Int, exerciseId: Int, solution: String) extends CollectionExSolution

// Table Definitions

class SqlTableDefs @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(override implicit val executionContext: ExecutionContext)
  extends HasDatabaseConfigProvider[JdbcProfile] with ExerciseCollectionTableDefs[SqlExercise, SqlCompleteEx, SqlScenario, SqlCompleteScenario, SqlSolution] {

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
    db.run(sqlSamples filter (table => table.exerciseId === exId && table.scenarioId === collId) result)

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

  implicit val SqlExTypeColumnType: BaseColumnType[SqlExerciseType] =
    MappedColumnType.base[SqlExerciseType, String](_.name, str => Try(SqlExerciseType.valueOf(str)) getOrElse SqlExerciseType.SELECT)

  implicit val SqlExTagColumnType: BaseColumnType[SqlExTag] =
    MappedColumnType.base[SqlExTag, String](_.name, str => Try(SqlExTag.valueOf(str)) getOrElse SqlExTag.SQL_JOIN)

  // Tables

  class SqlScenarioesTable(tag: Tag) extends HasBaseValuesTable[SqlScenario](tag, "sql_scenarioes") {

    def shortName = column[String](shortNameName)


    def pk = primaryKey("pk", id)


    override def * = (id, title, author, text, state, shortName) <> (SqlScenario.tupled, SqlScenario.unapply)

  }

  class SqlExercisesTable(tag: Tag) extends ExerciseInCollectionTable[SqlExercise](tag, "sql_exercises") {

    def exerciseType = column[SqlExerciseType]("exercise_type")

    def hint = column[String](hintName)

    def tags = column[String](tagsName)


    def pk = primaryKey("pk", (id, collectionId))

    def scenarioFk = foreignKey("scenario_fk", collectionId, collTable)(_.id)


    override def * = (id, title, author, text, state, collectionId, exerciseType, tags, hint.?) <> (SqlExercise.tupled, SqlExercise.unapply)

  }

  class SqlSampleTable(tag: Tag) extends Table[SqlSample](tag, "sql_samples") {

    def id = column[Int](idName)

    def exerciseId = column[Int]("exercise_id")

    def scenarioId = column[Int]("collection_id")

    def sample = column[String]("sample")


    def pk = primaryKey("pk", (id, exerciseId, scenarioId))

    def exerciseFk = foreignKey("exercise_fk", (exerciseId, scenarioId), exTable)(exes => (exes.id, exes.collectionId))


    override def * = (id, exerciseId, scenarioId, sample) <> (SqlSample.tupled, SqlSample.unapply)

  }

  class SqlSolutionsTable(tag: Tag) extends CollectionExSolutionsTable[SqlSolution](tag, "sql_solutions") {

    def solution = column[String]("solution")


    override def * = (username, collectionId, exerciseId, solution) <> (SqlSolution.tupled, SqlSolution.unapply)

  }

}