package model.programming

import javax.inject.Inject
import model.Enums.ExerciseState
import model._
import model.persistence.SingleExerciseTableDefs
import model.programming.ProgConsts._
import model.programming.ProgDataTypes._
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.libs.json.{JsArray, JsObject, JsValue, Json}
import play.twirl.api.Html
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}
import scala.language.{implicitConversions, postfixOps}

// Wrapper classes

class ProgCompleteExWrapper(val compEx: ProgCompleteEx) extends CompleteExWrapper {

  override type Ex = ProgExercise

  override type CompEx = ProgCompleteEx

}

// Classes for user

case class ProgCompleteEx(ex: ProgExercise, inputTypes: Seq[ProgInput], sampleSolutions: Seq[ProgSampleSolution], sampleTestData: Seq[CompleteSampleTestData])
  extends PartsCompleteEx[ProgExercise, ProgExPart] {

  override def preview: Html = views.html.programming.progPreview(this)

  val inputCount: Int = inputTypes.size

  override def hasPart(partType: ProgExPart): Boolean = partType match {
    case Implementation  => true
    case ActivityDiagram => true
    // TODO: Creation of test data is currently disabled
    case TestdataCreation => false
  }

  override def wrapped: CompleteExWrapper = new ProgCompleteExWrapper(this)

}

sealed trait CompleteTestData {

  val testData: TestData

  val inputs: Seq[TestDataInput]

  def write: String = (inputs map (_.input)) mkString " "

}

case class CompleteSampleTestData(testData: SampleTestData, inputs: Seq[SampleTestDataInput]) extends CompleteTestData

case class CompleteCommitedTestData(testData: CommitedTestData, inputs: Seq[CommitedTestDataInput]) extends CompleteTestData {

  def toJson: JsObject = Json.obj(
    "id" -> testData.id,
    "exerciseId" -> testData.exerciseId,
    "username" -> testData.username,
    "output" -> testData.output,
    "state" -> testData.state.name,
    "inputs" -> JsArray(inputs map (_.toJson))
  )

}

// Case classes for tables


case class ProgExercise(id: Int, title: String, author: String, text: String, state: ExerciseState, functionName: String, outputType: ProgDataType) extends Exercise {

  def this(baseValues: (Int, String, String, String, ExerciseState), functionName: String, outputType: ProgDataType) =
    this(baseValues._1, baseValues._2, baseValues._3, baseValues._4, baseValues._5, functionName, outputType)

}

case class ProgInput(id: Int, exerciseId: Int, inputName: String, inputType: ProgDataType)

case class ProgSampleSolution(exerciseId: Int, language: ProgLanguage, base: String, solution: String, testMain: String)

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

case class CommitedTestDataInput(id: Int, testId: Int, exerciseId: Int, input: String, username: String) extends TestDataInput {

  def toJson: JsObject = Json.obj(
    "id" -> id, "testId" -> testId, "exerciseId" -> exerciseId, "input" -> input, "username" -> username
  )

}

// Solution types

object TestDataSolution extends JsonFormat {

  def readTestDataFromJson(jsonStr: String): Seq[CompleteCommitedTestData] = Json.parse(jsonStr).asArray {
    jsValue =>

      def readTestData(jsObject: JsObject): Option[CommitedTestData] = for {
        id <- jsObject.intField("id")
        exerciseId <- jsObject.intField("exerciseId")
        username <- jsObject.stringField("username")
        output <- jsObject.stringField("output")
        state <- jsObject.enumField("state", ExerciseState.valueOf)
      } yield CommitedTestData(id, exerciseId, username, output, state)

      def readInput(jsValue: JsValue): Option[CommitedTestDataInput] = jsValue.asObj flatMap { jsObj =>
        for {
          id <- jsObj.intField("id")
          testId <- jsObj.intField("testId")
          exerciseId <- jsObj.intField("exerciseId")
          input <- jsObj.stringField("input")
          username <- jsObj.stringField("username")
        } yield CommitedTestDataInput(id, testId, exerciseId, input, username)

      }

      for {
        jsObj <- jsValue.asObj
        testData <- readTestData(jsObj)
        inputs <- jsObj.arrayField("inputs", readInput)
      } yield CompleteCommitedTestData(testData, inputs)

  } getOrElse Seq.empty

}

