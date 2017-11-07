package model.programming

import com.fasterxml.jackson.annotation.JsonProperty
import model.Enums.ExerciseState
import model.core.CompleteEx
import model.core.StringConsts._
import model.{Exercise, TableDefs}
import play.api.db.slick.HasDatabaseConfigProvider
import play.api.libs.functional.syntax.toFunctionalBuilderOps
import play.api.libs.json.{JsPath, Reads}
import play.twirl.api.Html
import slick.jdbc.JdbcProfile

object ProgExerciseReads {
  implicit def progExReads: Reads[ProgExercise] = (
    (JsPath \ ID_NAME).read[Int] and
      (JsPath \ TITLE_NAME).read[String] and
      (JsPath \ AUTHOR_NAME).read[String] and
      (JsPath \ TEXT_NAME).read[List[String]] and
      (JsPath \ STATE_NAME).read[String] and
      (JsPath \ FUNCTIONNAME_NAME).read[String] and
      (JsPath \ INPUTCOUNT_NAME).read[Int]
    ) ((i, ti, a, te, s, fn, ic) => ProgExercise(i, ti, a, te.mkString, ExerciseState.valueOf(s), fn, ic))
}

case class ProgCompleteEx(ex: ProgExercise) extends CompleteEx[ProgExercise]

case class ProgExercise(i: Int, ti: String, a: String, te: String, s: ExerciseState,
                        @JsonProperty(required = true) functionName: String,
                        @JsonProperty(required = true) inputCount: Int)
  extends Exercise(i, ti, a, te, s) {

  override def renderRest: Html = new Html(
    s"""<td>$functionName</td>
       |<td>$inputCount</td>""".stripMargin)

}


trait ProgExercises extends TableDefs {
  self: HasDatabaseConfigProvider[JdbcProfile] =>

  import profile.api._

  val progExercises = TableQuery[ProgExercisesTable]

  class ProgExercisesTable(tag: Tag) extends HasBaseValuesTable[ProgExercise](tag, "prog_exercises") {

    def functionName = column[String]("function_name")

    def inputCount = column[Int]("input_count")

    def * = (id, title, author, text, state, functionName, inputCount) <> (ProgExercise.tupled, ProgExercise.unapply)
  }

}