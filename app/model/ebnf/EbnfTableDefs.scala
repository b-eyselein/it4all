package model.ebnf

import controllers.exes.idExes.EbnfToolObject
import model.Enums.ExerciseState
import model._
import model.ebnf.EbnfConsts._
import play.api.db.slick.HasDatabaseConfigProvider
import play.api.mvc.Call
import play.twirl.api.Html
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

// Complete classes for use

case class EbnfCompleteExercise(ex: EbnfExercise, testdata: Seq[EbnfTestData]) extends CompleteEx[EbnfExercise] {

  override def preview: Html = views.html.ebnf.ebnfPreview(this)

  override def renderListRest: Html = ???

  override def exerciseRoutes: Map[Call, String] = EbnfToolObject.exerciseRoutes(this)

}

// Case classes for database

object EbnfExercise {

  val StartSymbol = "S"

  val termsJoinStr = ","

  def tupled(t: (Int, String, String, String, ExerciseState, String)): EbnfExercise = EbnfExercise(t._1, t._2, t._3, t._4, t._5, t._6)

  def apply(id: Int, title: String, author: String, text: String, state: ExerciseState, terminals: String): EbnfExercise =
    new EbnfExercise(BaseValues(id, title, author, text, state), terminals)

  def apply(baseValues: BaseValues, terminals: Seq[String]): EbnfExercise = EbnfExercise(baseValues, terminals mkString termsJoinStr)

  def unapply(arg: EbnfExercise): Option[(Int, String, String, String, ExerciseState, String)] =
    Some((arg.id, arg.title, arg.author, arg.text, arg.state, arg.joinedTerminals))


}

case class EbnfExercise(override val baseValues: BaseValues, joinedTerminals: String) extends Exercise {

  def terminals: Seq[Terminal] = joinedTerminals split EbnfExercise.termsJoinStr map Terminal

}

case class EbnfTestData(exerciseId: Int, testData: String)

trait EbnfTableDefs extends TableDefs {
  self: HasDatabaseConfigProvider[JdbcProfile] =>

  import profile.api._

  val ebnfExercises = TableQuery[EbnfExerciseTable]

  val ebnfTestData = TableQuery[EbnfTestDataTable]

  // Reading

  def ebnfCompleteExes(implicit ec: ExecutionContext): Future[Seq[EbnfCompleteExercise]] =
    db.run(ebnfExercises.result) map (_ map (ex => EbnfCompleteExercise(ex, testdata = Seq.empty)))

  def ebnfCompleteExById(id: Int)(implicit ec: ExecutionContext): Future[Option[EbnfCompleteExercise]] =
    db.run(ebnfExercises.filter(_.id === id).result.headOption) map (_ map (ex => EbnfCompleteExercise(ex, testdata = Seq.empty)))

  // Saving

  def ebnfSaveCompleteEx(ex: EbnfCompleteExercise)(implicit ec: ExecutionContext): Future[Boolean] =
    db.run(ebnfExercises += ex.ex) map (_ => true) recover { case e: Exception => false }

  // Table defs

  class EbnfExerciseTable(tag: Tag) extends HasBaseValuesTable[EbnfExercise](tag, "ebnf_exercises") {

    def terminals = column[String](TERMINALS)


    def pk = primaryKey("pk", id)


    def * = (id, title, author, text, state, terminals) <> (EbnfExercise.tupled, EbnfExercise.unapply)

  }

  class EbnfTestDataTable(tag: Tag) extends Table[EbnfTestData](tag, "ebnf_testdata") {

    def exerciseId = column[Int]("exercise_id")

    def testData = column[String]("test_data")


    def pk = primaryKey("pk", (exerciseId, testData))

    def exerciseFk = foreignKey("exercise_fk", exerciseId, ebnfExercises)(_.id)


    def * = (exerciseId, testData) <> (EbnfTestData.tupled, EbnfTestData.unapply)

  }

}