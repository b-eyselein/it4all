package model.programming

import javax.inject.Inject
import model.ExerciseState
import model.persistence.SingleExerciseTableDefs
import model.programming.ProgConsts._
import model.programming.ProgDataTypes._
import model.uml.UmlClassDiagram
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

  override protected implicit val solutionTypeColumnType: BaseColumnType[ProgSolution] =
    MappedColumnType.base[ProgSolution, String](_.toString, _ => null)

  // Tables

  class ProgExercisesTable(tag: Tag) extends HasBaseValuesTable[ProgExercise](tag, "prog_exercises") {

    def folderIdentifier = column[String]("folder_identifier")

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

  class ProgSampleSolutionsTable(tag: Tag) extends Table[ProgSampleSolution](tag, "prog_sample_solutions") {

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

  class UmlClassDiagPartsTable(tag: Tag) extends Table[UmlClassDiagPart](tag, "prog_uml_cd_parts") {

    def exerciseId = column[Int]("exercise_id", O.PrimaryKey)

    def className = column[String]("class_name")

    def classDiagram = column[UmlClassDiagram]("class_diagram")


    override def * = (exerciseId, className, classDiagram).mapTo[UmlClassDiagPart]

  }

  class ProgSolutionTable(tag: Tag) extends PartSolutionsTable(tag, "prog_solutions") {

    override def * = (username, exerciseId, part, solution, points, maxPoints) <> (DBProgSolution.tupled, DBProgSolution.unapply)

  }

}