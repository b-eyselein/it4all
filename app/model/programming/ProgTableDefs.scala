package model.programming

import javax.inject.Inject
import model.persistence.SingleExerciseTableDefs
import model.programming.ProgConsts._
import model.uml.UmlClassDiagram
import model.{ExerciseState, User}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.libs.json.{JsValue, Json}
import slick.jdbc.JdbcProfile
import slick.lifted.{ForeignKeyQuery, PrimaryKey, ProvenShape}

import scala.concurrent.{ExecutionContext, Future}
//import scala.language.{implicitConversions, postfixOps}

class ProgTableDefs @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(override implicit val executionContext: ExecutionContext)
  extends HasDatabaseConfigProvider[JdbcProfile] with SingleExerciseTableDefs[ProgExercise, ProgCompleteEx, ProgSolution, DBProgSolution, ProgExPart, ProgExerciseReview] {


  import profile.api._

  // Abstract types

  override protected type ExTableDef = ProgExercisesTable
  override protected type SolTableDef = ProgSolutionTable
  override protected type ReviewsTableDef = ProgExerciseReviewsTable

  // Table Queries

  override protected val exTable      = TableQuery[ProgExercisesTable]
  override protected val solTable     = TableQuery[ProgSolutionTable]
  override protected val reviewsTable = TableQuery[ProgExerciseReviewsTable]

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
    classDiagPartSaved <- saveSeq[UmlClassDiagPart](compEx.maybeClassDiagramPart.toList, i => db.run(umlClassDiagParts += i))
  } yield samplesSaved && inputTypesSaved && sampleTestDataSaved && classDiagPartSaved

  override def copyDBSolType(oldSol: DBProgSolution, newId: Int): DBProgSolution = oldSol.copy(id = newId)

  override def futureSampleSolutionsForExercisePart(id: Int, part: ProgExPart): Future[Seq[String]] =
    db.run(sampleSolutions.filter(_.exerciseId === id).map(_.solution).result)

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

    def folderIdentifier: Rep[String] = column[String]("folder_identifier")

    def functionName: Rep[String] = column[String]("function_name")

    def indentLevel: Rep[Int] = column[Int]("indent_level")

    def outputType: Rep[ProgDataType] = column[ProgDataType]("output_type")

    def baseDataAsJson: Rep[JsValue] = column[JsValue]("base_data_json")


    override def * : ProvenShape[ProgExercise] = (id, semanticVersion, title, author, text, state, folderIdentifier, functionName, indentLevel, outputType, baseDataAsJson.?) <> (ProgExercise.tupled, ProgExercise.unapply)

  }

  class InputTypesTable(tag: Tag) extends ExForeignKeyTable[ProgInput](tag, "prog_input_types") {

    def id: Rep[Int] = column[Int](idName)

    def inputName: Rep[String] = column[String]("input_name")

    def inputType: Rep[ProgDataType] = column[ProgDataType]("input_type")


    def pk: PrimaryKey = primaryKey("pk", (id, exerciseId, exSemVer))


    override def * : ProvenShape[ProgInput] = (id, exerciseId, exSemVer, inputName, inputType) <> (ProgInput.tupled, ProgInput.unapply)

  }

  class ProgSampleSolutionsTable(tag: Tag) extends ExForeignKeyTable[ProgSampleSolution](tag, "prog_sample_solutions") {

    def language: Rep[ProgLanguage] = column[ProgLanguage]("language")

    def base: Rep[String] = column[String]("base")

    def solution: Rep[String] = column[String]("solution")


    def pk: PrimaryKey = primaryKey("pk", (exerciseId, exSemVer, language))


    override def * : ProvenShape[ProgSampleSolution] = (exerciseId, exSemVer, language, base, solution) <> (ProgSampleSolution.tupled, ProgSampleSolution.unapply)

  }

  // Test data

  abstract class ITestDataTable[T <: TestData](tag: Tag, name: String) extends ExForeignKeyTable[T](tag, name) {

    def id: Rep[Int] = column[Int]("id")

    def inputAsJson: Rep[JsValue] = column[JsValue]("input_json")

    def output: Rep[JsValue] = column[JsValue]("output")

  }

  class SampleTestDataTable(tag: Tag) extends ITestDataTable[SampleTestData](tag, "prog_sample_testdata") {

    def pk: PrimaryKey = primaryKey("pk", (id, exerciseId, exSemVer))


    override def * : ProvenShape[SampleTestData] = (id, exerciseId, exSemVer, inputAsJson, output) <> (SampleTestData.tupled, SampleTestData.unapply)

  }

  class CommitedTestDataTable(tag: Tag) extends ITestDataTable[CommitedTestData](tag, "prog_commited_testdata") {

    def username: Rep[String] = column[String]("username")

    def state: Rep[ExerciseState] = column[ExerciseState]("approval_state")


    def pk: PrimaryKey = primaryKey("pk", (id, exerciseId, exSemVer, username))

    def userFk: ForeignKeyQuery[UsersTable, User] = foreignKey("user_fk", username, users)(_.username)


    override def * : ProvenShape[CommitedTestData] = (id, exerciseId, exSemVer, inputAsJson, output, username, state) <> (CommitedTestData.tupled, CommitedTestData.unapply)

  }

  class UmlClassDiagPartsTable(tag: Tag) extends ExForeignKeyTable[UmlClassDiagPart](tag, "prog_uml_cd_parts") {

    def className: Rep[String] = column[String]("class_name")

    def classDiagram: Rep[UmlClassDiagram] = column[UmlClassDiagram]("class_diagram")


    def pk: PrimaryKey = primaryKey("pk", (exerciseId, exSemVer))


    override def * : ProvenShape[UmlClassDiagPart] = (exerciseId, exSemVer, className, classDiagram) <> (UmlClassDiagPart.tupled, UmlClassDiagPart.unapply)

  }

  class ProgSolutionTable(tag: Tag) extends PartSolutionsTable(tag, "prog_solutions") {

    def solution: Rep[String] = column[String]("solution")

    def extendedUnitTests: Rep[Boolean] = column[Boolean]("extended_unit_tests")

    def language: Rep[ProgLanguage] = column[ProgLanguage]("language")


    override def * : ProvenShape[DBProgSolution] = (id, username, exerciseId, exSemVer, part, solution, language,
      extendedUnitTests, points, maxPoints) <> (DBProgSolution.tupled, DBProgSolution.unapply)

  }

  class ProgExerciseReviewsTable(tag: Tag) extends ExerciseReviewsTable(tag, "prog_exercise_reviews") {

    override def * : ProvenShape[ProgExerciseReview] = (username, exerciseId, exerciseSemVer, exercisePart, difficulty, maybeDuration.?) <> (ProgExerciseReview.tupled, ProgExerciseReview.unapply)

  }

}