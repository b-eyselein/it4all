package model.programming

import javax.inject.Inject
import model.Enums.ExerciseState
import model._
import model.programming.ProgConsts._
import model.programming.ProgDataTypes._
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.mvc.Call
import play.twirl.api.Html
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}
import scala.language.{implicitConversions, postfixOps}

case class ProgCompleteEx(ex: ProgExercise, inputTypes: Seq[InputType], sampleSolution: ProgSampleSolution, sampleTestData: Seq[CompleteSampleTestData])
  extends PartsCompleteEx[ProgExercise, ProgExPart] {

  // FIXME: only one solution?

  override def preview: Html = views.html.programming.progPreview.render(this)

  val inputCount: Int = inputTypes.size

  override def hasPart(partType: ProgExPart): Boolean = true
}

sealed trait CompleteTestData {

  val testData: TestData

  val inputs: Seq[TestDataInput]

  def write: String = (inputs map (_.input)) mkString " "

}

case class CompleteSampleTestData(testData: SampleTestData, inputs: Seq[SampleTestDataInput]) extends CompleteTestData

case class CompleteCommitedTestData(testData: CommitedTestData, inputs: Seq[CommitedTestDataInput]) extends CompleteTestData

// Case classes for tables

object ProgExercise {

  def tupled(t: (Int, String, String, String, ExerciseState, String, ProgDataType)): ProgExercise =
    ProgExercise(t._1, t._2, t._3, t._4, t._5, t._6, t._7)

  def apply(id: Int, title: String, author: String, text: String, state: ExerciseState, funName: String, outputType: ProgDataType): ProgExercise =
    new ProgExercise(BaseValues(id, title, author, text, state), funName, outputType)

  def unapply(arg: ProgExercise): Option[(Int, String, String, String, ExerciseState, String, ProgDataType)] =
    Some(arg.id, arg.title, arg.author, arg.text, arg.state, arg.functionName, arg.outputType)

}

case class ProgExercise(baseValues: BaseValues, functionName: String, outputType: ProgDataType) extends Exercise

case class InputType(id: Int, exerciseId: Int, inputType: ProgDataType)

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

