package model.ebnf

import controllers.idExes.EbnfToolObject
import model.Enums.ExerciseState
import model._
import model.ebnf.EbnfConsts._
import model.ebnf.EbnfExerciseHelper._
import play.api.db.slick.HasDatabaseConfigProvider
import play.api.mvc.Call
import slick.jdbc.JdbcProfile

object EbnfExerciseHelper {

  val StartSymbol = "S"

  val termsJoinStr = ","

}

case class EbnfExercise(i: Int, ti: String, a: String, te: String, s: ExerciseState, terminals: String) extends Exercise with CompleteEx[EbnfExercise] {

  override val baseValues = BaseValues(i, ti, a, te, s)

  override val ex: HasBaseValues = this

  def getTerminals: Array[String] = terminals.split(termsJoinStr)

  def getTerminalsForForm: String = getTerminals.map(t => s"'$t'").mkString(" ")

  override def preview = ???

  override def renderListRest = ???

  override def exerciseRoutes: List[(Call, String)] = EbnfToolObject.exerciseRoutes(this)

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