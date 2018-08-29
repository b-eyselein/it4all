package model.programming

import javax.inject.Inject
import model.persistence.SingleExerciseTableDefs
import model.programming.ProgConsts._
import model.programming.ProgDataTypes._
import model.uml.UmlClassDiagram
import model.{ExerciseState, SemanticVersion}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.libs.json.{JsValue, Json}
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}
import scala.language.{implicitConversions, postfixOps}

class ProgTableDefs @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(override implicit val executionContext: ExecutionContext)
  extends HasDatabaseConfigProvider[JdbcProfile] with SingleExerciseTableDefs[ProgExercise, ProgCompleteEx, ProgSolution, DBProgSolution, ProgExPart] {

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
  // TODO:  private val commitedTestData = TableQuery[CommitedTestDataTable]
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
    MappedColumnType.base[ProgLanguage, String](_.entryName, ProgLanguages.withNameInsensitive)

  private implicit val progDataTypesColumnType: BaseColumnType[ProgDataType] =
    MappedColumnType.base[ProgDataType, String](_.typeName, str => ProgDataTypes.byName(str) getOrElse ProgDataTypes.STRING)

  override protected implicit val partTypeColumnType: BaseColumnType[ProgExPart] =
    MappedColumnType.base[ProgExPart, String](_.entryName, ProgExParts.withNameInsensitive)

  // Tables

  class ProgExercisesTable(tag: Tag) extends ExerciseTableDef(tag, "prog_exercises") {

    def folderIdentifier = column[String]("folder_identifier")

    def base = column[String]("base")

    def functionname = column[String]("function_name")

    def indentLevel = column[Int]("indent_level")

    def outputType = column[ProgDataType]("output_type")

    def baseDataAsJson = column[JsValue]("base_data_json")


    override def * = (id, semanticVersion, title, author, text, state, folderIdentifier, base, functionname, indentLevel, outputType, baseDataAsJson.?).mapTo[ProgExercise]

  }

  class InputTypesTable(tag: Tag) extends Table[ProgInput](tag, "prog_input_types") {

    def id = column[Int](idName)

    def exerciseId = column[Int]("exercise_id")

    def exSemVer = column[SemanticVersion]("ex_sem_ver")

    def inputName = column[String]("input_name")

    def inputType = column[ProgDataType]("input_type")


    def pk = primaryKey("pk", (id, exerciseId, exSemVer))

    def exerciseFk = foreignKey("exercise_fk", (exerciseId, exSemVer), exTable)(ex => (ex.id, ex.semanticVersion))


    override def * = (id, exerciseId, exSemVer, inputName, inputType).mapTo[ProgInput]

  }

  class ProgSampleSolutionsTable(tag: Tag) extends Table[ProgSampleSolution](tag, "prog_sample_solutions") {

    def exerciseId = column[Int]("exercise_id")

    def exSemVer = column[SemanticVersion]("ex_sem_ver")

    def language = column[ProgLanguage]("language")

    def base = column[String]("base")

    def solution = column[String]("solution")


    def pk = primaryKey("pk", (exerciseId, exSemVer, language))

    def exerciseFk = foreignKey("exercise_fk", (exerciseId, exSemVer), exTable)(ex => (ex.id, ex.semanticVersion))


    override def * = (exerciseId, exSemVer, language, base, solution).mapTo[ProgSampleSolution]

  }

  // Test data

  abstract class ITestDataTable[T <: TestData](tag: Tag, name: String) extends Table[T](tag, name) {

    def id = column[Int]("id")

    def exerciseId = column[Int]("exercise_id")

    def exSemVer = column[SemanticVersion]("ex_sem_ver")

    def inputAsJson = column[JsValue]("input_json")

    def output = column[JsValue]("output")


    def exerciseFk = foreignKey("exercise_fk", (exerciseId, exSemVer), exTable)(ex => (ex.id, ex.semanticVersion))

  }

  class SampleTestDataTable(tag: Tag) extends ITestDataTable[SampleTestData](tag, "prog_sample_testdata") {

    def pk = primaryKey("pk", (id, exerciseId, exSemVer))


    override def * = (id, exerciseId, exSemVer, inputAsJson, output).mapTo[SampleTestData]

  }

  class CommitedTestDataTable(tag: Tag) extends ITestDataTable[CommitedTestData](tag, "prog_commited_testdata") {

    def username = column[String]("username")

    def state = column[ExerciseState]("approval_state")


    def pk = primaryKey("pk", (id, exerciseId, exSemVer, username))

    def userFk = foreignKey("user_fk", username, users)(_.username)


    override def * = (id, exerciseId, exSemVer, inputAsJson, output, username, state).mapTo[CommitedTestData]

  }

  class UmlClassDiagPartsTable(tag: Tag) extends Table[UmlClassDiagPart](tag, "prog_uml_cd_parts") {

    def exerciseId = column[Int]("exercise_id")

    def exSemVer = column[SemanticVersion]("ex_sem_ver")

    def className = column[String]("class_name")

    def classDiagram = column[UmlClassDiagram]("class_diagram")


    def pk = primaryKey("pk", (exerciseId, exSemVer))

    def exerciseFk = foreignKey("exercise_fk", (exerciseId, exSemVer), exTable)(ex => (ex.id, ex.semanticVersion))


    override def * = (exerciseId, exSemVer, className, classDiagram).mapTo[UmlClassDiagPart]

  }

  class ProgSolutionTable(tag: Tag) extends PartSolutionsTable(tag, "prog_solutions") {

    def solution = column[String]("solution")

    def language = column[ProgLanguage]("language")


    override def * = (username, exerciseId, exSemVer, part, solution, language, points, maxPoints).mapTo[DBProgSolution]

  }

}