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

// Classes for use

case class SqlCompleteScenario(override val coll: SqlScenario, override val exercises: Seq[SqlCompleteEx]) extends CompleteCollection {

  override type ExType = SqlExercise

  override type CompExType = SqlCompleteEx

  override type CollType = SqlScenario

  def getBorder(exType: SqlExerciseType, start: Int): Int =
    (Math.min(getExercisesByType(exType).size, start + STEP) / STEP) * STEP

  def getExercises(exType: SqlExerciseType, start: Int): Seq[SqlCompleteEx] = {
    val ex = getExercisesByType(exType)
    ex.slice(Math.max(start, 0), Math.min(start + STEP, ex.size))
  }

  def getExercisesByType(exType: SqlExerciseType): Seq[SqlCompleteEx] = exercises filter (_.ex.exerciseType == exType)

  val numOfSites: Int = exercises.size / STEP + 1

}

case class SqlCompleteEx(ex: SqlExercise, override val tags: Seq[SqlExTag], samples: Seq[SqlSample]) extends CompleteEx[SqlExercise] {

  override def preview: Html = ???

  override def renderListRest: Html = ???

  override def exerciseRoutes: Map[Call, String] = ???

}

// case classes for db

object SqlScenario {

  def tupled(t: (Int, String, String, String, ExerciseState, String, String)): SqlScenario =
    SqlScenario(t._1, t._2, t._3, t._4, t._5, t._6, t._7)

  def apply(i: Int, ti: String, a: String, te: String, s: ExerciseState, shortName: String, scriptFile: String) =
    new SqlScenario(BaseValues(i, ti, a, te, s), shortName, scriptFile)

  def unapply(arg: SqlScenario): Option[(Int, String, String, String, ExerciseState, String, String)] =
    Some((arg.id, arg.title, arg.author, arg.text, arg.state, arg.shortName, arg.scriptFile))

}

case class SqlScenario(override val baseValues: BaseValues, shortName: String, scriptFile: String) extends ExerciseCollection[SqlExercise, SqlCompleteEx] {

  val imageUrl: String = shortName + ".png"

}

object SqlExercise {

  def tupled(t: (Int, String, String, String, ExerciseState, Int, SqlExerciseType, Option[String])): SqlExercise =
    SqlExercise(t._1, t._2, t._3, t._4, t._5, t._6, t._7, t._8)

  def apply(i: Int, ti: String, a: String, te: String, s: ExerciseState, scenarioId: Int, exerciseType: SqlExerciseType, hint: Option[String]) =
    new SqlExercise(BaseValues(i, ti, a, te, s), scenarioId, exerciseType, hint)

  def unapply(arg: SqlExercise): Option[(Int, String, String, String, ExerciseState, Int, SqlExerciseType, Option[String])] =
    Some((arg.id, arg.title, arg.author, arg.text, arg.state, arg.scenarioId, arg.exerciseType, arg.hint))

}

case class SqlExercise(override val baseValues: BaseValues, scenarioId: Int, exerciseType: SqlExerciseType, hint: Option[String]) extends Exercise

case class SqlSolution(userName: String, exerciseId: Int, solution: String)

// Table Definitions

trait SqlTableDefs extends TableDefs {
  self: HasDatabaseConfigProvider[JdbcProfile] =>

  import profile.api._

  object sqlScenarioes extends TableQuery(new SqlScenarioesTable(_)) {

    def completeScenarioes(implicit ec: ExecutionContext): Future[Seq[SqlCompleteScenario]] = db.run(
      this.result map (results => results map (res => SqlCompleteScenario(res, Seq.empty))))

  }

  val sqlExercises = TableQuery[SqlExercisesTable]

  implicit val SqlExTypeColumnType: BaseColumnType[SqlExerciseType] =
    MappedColumnType.base[SqlExerciseType, String](_.toString, str => Option(SqlExerciseType.valueOf(str)).getOrElse(SqlExerciseType.SELECT))

  class SqlScenarioesTable(tag: Tag) extends HasBaseValuesTable[SqlScenario](tag, "sql_scenarioes") {

    def shortName = column[String](SHORTNAME_NAME)

    def scriptFile = column[String](SCRIPTFILE_NAME)

    def * = (id, title, author, text, state, shortName, scriptFile) <> (SqlScenario.tupled, SqlScenario.unapply)

  }

  class SqlExercisesTable(tag: Tag) extends HasBaseValuesTable[SqlExercise](tag, "sql_exercises") {

    def scenarioId = column[Int]("scenario_id")

    def exerciseType = column[SqlExerciseType]("exercise_type")

    def hint = column[String](HINT_NAME)


    def pk = primaryKey("pk", (id, scenarioId))

    def scenarioFk = foreignKey("scenario_fk", scenarioId, sqlScenarioes)(_.id)


    def * = (id, title, author, text, state, scenarioId, exerciseType, hint.?) <> (SqlExercise.tupled, SqlExercise.unapply)

  }

}