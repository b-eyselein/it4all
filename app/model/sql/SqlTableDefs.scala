package model.sql

import com.fasterxml.jackson.annotation.{JsonIgnore, JsonProperty}
import controllers.core.BaseExerciseController._
import model.Enums.ExerciseState
import model.core.ExerciseCollection
import model.core.StringConsts._
import model.sql.SqlEnums.{SqlExTag, SqlExerciseType}
import model.{DbExercise, TableDefs}
import play.api.db.slick.HasDatabaseConfigProvider
import play.api.libs.functional.syntax.toFunctionalBuilderOps
import play.api.libs.json.{JsPath, Reads}
import slick.jdbc.JdbcProfile

object SqlReads {
  implicit def sqlScenarioReads: Reads[SqlScenario] = (
    (JsPath \ ID_NAME).read[Int] and
      (JsPath \ TITLE_NAME).read[String] and
      (JsPath \ AUTHOR_NAME).read[String] and
      (JsPath \ TEXT_NAME).read[List[String]] and
      (JsPath \ STATE_NAME).read[String] and
      (JsPath \ SHORTNAME_NAME).read[String] and
      (JsPath \ SCRIPTFILE_NAME).read[String]
    ) ((i, ti, a, te, s, shortName, scriptFile) => SqlScenario(i, ti, a, te.mkString, ExerciseState.valueOf(s), shortName, scriptFile))
}

case class SqlScenario(i: Int, ti: String, a: String, te: String, s: ExerciseState,
                       @JsonProperty(required = true) shortName: String,
                       @JsonProperty(required = true) scriptFile: String)
  extends ExerciseCollection[SqlExercise](i, ti, a, te, s) {

  override def exercises: List[SqlExercise] = List.empty

  def getBorder(exType: SqlExerciseType, start: Int): Int =
    (Math.min(getExercisesByType(exType).size, start + STEP) / STEP) * STEP

  def getExercises(exType: SqlExerciseType, start: Int): List[SqlExercise] = {
    val ex = getExercisesByType(exType)
    ex.slice(Math.max(start, 0), Math.min(start + STEP, ex.size))
  }

  def getExercisesByType(exType: SqlExerciseType): List[SqlExercise] = exercises.filter(_.exerciseType == exType)

  @JsonIgnore
  def imageUrl: String = shortName + ".png"

  @JsonIgnore
  def numOfSites: Int = exercises.size / STEP + 1

}

object SqlExerciseHelper {
  val SampleJoinChar = "#"
}

case class SqlExercise(i: Int, ti: String, a: String, te: String, s: ExerciseState, exerciseType: SqlExerciseType, sqlTags: String, hint: String)
  extends DbExercise(i, ti, a, te, s) {

  override def getTags: List[SqlExTag] = {
    if (sqlTags.isEmpty) List.empty
    else sqlTags.split(SqlExerciseHelper.SampleJoinChar).map(SqlExTag.valueOf).toList
  }

  def scenario: SqlScenario = null

  def samples: List[SqlSample] = List.empty

  @JsonProperty(value = "tags", required = true)
  def tagsForJson: List[String] = sqlTags.split(SqlExerciseHelper.SampleJoinChar).toList

}

case class SqlSolution(userName: String, exerciseId: Int, solution: String)


trait SqlTableDefs extends TableDefs {
  self: HasDatabaseConfigProvider[JdbcProfile] =>

  import profile.api._

  val sqlScenarioes = TableQuery[SqlScenarioesTable]

  val sqlExercises = TableQuery[SqlExercisesTable]

  implicit val SqlExTypeColumnType: BaseColumnType[SqlExerciseType] =
    MappedColumnType.base[SqlExerciseType, String](_.toString, str => Option(SqlExerciseType.valueOf(str)).getOrElse(SqlExerciseType.SELECT))

  class SqlScenarioesTable(tag: Tag) extends HasBaseValuesTable[SqlScenario](tag, "sql_scenarioes") {

    def shortName = column[String](SHORTNAME_NAME)

    def scriptFile = column[String](SCRIPTFILE_NAME)

    def * = (id, title, author, text, state, shortName, scriptFile) <> (SqlScenario.tupled, SqlScenario.unapply)

  }

  class SqlExercisesTable(tag: Tag) extends HasBaseValuesTable[SqlExercise](tag, "sql_exercises") {

    def exerciseType = column[SqlExerciseType]("exercise_type")

    def tags = column[String](TAGS_NAME)

    def hint = column[String](HINT_NAME)

    def * = (id, title, author, text, state, exerciseType, tags, hint) <> (SqlExercise.tupled, SqlExercise.unapply)
  }

}