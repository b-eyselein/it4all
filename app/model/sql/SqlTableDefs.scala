package model.sql

import model.Enums.ExerciseState
import model._
import model.sql.SqlConsts._
import model.sql.SqlEnums.{SqlExTag, SqlExerciseType}
import play.api.db.slick.HasDatabaseConfigProvider
import play.api.mvc.Call
import play.twirl.api.Html
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}
import scala.language.postfixOps
import scala.util.Try

// Classes for use

case class SqlCompleteScenario(override val coll: SqlScenario, override val exercises: Seq[SqlCompleteEx]) extends CompleteCollection {

  override type ExType = SqlExercise

  override type CompExType = SqlCompleteEx

  override type CollType = SqlScenario

  override def exercisesWithFilter(filter: String): Seq[SqlCompleteEx] = SqlExerciseType.byString(filter) map (exType => getExercisesByType(exType)) getOrElse Seq.empty

  def getExercisesByType(exType: SqlExerciseType): Seq[SqlCompleteEx] = exercises filter (_.ex.exerciseType == exType)

  override def renderRest: Html = new Html(
    s"""<div class="row">
       |  <div class="col-md-2"><b>Kurzname</b></div>
       |  <div class="col-md-4">${coll.shortName}</div>
       |</div>""".stripMargin)

}

case class SqlCompleteEx(ex: SqlExercise, samples: Seq[SqlSample]) extends CompleteEx[SqlExercise] {

  override def tags: Seq[SqlExTag] = (ex.tags split TagJoinChar).toSeq flatMap SqlExTag.byString

  override def exType: String = ex.exerciseType.name

  override def preview: Html = new Html(
    s"""<div class="row">
       |  <div class="col-sm-2"><b>Typ:</b></div>
       |  <div class="col-sm-4">${ex.exerciseType}</div>
       |  <div class="col-sm-2"><b>Tags:</b></div>
       |  <div class="col-sm-2">${tags map (_.title) mkString ", "}</div>
       |</div>
       |<div class="row">
       |  <div class="col-sm-12"><b>Musterl&ouml;sungen:</b></div>
       |</div>
       |${samples map (sample => s"<pre>${sample.sample}</pre>") mkString}""".stripMargin)


  override def exerciseRoutes: Map[Call, String] = ???

}

// case classes for db

object SqlScenario {

  def tupled(t: (Int, String, String, String, ExerciseState, String)): SqlScenario =
    SqlScenario(t._1, t._2, t._3, t._4, t._5, t._6)

  def apply(i: Int, ti: String, a: String, te: String, s: ExerciseState, shortName: String) =
    new SqlScenario(BaseValues(i, ti, a, te, s), shortName)

  def unapply(arg: SqlScenario): Option[(Int, String, String, String, ExerciseState, String)] =
    Some((arg.id, arg.title, arg.author, arg.text, arg.state, arg.shortName))

}

case class SqlScenario(override val baseValues: BaseValues, shortName: String) extends ExerciseCollection[SqlExercise, SqlCompleteEx] {

  val imageUrl: String = shortName + ".png"

}

object SqlExercise {

  def tupled(t: (Int, String, String, String, ExerciseState, Int, SqlExerciseType, String, Option[String])): SqlExercise =
    SqlExercise(t._1, t._2, t._3, t._4, t._5, t._6, t._7, t._8, t._9)

  def apply(i: Int, ti: String, a: String, te: String, s: ExerciseState, scenarioId: Int, exerciseType: SqlExerciseType, tags: String, hint: Option[String]) =
    new SqlExercise(BaseValues(i, ti, a, te, s), scenarioId, exerciseType, tags, hint)

  def unapply(arg: SqlExercise): Option[(Int, String, String, String, ExerciseState, Int, SqlExerciseType, String, Option[String])] =
    Some((arg.id, arg.title, arg.author, arg.text, arg.state, arg.collectionId, arg.exerciseType, arg.tags, arg.hint))

}

case class SqlExercise(override val baseValues: BaseValues, collectionId: Int, exerciseType: SqlExerciseType, tags: String, hint: Option[String]) extends ExerciseInCollection

case class SqlSample(id: Int, exerciseId: Int, scenarioId: Int, sample: String)

case class SqlSolution(userName: String, scenarioId: Int, exerciseId: Int, solution: String)

// Table Definitions

trait SqlTableDefs extends TableDefs {
  self: HasDatabaseConfigProvider[JdbcProfile] =>

  import profile.api._

  // Saving

  def saveSqlCompleteScenario(completeScenario: SqlCompleteScenario)(implicit ec: ExecutionContext): Future[Seq[Any]] =
    db.run(sqlScenarioes insertOrUpdate completeScenario.coll) flatMap (_ => Future.sequence(completeScenario.exercises map saveSqlCompleteEx))

  private def saveSqlCompleteEx(completeEx: SqlCompleteEx)(implicit ec: ExecutionContext) =
    db.run(sqlExercises insertOrUpdate completeEx.ex) flatMap { _ => Future.sequence(completeEx.samples map saveSqlSample) }

  private def saveSqlSample(sample: SqlSample)(implicit ec: ExecutionContext): Future[Int] =
    db.run(sqlSamples insertOrUpdate sample)