object ProgSolution {

  def tupled(t: (String, Int, ProgExPart, ProgLanguage, String)): ProgSolution = t._3 match {
    case Implementation   => ImplementationSolution(t._1, t._2, t._4, t._5)
    case TestdataCreation => TestDataSolution(t._1, t._2, t._4, TestDataSolution.readTestDataFromJson(t._5))
    case ActivityDiagram  => ActivityDiagramSolution(t._1, t._2, t._4, t._5)
  }

  def apply(username: String, exerciseId: Int, exercisePart: ProgExPart, progLanguage: ProgLanguage, solutionStr: String): ProgSolution = exercisePart match {
    case Implementation   => ImplementationSolution(username, exerciseId, progLanguage, solutionStr)
    case TestdataCreation => TestDataSolution(username, exerciseId, progLanguage, TestDataSolution.readTestDataFromJson(solutionStr))
    case ActivityDiagram  => ActivityDiagramSolution(username, exerciseId, progLanguage, solutionStr)
  }

  def unapply(arg: ProgSolution): Option[(String, Int, ProgExPart, ProgLanguage, String)] =
    Some(arg.username, arg.exerciseId, arg.part, arg.language, arg.solution)

}

sealed trait ProgSolution extends PartSolution {

  override type PartType = ProgExPart

  val username  : String
  val exerciseId: Int

  val part    : ProgExPart
  val language: ProgLanguage

  def solution: String

}

case class ImplementationSolution(username: String, exerciseId: Int, language: ProgLanguage, solution: String) extends ProgSolution {

  override val part: ProgExPart = Implementation

}

case class TestDataSolution(username: String, exerciseId: Int, language: ProgLanguage, completeCommitedTestData: Seq[CompleteCommitedTestData]) extends ProgSolution {

  override val part: ProgExPart = TestdataCreation

  override def solution: String = "[" + completeCommitedTestData.map(_.toJson).mkString(",") + "]"

}

case class ActivityDiagramSolution(username: String, exerciseId: Int, language: ProgLanguage, solution: String) extends ProgSolution {

  override val part: ProgExPart = ActivityDiagram

}

// Table Definitions

