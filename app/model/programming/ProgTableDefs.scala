package model.programming

import javax.inject.Inject
import model.Enums.ExerciseState
import model._
import model.persistence.SingleExerciseTableDefs
import model.programming.ProgConsts._
import model.programming.ProgDataTypes._
import model.uml.UmlClassDiagram
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.libs.json.{JsArray, JsObject, JsValue, Json}
import play.twirl.api.Html
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}
import scala.language.{implicitConversions, postfixOps}

// Classes for use

case class ProgCompleteEx(ex: ProgExercise, inputTypes: Seq[ProgInput], sampleSolutions: Seq[ProgSampleSolution],
                          sampleTestData: Seq[CompleteSampleTestData], maybeClassDiagramPart: Option[UmlClassDiagPart])
  extends PartsCompleteEx[ProgExercise, ProgrammingExPart] {

  override def preview: Html = // FIXME: move to toolMain!
    views.html.programming.progPreview(this)

  val inputCount: Int = inputTypes.size

  override def hasPart(partType: ProgrammingExPart): Boolean = partType match {
    case Implementation  => true
    case ActivityDiagram => true
    // TODO: Creation of test data is currently disabled
    case TestdataCreation => false
  }

  def addIndent(solution: String): String = solution split "\n" map (str => " " * (4 * ex.indentLevel) + str) mkString "\n"

}

sealed trait CompleteTestData {

  val testData: TestData

  val inputs: Seq[TestDataInput]

  def write: String = (inputs map (_.input)) mkString " "

}

case class CompleteSampleTestData(testData: SampleTestData, inputs: Seq[SampleTestDataInput]) extends CompleteTestData

case class CompleteCommitedTestData(testData: CommitedTestData, inputs: Seq[CommitedTestDataInput]) extends CompleteTestData {

  def toJson: JsObject = Json.obj(
    idName -> testData.id,
    exerciseIdName -> testData.exerciseId,
    usernameName -> testData.username,
    outputName -> testData.output,
    stateName -> testData.state.name,
    inputsName -> JsArray(inputs map (_.toJson))
  )

}

// Case classes for tables

case class ProgExercise(id: Int, title: String, author: String, text: String, state: ExerciseState, folderIdentifier: String, base: String,
                        functionname: String, indentLevel: Int, outputType: ProgDataType) extends Exercise


case class ProgInput(id: Int, exerciseId: Int, inputName: String, inputType: ProgDataType)

case class ProgSampleSolution(exerciseId: Int, language: ProgLanguage, base: String, solution: String)

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
    idName -> id, testIdName -> testId, exerciseIdName -> exerciseId, inputName -> input, usernameName -> username
  )

}

case class UmlClassDiagPart(exerciseId: Int, className: String, umlClassDiagram: UmlClassDiagram)

// Solution types

object TestDataSolution extends JsonFormat {

  private def readTestData(jsObject: JsObject): Option[CommitedTestData] = for {
    id <- jsObject.intField(idName)
    exerciseId <- jsObject.intField(exerciseIdName)
    username <- jsObject.stringField(usernameName)
    output <- jsObject.stringField(outputName)
    state <- jsObject.enumField(stateName, ExerciseState.valueOf)
  } yield CommitedTestData(id, exerciseId, username, output, state)

  private def readInput(jsValue: JsValue): Option[CommitedTestDataInput] = jsValue.asObj flatMap { jsObj =>
    for {
      id <- jsObj.intField(idName)
      testId <- jsObj.intField(testIdName)
      exerciseId <- jsObj.intField(exerciseIdName)
      input <- jsObj.stringField(inputName)
      username <- jsObj.stringField(usernameName)
    } yield CommitedTestDataInput(id, testId, exerciseId, input, username)

  }

  def readTestDataFromJson(jsonStr: String): Seq[CompleteCommitedTestData] = Json.parse(jsonStr).asArray { jsValue =>

    for {
      jsObj <- jsValue.asObj
      testData <- readTestData(jsObj)
      inputs <- jsObj.arrayField(inputsName, readInput)
    } yield CompleteCommitedTestData(testData, inputs)

  } getOrElse Seq.empty

}

object ProgSolution {

  def tupled(t: (String, Int, ProgrammingExPart, ProgLanguage, String)): ProgSolution = t._3 match {
    case Implementation   => ImplementationSolution(t._1, t._2, t._4, t._5)
    case TestdataCreation => TestDataSolution(t._1, t._2, t._4, TestDataSolution.readTestDataFromJson(t._5))
    case ActivityDiagram  => ActivityDiagramSolution(t._1, t._2, t._4, t._5)
  }

