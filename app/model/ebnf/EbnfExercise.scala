package model.ebnf

import com.fasterxml.jackson.annotation.JsonIgnore
import model.Enums.ExerciseState
import model.ebnf.EbnfConsts._
import model.ebnf.EbnfExerciseHelper._
import model.{CompleteEx, Exercise, HasBaseValues, TableDefs}
import play.api.db.slick.HasDatabaseConfigProvider
import play.twirl.api.Html
import slick.jdbc.JdbcProfile

object EbnfExerciseHelper {

  val StartSymbol = "S"

  val termsJoinStr = ","

}


case class EbnfExercise(i: Int, ti: String, a: String, te: String, s: ExerciseState, terminals: String) extends Exercise(i, ti, a, te, s) with CompleteEx[EbnfExercise] {

  override val ex: HasBaseValues = this

  def getTerminals: Array[String] = terminals.split(termsJoinStr)

  @JsonIgnore
  def getTerminalsForForm: String = getTerminals.map(t => s"'$t'").mkString(" ")

  override def renderRest: Html = new Html(s"<td>$terminals</td>")

}

trait EbnfExercises extends TableDefs {
  self: HasDatabaseConfigProvider[JdbcProfile] =>

  import profile.api._

  class EbnfExerciseTable(tag: Tag) extends HasBaseValuesTable[EbnfExercise](tag, "ebnf_exercises") {

    def terminals = column[String](TERMINALS)

    def * = (id, title, author, text, state, terminals) <> (EbnfExercise.tupled, EbnfExercise.unapply)
  }

  lazy val ebnfExercises = TableQuery[EbnfExerciseTable]

}