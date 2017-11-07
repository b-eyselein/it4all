package model.ebnf

import com.fasterxml.jackson.annotation.{JsonIgnore, JsonProperty}
import model.Enums.ExerciseState
import model.core.CompleteEx
import model.core.StringConsts._
import model.ebnf.EbnfExerciseHelper._
import model.{Exercise, TableDefs}
import play.api.db.slick.HasDatabaseConfigProvider
import play.api.libs.functional.syntax.toFunctionalBuilderOps
import play.api.libs.json.{JsPath, Reads}
import play.twirl.api.Html
import slick.jdbc.JdbcProfile

object EbnfExerciseHelper {

  val StartSymbol = "S"

  val termsJoinStr = ","

}

object EbnfExerciseReads {

  implicit def ebnfExReads: Reads[EbnfExercise] = (
    (JsPath \ ID_NAME).read[Int] and
      (JsPath \ TITLE_NAME).read[String] and
      (JsPath \ AUTHOR_NAME).read[String] and
      (JsPath \ TEXT_NAME).read[List[String]] and
      (JsPath \ STATE_NAME).read[String] and
      (JsPath \ "terminals").read[List[String]]
    ) ((i, ti, a, te, s, terms) => EbnfExercise(i, ti, a, te.mkString, ExerciseState.valueOf(s), terms.mkString(termsJoinStr)))
}

case class EbnfCompleteEx(ex: EbnfExercise) extends CompleteEx[EbnfExercise]

case class EbnfExercise(i: Int, ti: String, a: String, te: String, s: ExerciseState,
                        @JsonProperty(required = true) terminals: String) extends Exercise(i, ti, a, te, s) {

  def getTerminals: Array[String] = terminals.split(termsJoinStr)

  @JsonIgnore
  def getTerminalsForForm: String = getTerminals.map(t => s"'$t'").mkString(" ")

  override def renderRest: Html = new Html(s"<td>$terminals</td>")

}

trait EbnfExercises extends TableDefs {
  self: HasDatabaseConfigProvider[JdbcProfile] =>

  import profile.api._

  class EbnfExerciseTable(tag: Tag) extends HasBaseValuesTable[EbnfExercise](tag, "ebnf_exercises") {

    def terminals = column[String]("terminals")

    def * = (id, title, author, text, state, terminals) <> (EbnfExercise.tupled, EbnfExercise.unapply)
  }

  lazy val ebnfExercises = TableQuery[EbnfExerciseTable]

}