  def apply(username: String, exerciseId: Int, exercisePart: ProgrammingExPart, progLanguage: ProgLanguage, solutionStr: String): ProgSolution = exercisePart match {
    case Implementation   => ImplementationSolution(username, exerciseId, progLanguage, solutionStr)
    case TestdataCreation => TestDataSolution(username, exerciseId, progLanguage, TestDataSolution.readTestDataFromJson(solutionStr))
    case ActivityDiagram  => ActivityDiagramSolution(username, exerciseId, progLanguage, solutionStr)
  }

  def unapply(arg: ProgSolution): Option[(String, Int, ProgrammingExPart, ProgLanguage, String)] =
    Some(arg.username, arg.exerciseId, arg.part, arg.language, arg.solution)

}

sealed trait ProgSolution extends PartSolution {

  override type PartType = ProgrammingExPart

  val language: ProgLanguage

  def solution: String

}

case class ImplementationSolution(username: String, exerciseId: Int, language: ProgLanguage, solution: String) extends ProgSolution {

  override val part: ProgrammingExPart = Implementation

}

case class TestDataSolution(username: String, exerciseId: Int, language: ProgLanguage, completeCommitedTestData: Seq[CompleteCommitedTestData]) extends ProgSolution {

  override val part: ProgrammingExPart = TestdataCreation

  override def solution: String = "[" + completeCommitedTestData.map(_.toJson).mkString(",") + "]"

}

case class ActivityDiagramSolution(username: String, exerciseId: Int, language: ProgLanguage, solution: String) extends ProgSolution {

  override val part: ProgrammingExPart = ActivityDiagram

}

// Table Definitions

