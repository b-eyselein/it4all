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

case class EbnfSolution(exerciseId: Int, username: String, solution: RulesList)

trait EbnfTableDefs extends TableDefs {
  self: HasDatabaseConfigProvider[JdbcProfile] =>

  import profile.api._

  val ebnfExercises = TableQuery[EbnfExerciseTable]

  val ebnfTestData = TableQuery[EbnfTestDataTable]

  val ebnfSolutions = TableQuery[EbnfSolutionsTable]

  // Reading

  def ebnfCompleteExes(implicit ec: ExecutionContext): Future[Seq[EbnfCompleteExercise]] =
    db.run(ebnfExercises.result) map (_ map (ex => EbnfCompleteExercise(ex, testdata = Seq.empty)))

  def ebnfCompleteExById(id: Int)(implicit ec: ExecutionContext): Future[Option[EbnfCompleteExercise]] =
    db.run(ebnfExercises.filter(_.id === id).result.headOption) flatMap {
      case None     => Future(None)
      case Some(ex) => db.run(ebnfTestData.filter(_.exerciseId === id).result) map (testdata => Some(EbnfCompleteExercise(ex, testdata)))
    }

  def readEbnfSolution(username: String, exerciseId: Int): Future[Option[EbnfSolution]] =
    db.run(ebnfSolutions.filter(sol => sol.exerciseId === exerciseId && sol.username === username).result.headOption)

  // Saving

  def ebnfSaveCompleteEx(ex: EbnfCompleteExercise)(implicit ec: ExecutionContext): Future[Boolean] =
    db.run(ebnfExercises.filter(_.id === ex.id).delete) flatMap { _ =>
      db.run(ebnfExercises += ex.ex) flatMap { _ =>
        Future.sequence(ex.testdata map (td => db.run(ebnfTestData += td) map (_ => true) recover { case _: Exception => false })) map (_.forall(identity))
      }
    }

  def saveEbnfSolution(user: User, exercise: EbnfCompleteExercise, rulesList: RulesList): Future[Int] =
    db.run(ebnfSolutions insertOrUpdate EbnfSolution(exercise.id, user.username, rulesList))

  // Column Type

  implicit val GrammarColumnType: BaseColumnType[RulesList] =
    MappedColumnType.base[RulesList, String](_.toDbString, Grammar.rulesListFromDbString)

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


  class EbnfSolutionsTable(tag: Tag) extends Table[EbnfSolution](tag, "ebnf_solutions") {

    def exerciseId = column[Int]("exercise_id")

    def username = column[String]("username")

    def solution = column[RulesList]("solution")


    def pk = primaryKey("pk", (exerciseId, username))

    def exerciseFk = foreignKey("exercise_fk", exerciseId, ebnfExercises)(_.id)

    def userFk = foreignKey("user_fk", username, users)(_.username)


    def * = (exerciseId, username, solution) <> (EbnfSolution.tupled, EbnfSolution.unapply)

  }

}