  // Reading

  def exercisesInScenario(id: Int): Future[Int] = db.run(sqlExercises.filter(_.scenarioId === id).size.result)

  def completeSqlScenarioes(implicit ec: ExecutionContext): Future[Seq[SqlCompleteScenario]] = db.run(
    sqlScenarioes.result map (results => results map (res => SqlCompleteScenario(res, Seq.empty))))

  def completeScenarioById(id: Int)(implicit ec: ExecutionContext): Future[Option[SqlCompleteScenario]] =
    db.run(sqlScenarioes.filter(_.id === id).result.headOption) flatMap {
      case None           => Future(None)
      case Some(scenario) => sqlExercises.completeExesForScenario(id) map (exercises => Some(SqlCompleteScenario(scenario, exercises)))
    }

  // Table queries

  val sqlScenarioes = TableQuery[SqlScenarioesTable]

  object sqlExercises extends TableQuery(new SqlExercisesTable(_)) {

    def completeExesForScenario(scenarioId: Int)(implicit ec: ExecutionContext): Future[Seq[SqlCompleteEx]] = db.run(this.filter(_.scenarioId === scenarioId).result) flatMap {
      exes =>
        Future.sequence(exes map { ex =>
          // FIXME: [val tagsFuture = db.run(sqlTags.filter ...)] ==> Table is missing!
          val samples: Future[Seq[SqlSample]] = db.run(sqlSamples filter (sample => sample.scenarioId === scenarioId && sample.exerciseId === ex.id) result)
          samples map (samp => SqlCompleteEx(ex, samp))
        })
    }

    def completeEx(collId: Int, id: Int)(implicit ec: ExecutionContext): Future[Option[SqlCompleteEx]] =
      db.run(this.filter(table => table.scenarioId === collId && table.id === id).result.headOption) flatMap {
        case None     => Future(None)
        case Some(ex) => samplesForEx(collId, id) map (samples => Some(SqlCompleteEx(ex, samples)))
      }

    def samplesForEx(collId: Int, exId: Int)(implicit ec: ExecutionContext): Future[Seq[SqlSample]] =
      db.run(sqlSamples filter (table => table.exerciseId === exId && table.scenarioId === collId) result)

  }

  val sqlSolutions = TableQuery[SqlSolutionTable]

  val sqlSamples = TableQuery[SqlSampleTable]

  // Column types

  implicit val SqlExTypeColumnType: BaseColumnType[SqlExerciseType] =
    MappedColumnType.base[SqlExerciseType, String](_.name, str => Try(SqlExerciseType.valueOf(str)) getOrElse SqlExerciseType.SELECT)

  implicit val SqlExTagColumnType: BaseColumnType[SqlExTag] =
    MappedColumnType.base[SqlExTag, String](_.name, str => Try(SqlExTag.valueOf(str)) getOrElse SqlExTag.SQL_JOIN)

  // Tables

  class SqlScenarioesTable(tag: Tag) extends HasBaseValuesTable[SqlScenario](tag, "sql_scenarioes") {

    def shortName = column[String](SHORTNAME_NAME)


    def pk = primaryKey("pk", id)


    def * = (id, title, author, text, state, shortName) <> (SqlScenario.tupled, SqlScenario.unapply)

  }

  class SqlExercisesTable(tag: Tag) extends HasBaseValuesTable[SqlExercise](tag, "sql_exercises") {

    def scenarioId = column[Int]("scenario_id")

    def exerciseType = column[SqlExerciseType]("exercise_type")

    def hint = column[String](HINT_NAME)

    def tags = column[String](TAGS_NAME)


    def pk = primaryKey("pk", (id, scenarioId))

    def scenarioFk = foreignKey("scenario_fk", scenarioId, sqlScenarioes)(_.id)


    def * = (id, title, author, text, state, scenarioId, exerciseType, tags, hint.?) <> (SqlExercise.tupled, SqlExercise.unapply)

  }

  class SqlSampleTable(tag: Tag) extends Table[SqlSample](tag, "sql_samples") {

    def id = column[Int](ID_NAME)

    def exerciseId = column[Int]("exercise_id")

    def scenarioId = column[Int]("scenario_id")

    def sample = column[String]("sample")


    def pk = primaryKey("pk", (id, exerciseId, scenarioId))

    def exerciseFk = foreignKey("exercise_fk", (exerciseId, scenarioId), sqlExercises)(exes => (exes.id, exes.scenarioId))


    def * = (id, exerciseId, scenarioId, sample) <> (SqlSample.tupled, SqlSample.unapply)

  }

  class SqlSolutionTable(tag: Tag) extends Table[SqlSolution](tag, "sql_solutions") {

    def username = column[String]("username")

    def scenarioId = column[Int]("scenario_id")

    def exerciseId = column[Int]("exercise_id")

    def solution = column[String]("solution")


    def pk = primaryKey("pk", (username, scenarioId, exerciseId))

    def exerciseFk = foreignKey("exercise_fk", (exerciseId, scenarioId), sqlExercises)(ex => (ex.id, ex.scenarioId))


    def * = (username, scenarioId, exerciseId, solution) <> (SqlSolution.tupled, SqlSolution.unapply)

  }

}