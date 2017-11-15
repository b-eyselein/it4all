package model.programming

import model.Enums.ExerciseState
import model._
import play.api.db.slick.HasDatabaseConfigProvider
import play.twirl.api.Html
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

case class ProgCompleteEx(ex: ProgExercise, sampleTestData: Seq[CompleteSampleTestData]) extends CompleteEx[ProgExercise] {

  override def renderRest: Html = new Html(
    s"""<td>${ex.functionName}</td>
       |<td>${ex.inputCount}</td>""".stripMargin)

}

case class CompleteSampleTestData(sampleTestData: SampleTestData, inputs: Seq[SampleTestDataInput])

case class CompleteCommitedTestData(commitedTestData: CommitedTestData, inputs: Seq[CommitedTestDataInput])

// Case classes for tables

object ProgExercise {

  def tupled(t: (Int, String, String, String, ExerciseState, String, Int)): ProgExercise =
    ProgExercise(t._1, t._2, t._3, t._4, t._5, t._6, t._7)

  def apply(id: Int, title: String, author: String, text: String, state: ExerciseState, funName: String, inpCount: Int): ProgExercise =
    new ProgExercise(BaseValues(id, title, author, text, state), funName, inpCount)

  def unapply(arg: ProgExercise): Option[(Int, String, String, String, ExerciseState, String, Int)] =
    Some(arg.id, arg.title, arg.author, arg.text, arg.state, arg.functionName, arg.inputCount)

}

case class ProgExercise(bv: BaseValues, functionName: String, inputCount: Int) extends Exercise(bv)

case class ProgSampleSolution(exerciseId: Int, language: ProgLanguage, solution: String)

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

// Dependent on users and progExercises

case class ProgSolution(username: String, exerciseId: Int, solution: String)

// Tables

trait ProgTableDefs extends TableDefs {
  self: HasDatabaseConfigProvider[JdbcProfile] =>

  import profile.api._

  object progExercises extends ExerciseTableQuery[ProgExercise, ProgCompleteEx, ProgExercisesTable](new ProgExercisesTable(_)) {

    override def saveCompleteEx(completeEx: ProgCompleteEx)(implicit ec: ExecutionContext): Future[Int] = db.run(
      (this insertOrUpdate completeEx.ex) zip
        // FIXME: implement!
        DBIO.sequence(completeEx.sampleTestData map (sampleTD => sampleTestData insertOrUpdate sampleTD.sampleTestData))
    ) map (_._1)

    override protected def completeExForEx(ex: ProgExercise)(implicit ec: ExecutionContext): Future[ProgCompleteEx] = ???

  }

  val sampleTestData = TableQuery[SampleTestDataTable]

  val commitedTestData = TableQuery[CommitedTestDataTable]


  val sampleTestDataInputs = TableQuery[SampleTestDataInputsTable]

  val commitedTestDataInput = TableQuery[CommitedTestdataInputTable]

  // Dependent tables

  val progSolutions = TableQuery[ProgSolutionTable]

  // Implicit column types

  implicit val ProgLanguageColumnType: BaseColumnType[ProgLanguage] =
    MappedColumnType.base[ProgLanguage, String](_.name, str => Try(ProgLanguage.valueOf(str)).getOrElse(ProgLanguage.PYTHON_3))

  // Tables

  class ProgExercisesTable(tag: Tag) extends HasBaseValuesTable[ProgExercise](tag, "prog_exercises") {

    def functionName = column[String]("function_name")

    def inputCount = column[Int]("input_count")


    def * = (id, title, author, text, state, functionName, inputCount) <> (ProgExercise.tupled, ProgExercise.unapply)

  }

  // Samples for validation of test data
//  case class ProgSampleSolution(exerciseId: Int, language: ProgLanguage, solution: String)

  class ProgSampleSolutionTable(tag: Tag) extends Table[ProgSampleSolution](tag, "prog_samples") {

    def exerciseId = column[Int]("exercise_id")

    def language = column[ProgLanguage]("language")

    def solution = column[String]("solution")


    def pk = primaryKey("pk", (exerciseId, language))

    def exerciseFk = foreignKey("exercise_fk", exerciseId, progExercises)(_.id)


    def * = (exerciseId, language, solution) <> (ProgSampleSolution.tupled, ProgSampleSolution.unapply)

  }


  // Test data

  abstract class ITestDataTable[T <: TestData](tag: Tag, name: String) extends Table[T](tag, name) {

    def id = column[Int]("id")

    def exerciseId = column[Int]("exercise_id")

    def output = column[String]("output")


    def exerciseFk = foreignKey("exercise_fk", exerciseId, progExercises)(_.id)

  }

  class SampleTestDataTable(tag: Tag) extends ITestDataTable[SampleTestData](tag, "prog_sample_testdata") {

    def pk = primaryKey("pk", (id, exerciseId))


    def * = (id, exerciseId, output) <> (SampleTestData.tupled, SampleTestData.unapply)

  }

  class CommitedTestDataTable(tag: Tag) extends ITestDataTable[CommitedTestData](tag, "prog_commited_testdata") {

    // TODO: 1.sql prog_commited_testdata

    def username = column[String]("username")

    def state = column[ExerciseState]("approval_state")


    def pk = primaryKey("pk", (id, exerciseId, username))

    def userFk = foreignKey("user_fk", username, users)(_.username)


    def * = (id, exerciseId, username, output, state) <> (CommitedTestData.tupled, CommitedTestData.unapply)

  }

  // Inputs for test data

  abstract class TestdataInputTable[I <: TestDataInput](tag: Tag, name: String) extends Table[I](tag, name) {

    def id = column[Int]("id")

    def testId = column[Int]("test_id")

    def exerciseId = column[Int]("exercise_id")

    def input = column[String]("input")

  }

  class SampleTestDataInputsTable(tag: Tag) extends TestdataInputTable[SampleTestDataInput](tag, "prog_sample_testdata_input") {

    def pk = primaryKey("pk", (id, testId, exerciseId))

    def sampleTestDataFk = foreignKey("sample_testdata_fk", (testId, exerciseId), sampleTestData)(sampleTD => (sampleTD.id, sampleTD.exerciseId))


    def * = (id, testId, exerciseId, input) <> (SampleTestDataInput.tupled, SampleTestDataInput.unapply)

  }

  class CommitedTestdataInputTable(tag: Tag) extends TestdataInputTable[CommitedTestDataInput](tag, "prog_commited_testdata_input") {

    // TODO: 1.sql prog_commited_testdata_input

    def username = column[String]("username")


    def pk = primaryKey("pk", (username, id, testId, exerciseId))

    def commitedTestdataFk = foreignKey("commited_testdata_fk", (testId, exerciseId, username), commitedTestData)(commitedTD => (commitedTD.id, commitedTD.exerciseId, commitedTD.username))


    def * = (id, testId, exerciseId, input, username) <> (CommitedTestDataInput.tupled, CommitedTestDataInput.unapply)

  }

  // Solutions of users

  class ProgSolutionTable(tag: Tag) extends Table[ProgSolution](tag, "prog_solutions") {

    // TODO: 1.sql prog_solutions

    def username = column[String]("username")

    def exerciseId = column[Int]("exercise_id")

    def solution = column[String]("solution")


    def pk = primaryKey("pk", (username, exerciseId))

    def exerciseFk = foreignKey("exercise_fk", exerciseId, progExercises)(_.id)

    def userFk = foreignKey("user_fk", username, users)(_.username)


    def * = (username, exerciseId, solution) <> (ProgSolution.tupled, ProgSolution.unapply)

  }

}