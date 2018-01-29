package model.rose

import javax.inject.Inject

import controllers.exes.idPartExes.RoseToolObject
import model.Enums.ExerciseState
import model._
import model.programming.ProgDataTypes.ProgDataType
import model.programming.{ProgDataTypes, ProgLanguage}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.mvc.Call
import play.twirl.api.Html
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}
import scala.language.{implicitConversions, postfixOps}

case class RoseCompleteEx(ex: RoseExercise, sampleSolution: RoseSampleSolution) extends PartsCompleteEx[RoseExercise, RoseExPart] {

  // FIXME: only one solution?

  override def preview: Html = views.html.rose.rosePreview.render(this)

  override def exerciseRoutes: Map[Call, String] = RoseToolObject.exerciseRoutes(this)

  override def hasPart(partType: RoseExPart): Boolean = true

  def declaration: String = if (ex.isMultiplayer) {
    """def run(self) -> None:
      |  pass""".stripMargin
  } else {
    """def act(self) -> Action:
      |  pass""".stripMargin
  }

}

sealed trait CompleteTestData {

  val testData: TestData

  val inputs: Seq[TestDataInput]

  def write: String = (inputs map (_.input)) mkString " "

}

case class CompleteSampleTestData(testData: SampleTestData, inputs: Seq[SampleTestDataInput]) extends CompleteTestData

case class CompleteCommitedTestData(testData: CommitedTestData, inputs: Seq[CommitedTestDataInput]) extends CompleteTestData

// Case classes for tables

object RoseExercise {

  def tupled(t: (Int, String, String, String, ExerciseState, Boolean)): RoseExercise =
    RoseExercise(t._1, t._2, t._3, t._4, t._5, t._6)

  def apply(id: Int, title: String, author: String, text: String, state: ExerciseState, isMultiplayer: Boolean): RoseExercise =
    new RoseExercise(BaseValues(id, title, author, text, state), isMultiplayer)

  def unapply(arg: RoseExercise): Option[(Int, String, String, String, ExerciseState, Boolean)] =
    Some(arg.id, arg.title, arg.author, arg.text, arg.state, arg.isMultiplayer)

}

case class RoseExercise(baseValues: BaseValues, isMultiplayer: Boolean) extends Exercise

case class InputType(id: Int, exerciseId: Int, inputType: ProgDataType)

case class RoseSampleSolution(exerciseId: Int, language: ProgLanguage, solution: String)

sealed trait TestData {

  val id        : Int
  val exerciseId: Int
  val output    : String

}

trait TestDataInput {

  val id        : Int
  val testId    : Int
  val exerciseId: Int
  val input     : String

}

case class SampleTestData(id: Int, exerciseId: Int, output: String) extends TestData

case class CommitedTestData(id: Int, exerciseId: Int, username: String, output: String, state: ExerciseState) extends TestData


case class SampleTestDataInput(id: Int, testId: Int, exerciseId: Int, input: String) extends TestDataInput

case class CommitedTestDataInput(id: Int, testId: Int, exerciseId: Int, input: String, username: String) extends TestDataInput

// Dependent on users and roseExercises

case class RoseSolution(username: String, exerciseId: Int, solution: String)

// Tables

class RoseTableDefs @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)
  extends HasDatabaseConfigProvider[JdbcProfile] with ExerciseTableDefs[RoseExercise, RoseCompleteEx] {

  import profile.api._

  // Table Queries

  val roseExercises = TableQuery[RoseExercisesTable]

  // Dependent tables

  val roseSolutions = TableQuery[RoseSolutionTable]

  // Other table stuff

  override type ExTableDef = RoseExercisesTable

  override val exTable = roseExercises

  override protected def completeExForEx(ex: RoseExercise)(implicit ec: ExecutionContext): Future[RoseCompleteEx] =
    Future(RoseCompleteEx(ex, RoseSampleSolution(ex.id, ProgLanguage.STANDARD_LANG, "")))

  override protected def saveExerciseRest(compEx: RoseCompleteEx)(implicit ec: ExecutionContext): Future[Boolean] = ???

  // Implicit column types

  implicit val ProgLanguageColumnType: BaseColumnType[ProgLanguage] =
    MappedColumnType.base[ProgLanguage, String](_.name, str => ProgLanguage.valueOf(str) getOrElse ProgLanguage.STANDARD_LANG)

  implicit val ProgDataTypesColumnType: BaseColumnType[ProgDataType] =
    MappedColumnType.base[ProgDataType, String](_.typeName, str => ProgDataTypes.byName(str) getOrElse ProgDataTypes.STRING)

  // Tables

  class RoseExercisesTable(tag: Tag) extends HasBaseValuesTable[RoseExercise](tag, "rose_exercises") {

    def isMultiplayer = column[Boolean]("is_mp")


    def pk = primaryKey("pk", id)


    def * = (id, title, author, text, state, isMultiplayer) <> (RoseExercise.tupled, RoseExercise.unapply)

  }

  // Solutions of users

  class RoseSolutionTable(tag: Tag) extends Table[RoseSolution](tag, "rose_solutions") {

    def username = column[String]("username")

    def exerciseId = column[Int]("exercise_id")

    def solution = column[String]("solution")


    def pk = primaryKey("pk", (username, exerciseId))

    def exerciseFk = foreignKey("exercise_fk", exerciseId, roseExercises)(_.id)

    def userFk = foreignKey("user_fk", username, users)(_.username)


    def * = (username, exerciseId, solution) <> (RoseSolution.tupled, RoseSolution.unapply)

  }

}