class ProgTableDefs @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)
  extends HasDatabaseConfigProvider[JdbcProfile] with SingleExerciseTableDefs[ProgExercise, ProgCompleteEx, ProgSolution, ProgExPart] {

  import profile.api._

  // Abstract types

  override protected type ExTableDef = ProgExercisesTable

  override protected type SolTableDef = ProgSolutionTable

  // Table Queries

  override protected val exTable = TableQuery[ProgExercisesTable]

  override protected val solTable = TableQuery[ProgSolutionTable]

  val inputTypesQuery = TableQuery[InputTypesTable]

  val sampleSolutions = TableQuery[ProgSampleSolutionsTable]

  val sampleTestData = TableQuery[SampleTestDataTable]

  val sampleTestDataInputs = TableQuery[SampleTestDataInputsTable]

  val commitedTestData = TableQuery[CommitedTestDataTable]

  val commitedTestDataInput = TableQuery[CommitedTestdataInputTable]

  // Queries

  override def completeExForEx(ex: ProgExercise)(implicit ec: ExecutionContext): Future[ProgCompleteEx] =
    for {
      samples <- db.run(sampleSolutions.filter(_.exerciseId === ex.id).result)
      inputTypes <- db.run(inputTypesQuery.filter(_.exerciseId === ex.id).result)
      testData <- db.run(completeTestDataForEx(ex))
    } yield {
      val completeSampleTestData: Seq[CompleteSampleTestData] = testData groupBy (_._1) mapValues (_ map (_._2)) map (ctd => CompleteSampleTestData(ctd._1, ctd._2.flatten)) toSeq

      ProgCompleteEx(ex, inputTypes, samples, completeSampleTestData)
    }

  override def saveExerciseRest(compEx: ProgCompleteEx)(implicit ec: ExecutionContext): Future[Boolean] = for {
    samplesSaved <- saveSeq[ProgSampleSolution](compEx.sampleSolutions, sampSol => db.run(sampleSolutions += sampSol))
    inputTypesSaved <- saveSeq[ProgInput](compEx.inputTypes, i => db.run(inputTypesQuery += i))
    sampleTestDataSaved <- saveSeq[CompleteSampleTestData](compEx.sampleTestData, saveSampleTestdata)
  } yield samplesSaved && inputTypesSaved && sampleTestDataSaved

  private def saveSampleTestdata(sampleData: CompleteSampleTestData)(implicit ec: ExecutionContext): Future[Boolean] =
    db.run(sampleTestData += sampleData.testData) flatMap { _ => saveSeq[SampleTestDataInput](sampleData.inputs, i => db.run(sampleTestDataInputs += i)) }

  private def completeTestDataForEx(ex: ProgExercise)(implicit ec: ExecutionContext) = sampleTestData.joinLeft(sampleTestDataInputs)
    .on { case (td, tdi) => td.exerciseId === tdi.exerciseId && td.id === tdi.testId }
    .filter(_._1.exerciseId === ex.id)
    .result

  // Implicit column types

  implicit val ProgLanguageColumnType: BaseColumnType[ProgLanguage] =
    MappedColumnType.base[ProgLanguage, String](_.name, str => ProgLanguage.valueOf(str) getOrElse ProgLanguage.STANDARD_LANG)

  implicit val ProgDataTypesColumnType: BaseColumnType[ProgDataType] =
    MappedColumnType.base[ProgDataType, String](_.typeName, str => ProgDataTypes.byName(str) getOrElse ProgDataTypes.STRING)

  override protected implicit val partTypeColumnType: BaseColumnType[ProgExPart] =
    MappedColumnType.base[ProgExPart, String](_.urlName, str => ProgExParts.values.find(_.urlName == str) getOrElse Implementation)

  // Tables

  class ProgExercisesTable(tag: Tag) extends HasBaseValuesTable[ProgExercise](tag, "prog_exercises") {

    def functionName = column[String]("function_name")

    def outputType = column[ProgDataType]("output_type")


    def pk = primaryKey("pk", id)


    def * = (id, title, author, text, state, functionName, outputType) <> (ProgExercise.tupled, ProgExercise.unapply)

  }

  class InputTypesTable(tag: Tag) extends Table[ProgInput](tag, "prog_input_types") {

    def id = column[Int](idName)

    def exerciseId = column[Int]("exercise_id")

    def inputName = column[String]("input_name")

    def inputType = column[ProgDataType]("input_type")


    def pk = primaryKey("pk", (id, exerciseId))

    def exerciseFk = foreignKey("exercise_fk", exerciseId, exTable)(_.id)


    def * = (id, exerciseId, inputName, inputType) <> (ProgInput.tupled, ProgInput.unapply)

  }

  class ProgSampleSolutionsTable(tag: Tag) extends Table[ProgSampleSolution](tag, "prog_samples") {

    def exerciseId = column[Int]("exercise_id")

    def language = column[ProgLanguage]("language")

    def base = column[String]("base")

    def solution = column[String]("solution")

    def testMain = column[String]("test_main")


    def pk = primaryKey("pk", (exerciseId, language))

    def exerciseFk = foreignKey("exercise_fk", exerciseId, exTable)(_.id)


    def * = (exerciseId, language, base, solution, testMain) <> (ProgSampleSolution.tupled, ProgSampleSolution.unapply)

  }

  // Test data

  abstract class ITestDataTable[T <: TestData](tag: Tag, name: String) extends Table[T](tag, name) {

    def id = column[Int]("id")

    def exerciseId = column[Int]("exercise_id")

    def output = column[String]("output")


    def exerciseFk = foreignKey("exercise_fk", exerciseId, exTable)(_.id)

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

  class ProgSolutionTable(tag: Tag) extends PartSolutionsTable[ProgSolution](tag, "prog_solutions") {

    def language = column[ProgLanguage]("language")

    def solution = column[String]("solution")


    def * = (username, exerciseId, part, language, solution) <> (ProgSolution.tupled, ProgSolution.unapply)

  }

}