class ProgTableDefs @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)
  extends HasDatabaseConfigProvider[JdbcProfile] with ExerciseTableDefs[ProgExercise, ProgCompleteEx] {

  import profile.api._

  def completeExForEx(ex: ProgExercise)(implicit ec: ExecutionContext): Future[ProgCompleteEx] = db.run(sampleSolForEx(ex) zip inputTypesForEx(ex) zip completeTestDataForEx(ex)) map {
    case ((sampleSol, inputTypes), ctds) =>

      val completeSampleTestData: Seq[CompleteSampleTestData] = ctds groupBy (_._1) mapValues (_ map (_._2)) map (ctd => CompleteSampleTestData(ctd._1, ctd._2.flatten)) toSeq

      ProgCompleteEx(ex, inputTypes, sampleSol, completeSampleTestData)
  }

  override def saveExerciseRest(compEx: ProgCompleteEx)(implicit ec: ExecutionContext): Future[Boolean] =
    (db.run(sampleSolutions += compEx.sampleSolution) map (_ => true) zip
      saveSeq[InputType](compEx.inputTypes, i => db.run(inputTypesQuery += i)) zip
      saveSeq[CompleteSampleTestData](compEx.sampleTestData, saveSampleTestdata)) map (f => f._1._1 && f._1._2 && f._2)

  private def saveSampleTestdata(sampleData: CompleteSampleTestData)(implicit ec: ExecutionContext): Future[Boolean] =
    db.run(sampleTestData += sampleData.testData) flatMap { _ => saveSeq[SampleTestDataInput](sampleData.inputs, i => db.run(sampleTestDataInputs += i)) }

  private def inputTypesForEx(ex: ProgExercise) = inputTypesQuery.filter(_.exerciseId === ex.id).result

  private def sampleSolForEx(ex: ProgExercise) = sampleSolutions.filter(_.exerciseId === ex.id).result.head

  private def completeTestDataForEx(ex: ProgExercise)(implicit ec: ExecutionContext) = sampleTestData.joinLeft(sampleTestDataInputs)
    .on { case (td, tdi) => td.exerciseId === tdi.exerciseId && td.id === tdi.testId }
    .filter(_._1.exerciseId === ex.id)
    .result

  def saveSolution(user: User, exercise: ProgCompleteEx, solution: String): Future[Int] =
    db.run(progSolutions insertOrUpdate ProgSolution(user.username, exercise.id, solution))

  def loadSolution(user: User, exercise: ProgCompleteEx): Future[Option[ProgSolution]] =
    db.run(progSolutions.filter(sol => sol.username === user.username && sol.exerciseId === exercise.id).result.headOption)

  def saveCompleteCommitedTestData(compCommTd: Seq[CompleteCommitedTestData])(implicit ec: ExecutionContext): Future[Boolean] =
    Future.sequence(compCommTd map saveCompCommTestData) map (_ forall identity)

  private def saveCompCommTestData(compCommTestData: CompleteCommitedTestData)(implicit ec: ExecutionContext): Future[Boolean] =
    db.run(commitedTestData += compCommTestData.testData) flatMap { _ =>
      saveSeq[CommitedTestDataInput](compCommTestData.inputs, inp => db.run(commitedTestDataInput += inp))
    }


  // Table Queries

  val progExercises = TableQuery[ProgExercisesTable]

  val inputTypesQuery = TableQuery[InputTypesTable]

  val sampleSolutions = TableQuery[ProgSampleSolutionsTable]

  val sampleTestData = TableQuery[SampleTestDataTable]

  val commitedTestData = TableQuery[CommitedTestDataTable]


  val sampleTestDataInputs = TableQuery[SampleTestDataInputsTable]

  val commitedTestDataInput = TableQuery[CommitedTestdataInputTable]

  // Dependent tables

  val progSolutions = TableQuery[ProgSolutionTable]

  // Other table stuff

  override type ExTableDef = ProgExercisesTable

  override val exTable = progExercises

  // Implicit column types

  implicit val ProgLanguageColumnType: BaseColumnType[ProgLanguage] =
    MappedColumnType.base[ProgLanguage, String](_.name, str => ProgLanguage.valueOf(str) getOrElse ProgLanguage.STANDARD_LANG)

  implicit val ProgDataTypesColumnType: BaseColumnType[ProgDataType] =
    MappedColumnType.base[ProgDataType, String](_.typeName, str => ProgDataTypes.byName(str) getOrElse ProgDataTypes.STRING)

  // Tables

  class ProgExercisesTable(tag: Tag) extends HasBaseValuesTable[ProgExercise](tag, "prog_exercises") {

    def functionName = column[String]("function_name")

    def outputType = column[ProgDataType]("output_type")


    def pk = primaryKey("pk", id)


    def * = (id, title, author, text, state, functionName, outputType) <> (ProgExercise.tupled, ProgExercise.unapply)

  }

  class InputTypesTable(tag: Tag) extends Table[InputType](tag, "prog_input_types") {

    def id = column[Int](ID_NAME)

    def exerciseId = column[Int]("exercise_id")

    def inputType = column[ProgDataType]("input_type")


    def pk = primaryKey("pk", (id, exerciseId))

    def exerciseFk = foreignKey("exercise_fk", exerciseId, progExercises)(_.id)


    def * = (id, exerciseId, inputType) <> (InputType.tupled, InputType.unapply)

  }

  class ProgSampleSolutionsTable(tag: Tag) extends Table[ProgSampleSolution](tag, "prog_samples") {

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

    def username = column[String]("username")


    def pk = primaryKey("pk", (username, id, testId, exerciseId))

    def commitedTestdataFk = foreignKey("commited_testdata_fk", (testId, exerciseId, username), commitedTestData)(commitedTD => (commitedTD.id, commitedTD.exerciseId, commitedTD.username))


    def * = (id, testId, exerciseId, input, username) <> (CommitedTestDataInput.tupled, CommitedTestDataInput.unapply)

  }

  // Solutions of users

  class ProgSolutionTable(tag: Tag) extends Table[ProgSolution](tag, "prog_solutions") {

    def username = column[String]("username")

    def exerciseId = column[Int]("exercise_id")

    def solution = column[String]("solution")


    def pk = primaryKey("pk", (username, exerciseId))

    def exerciseFk = foreignKey("exercise_fk", exerciseId, progExercises)(_.id)

    def userFk = foreignKey("user_fk", username, users)(_.username)


    def * = (username, exerciseId, solution) <> (ProgSolution.tupled, ProgSolution.unapply)

  }

}