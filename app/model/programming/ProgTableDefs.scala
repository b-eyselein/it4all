package model.programming

import javax.inject.Inject
import model.Enums.ExerciseState
import model._
import model.persistence.SingleExerciseTableDefs
import model.programming.ProgConsts._
import model.programming.ProgDataTypes._
import model.uml.UmlClassDiagram
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.libs.json.{JsObject, JsValue, Json}
import play.twirl.api.Html
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}
import scala.language.{implicitConversions, postfixOps}

// Classes for use

case class ProgCompleteEx(ex: ProgExercise, inputTypes: Seq[ProgInput], sampleSolutions: Seq[ProgSampleSolution],
                          sampleTestData: Seq[SampleTestData], maybeClassDiagramPart: Option[UmlClassDiagPart])
  extends PartsCompleteEx[ProgExercise, ProgrammingExPart] {

  override def preview: Html = // FIXME: move to toolMain!
    views.html.idExercises.programming.progPreview(this)

  val inputCount: Int = inputTypes.size

  override def hasPart(partType: ProgrammingExPart): Boolean = partType match {
    case Implementation  => true
    case ActivityDiagram => true
    // TODO: Creation of test data is currently disabled
    case TestdataCreation => false
  }

  def addIndent(solution: String): String = solution split "\n" map (str => " " * (4 * ex.indentLevel) + str) mkString "\n"

}

// Case classes for tables

case class ProgExercise(id: Int, title: String, author: String, text: String, state: ExerciseState, folderIdentifier: String, base: String,
                        functionname: String, indentLevel: Int, outputType: ProgDataType, baseData: Option[JsValue]) extends Exercise


case class ProgInput(id: Int, exerciseId: Int, inputName: String, inputType: ProgDataType)

case class ProgSampleSolution(exerciseId: Int, language: ProgLanguage, base: String, solution: String)

sealed trait TestData {

  val id         : Int
  val exerciseId : Int
  val inputAsJson: JsValue
  val output     : String

}

trait TestDataInput {

  val id        : Int
  val testId    : Int
  val exerciseId: Int
  val input     : String

}

case class SampleTestData(id: Int, exerciseId: Int, inputAsJson: JsValue, output: String) extends TestData

case class CommitedTestData(id: Int, exerciseId: Int, inputAsJson: JsValue, output: String, username: String, state: ExerciseState) extends TestData {

  def toJson: JsObject = Json.obj(
    idName -> id,
    exerciseIdName -> exerciseId,
    usernameName -> username,
    outputName -> output,
    stateName -> state.name,
    inputsName -> inputAsJson
  )

}

// Solution types

object TestDataSolution extends JsonFormat {

  def readTestDataFromJson(jsonStr: String): Seq[CommitedTestData] = Json.parse(jsonStr).asArray { jsValue =>

    for {
      jsObject <- jsValue.asObj
      id <- jsObject.intField(idName)
      exerciseId <- jsObject.intField(exerciseIdName)
      output <- jsObject.stringField(outputName)
      inputAsJson <- jsObject.value get "TODO!"
      username <- jsObject.stringField(usernameName)
      state <- jsObject.enumField(stateName, ExerciseState.valueOf)
    } yield CommitedTestData(id, exerciseId, inputAsJson, output, username, state)

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

case class TestDataSolution(username: String, exerciseId: Int, language: ProgLanguage, commitedTestData: Seq[CommitedTestData]) extends ProgSolution {

  override val part: ProgrammingExPart = TestdataCreation

  override def solution: String = "[" + commitedTestData.map(_.toJson).mkString(",") + "]"

}

case class ActivityDiagramSolution(username: String, exerciseId: Int, language: ProgLanguage, solution: String) extends ProgSolution {

  override val part: ProgrammingExPart = ActivityDiagram

}

// Table Definitions

class ProgTableDefs @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(override implicit val executionContext: ExecutionContext)
  extends HasDatabaseConfigProvider[JdbcProfile] with SingleExerciseTableDefs[ProgExercise, ProgCompleteEx, ProgSolution, ProgrammingExPart] {

  import profile.api._

  // Abstract types

  override protected type ExTableDef = ProgExercisesTable

  override protected type SolTableDef = ProgSolutionTable

  // Table Queries

  override protected val exTable  = TableQuery[ProgExercisesTable]
  override protected val solTable = TableQuery[ProgSolutionTable]

  private val inputTypesQuery   = TableQuery[InputTypesTable]
  private val sampleSolutions   = TableQuery[ProgSampleSolutionsTable]
  private val sampleTestData    = TableQuery[SampleTestDataTable]
  private val commitedTestData  = TableQuery[CommitedTestDataTable]
  private val umlClassDiagParts = TableQuery[UmlClassDiagPartsTable]

  // Queries

  override def completeExForEx(ex: ProgExercise): Future[ProgCompleteEx] = for {
    samples <- db.run(sampleSolutions.filter(_.exerciseId === ex.id).result)
    inputTypes <- db.run(inputTypesQuery.filter(_.exerciseId === ex.id).result)
    sampleTestData <- db.run(sampleTestData.filter(_.exerciseId === ex.id).result)
    maybeClassDiagramPart <- db.run(umlClassDiagParts.filter(_.exerciseId === ex.id).result.headOption)
  } yield ProgCompleteEx(ex, inputTypes, samples, sampleTestData, maybeClassDiagramPart)

  override def saveExerciseRest(compEx: ProgCompleteEx): Future[Boolean] = for {
    samplesSaved <- saveSeq[ProgSampleSolution](compEx.sampleSolutions, i => db.run(sampleSolutions += i))
    inputTypesSaved <- saveSeq[ProgInput](compEx.inputTypes, i => db.run(inputTypesQuery += i))
    sampleTestDataSaved <- saveSeq[SampleTestData](compEx.sampleTestData, i => db.run(sampleTestData += i))
    classDiagPartSaved <- saveSeq[UmlClassDiagPart](compEx.maybeClassDiagramPart.toSeq, i => db.run(umlClassDiagParts += i))
  } yield samplesSaved && inputTypesSaved && sampleTestDataSaved && classDiagPartSaved


  // Implicit column types

  private implicit val jsonColumnType: BaseColumnType[JsValue] = MappedColumnType.base[JsValue, String](_.toString, Json.parse)

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

    def baseDataAsJson = column[JsValue]("base_data_json")


    def pk = primaryKey("pk", id)


    override def * = (id, title, author, text, state, folderIdentifier, base, functionname, indentLevel, outputType, baseDataAsJson.?) <> (ProgExercise.tupled, ProgExercise.unapply)

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

    def inputAsJson = column[JsValue]("input_json")

    def output = column[String]("output")


    def exerciseFk = foreignKey("exercise_fk", exerciseId, exTable)(_.id)

  }

  class SampleTestDataTable(tag: Tag) extends ITestDataTable[SampleTestData](tag, "prog_sample_testdata") {

    def pk = primaryKey("pk", (id, exerciseId))


    override def * = (id, exerciseId, inputAsJson, output) <> (SampleTestData.tupled, SampleTestData.unapply)

  }

  class CommitedTestDataTable(tag: Tag) extends ITestDataTable[CommitedTestData](tag, "prog_commited_testdata") {

    def username = column[String]("username")

    def state = column[ExerciseState]("approval_state")


    def pk = primaryKey("pk", (id, exerciseId, username))

    def userFk = foreignKey("user_fk", username, users)(_.username)


    override def * = (id, exerciseId, inputAsJson, output, username, state) <> (CommitedTestData.tupled, CommitedTestData.unapply)

  }

  // Inputs for test data

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