class ProgTableDefs @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext)
  extends HasDatabaseConfigProvider[JdbcProfile] with SingleExerciseTableDefs[ProgExercise, ProgCompleteEx, ProgSolution, ProgrammingExPart] {

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

  val umlClassDiagParts = TableQuery[UmlClassDiagPartsTable]

  // Queries

  override def completeExForEx(ex: ProgExercise)(implicit ec: ExecutionContext): Future[ProgCompleteEx] = for {
    samples <- db.run(sampleSolutions.filter(_.exerciseId === ex.id).result)
    inputTypes <- db.run(inputTypesQuery.filter(_.exerciseId === ex.id).result)
    completeSampleTestData <- completeTestDataForEx(ex)
    maybeClassDiagramPart <- db.run(umlClassDiagParts.filter(_.exerciseId === ex.id).result.headOption)
  } yield ProgCompleteEx(ex, inputTypes, samples, completeSampleTestData, maybeClassDiagramPart)

  override def saveExerciseRest(compEx: ProgCompleteEx)(implicit ec: ExecutionContext): Future[Boolean] = for {
    samplesSaved <- saveSeq[ProgSampleSolution](compEx.sampleSolutions, sampSol => db.run(sampleSolutions += sampSol))
    inputTypesSaved <- saveSeq[ProgInput](compEx.inputTypes, i => db.run(inputTypesQuery += i))
    sampleTestDataSaved <- saveSeq[CompleteSampleTestData](compEx.sampleTestData, saveSampleTestdata)
    classDiagPartSaved <- saveSeq[UmlClassDiagPart](compEx.maybeClassDiagramPart.toSeq, i => db.run(umlClassDiagParts += i))
  } yield samplesSaved && inputTypesSaved && sampleTestDataSaved && classDiagPartSaved

  private def saveSampleTestdata(sampleData: CompleteSampleTestData): Future[Boolean] =
    db.run(sampleTestData += sampleData.testData) flatMap { _ => saveSeq[SampleTestDataInput](sampleData.inputs, i => db.run(sampleTestDataInputs += i)) }

  private def completeTestDataForEx(ex: ProgExercise): Future[Seq[CompleteSampleTestData]] =
    db.run(sampleTestData.filter(_.exerciseId === ex.id).result) flatMap { sampleTDSeq: Seq[SampleTestData] =>
      Future.sequence(sampleTDSeq map sampleTestDataInputsForSampleTestData)
    }

  private def sampleTestDataInputsForSampleTestData(sampleTD: SampleTestData): Future[CompleteSampleTestData] =
    db.run(sampleTestDataInputs.filter(tdi => tdi.exerciseId === sampleTD.exerciseId && tdi.testId === sampleTD.id).result) map {
      sampleTDInputs => CompleteSampleTestData(sampleTD, sampleTDInputs)
    }

  // Implicit column types

  private implicit val progLanguageColumnType: BaseColumnType[ProgLanguage] =
    MappedColumnType.base[ProgLanguage, String](_.name, str => ProgLanguage.valueOf(str) getOrElse ProgLanguage.STANDARD_LANG)

  private implicit val progDataTypesColumnType: BaseColumnType[ProgDataType] =
    MappedColumnType.base[ProgDataType, String](_.typeName, str => ProgDataTypes.byName(str) getOrElse ProgDataTypes.STRING)

  override protected implicit val partTypeColumnType: BaseColumnType[ProgrammingExPart] =
    MappedColumnType.base[ProgrammingExPart, String](_.urlName, str => ProgrammingExParts.values.find(_.urlName == str) getOrElse Implementation)

  // Tables

  class ProgExercisesTable(tag: Tag) extends HasBaseValuesTable[ProgExercise](tag, "prog_exercises") {

    def folderIdentifier = column[String]("identifier")

    def base = column[String]("base")

    def functionname = column[String]("function_name")

    def indentLevel = column[Int]("indent_level")

    def outputType = column[ProgDataType]("output_type")


    def pk = primaryKey("pk", id)


    override def * = (id, title, author, text, state, folderIdentifier, base, functionname, indentLevel, outputType) <> (ProgExercise.tupled, ProgExercise.unapply)

  }

  class InputTypesTable(tag: Tag) extends Table[ProgInput](tag, "prog_input_types") {

    def id = column[Int](idName)

    def exerciseId = column[Int]("exercise_id")

    def inputName = column[String]("input_name")

    def inputType = column[ProgDataType]("input_type")


    def pk = primaryKey("pk", (id, exerciseId))

    def exerciseFk = foreignKey("exercise_fk", exerciseId, exTable)(_.id)


    override def * = (id, exerciseId, inputName, inputType) <> (ProgInput.tupled, ProgInput.unapply)

  }

  class ProgSampleSolutionsTable(tag: Tag) extends Table[ProgSampleSolution](tag, "prog_samples") {

    def exerciseId = column[Int]("exercise_id")

    def language = column[ProgLanguage]("language")

    def base = column[String]("base")

    def solution = column[String]("solution")


    def pk = primaryKey("pk", (exerciseId, language))

    def exerciseFk = foreignKey("exercise_fk", exerciseId, exTable)(_.id)


    override def * = (exerciseId, language, base, solution) <> (ProgSampleSolution.tupled, ProgSampleSolution.unapply)

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


    override def * = (id, exerciseId, output) <> (SampleTestData.tupled, SampleTestData.unapply)

  }

  class CommitedTestDataTable(tag: Tag) extends ITestDataTable[CommitedTestData](tag, "prog_commited_testdata") {

    def username = column[String]("username")

    def state = column[ExerciseState]("approval_state")


    def pk = primaryKey("pk", (id, exerciseId, username))

    def userFk = foreignKey("user_fk", username, users)(_.username)


    override def * = (id, exerciseId, username, output, state) <> (CommitedTestData.tupled, CommitedTestData.unapply)

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


    override def * = (id, testId, exerciseId, input) <> (SampleTestDataInput.tupled, SampleTestDataInput.unapply)

  }

  class CommitedTestdataInputTable(tag: Tag) extends TestdataInputTable[CommitedTestDataInput](tag, "prog_commited_testdata_input") {

    def username = column[String]("username")


    def pk = primaryKey("pk", (username, id, testId, exerciseId))

    def commitedTestdataFk = foreignKey("commited_testdata_fk", (testId, exerciseId, username), commitedTestData)(commitedTD => (commitedTD.id, commitedTD.exerciseId, commitedTD.username))


    override def * = (id, testId, exerciseId, input, username) <> (CommitedTestDataInput.tupled, CommitedTestDataInput.unapply)

  }

  class UmlClassDiagPartsTable(tag: Tag) extends Table[UmlClassDiagPart](tag, "prog_uml_cd_parts") {

    def exerciseId = column[Int]("exercise_id", O.PrimaryKey)

    def className = column[String]("class_name")

    def classDiagram = column[UmlClassDiagram]("class_diagram")


    override def * = (exerciseId, className, classDiagram) <> (UmlClassDiagPart.tupled, UmlClassDiagPart.unapply)

  }

  class ProgSolutionTable(tag: Tag) extends PartSolutionsTable[ProgSolution](tag, "prog_solutions") {

    def language = column[ProgLanguage]("language")

    def solution = column[String]("solution")


    override def * = (username, exerciseId, part, language, solution) <> (ProgSolution.tupled, ProgSolution.unapply)

  }

}