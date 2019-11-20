package model.tools.collectionTools.programming.persistence

import javax.inject.Inject
import model.persistence.{DbExerciseFile, ExerciseTableDefs}
import model.tools.collectionTools.ExParts
import model.tools.collectionTools.programming.ProgConsts._
import model.tools.collectionTools.programming._
import model.tools.collectionTools.uml.UmlClassDiagram
import model.{ExerciseState, User}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.libs.json.{JsValue, Json}
import slick.jdbc.JdbcProfile
import slick.lifted.{ForeignKeyQuery, PrimaryKey, ProvenShape}

import scala.concurrent.ExecutionContext

class ProgTableDefs @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(override implicit val executionContext: ExecutionContext)
  extends HasDatabaseConfigProvider[JdbcProfile]
    with ExerciseTableDefs[ProgExPart, ProgExercise, ProgSolution, ProgSampleSolution, ProgUserSolution, ProgExerciseReview]
    with ProgTableQueries {

  import profile.api._

  // Abstract types

  override protected type DbExType = ProgExercise

  override protected type ExTableDef = ProgExercisesTable


  override protected type CollTableDef = ProgCollectionsTable


  override protected type DbUserSolType = DbProgUserSolution

  override protected type DbUserSolTable = ProgUserSolutionTable


  override protected type DbReviewType = DbProgrammingExerciseReview

  override protected type ReviewsTable = ProgExerciseReviewsTable

  // Table Queries

  override protected val exTable  : TableQuery[ProgExercisesTable]   = TableQuery[ProgExercisesTable]
  override protected val collTable: TableQuery[ProgCollectionsTable] = TableQuery[ProgCollectionsTable]

  override protected val userSolutionsTableQuery    : TableQuery[ProgUserSolutionTable]      = TableQuery[ProgUserSolutionTable]
  protected          val userSolutionFilesTableQuery: TableQuery[ProgUserSolutionFilesTable] = TableQuery[ProgUserSolutionFilesTable]

  override protected val reviewsTable: TableQuery[ProgExerciseReviewsTable] = TableQuery[ProgExerciseReviewsTable]

  // TODO:  private val commitedTestData = TableQuery[CommitedTestDataTable]

  protected val implementationFilesTQ: TableQuery[ImplementationFilesTable] = TableQuery[ImplementationFilesTable]

  // Helper methods

  override protected val exParts: ExParts[ProgExPart] = ProgExParts

  override protected val dbModels               = ProgDbModels
  override protected val exerciseReviewDbModels = ProgExerciseReviewDbModels

  override def copyDbUserSolType(oldSol: DbProgUserSolution, newId: Int): DbProgUserSolution = oldSol.copy(id = newId)

  // Implicit column types

  private val jsonValueColumnType: BaseColumnType[JsValue] = MappedColumnType.base[JsValue, String](_.toString, Json.parse)

  private val progDataTypesColumnType: BaseColumnType[ProgDataType] =
    jsonColumnType(ProgrammingToolJsonProtocol.progDataTypeFormat)

  override protected implicit val partTypeColumnType: BaseColumnType[ProgExPart] = jsonColumnType(exParts.jsonFormat)

  // Tables

  protected class ProgCollectionsTable(tag: Tag) extends ExerciseCollectionsTable(tag, "prog_collections")

  protected class ProgExercisesTable(tag: Tag) extends ExerciseInCollectionTable(tag, "prog_exercises") {

    private implicit val pict : BaseColumnType[Seq[ProgInput]] =
      jsonSeqColumnType(ProgrammingToolJsonProtocol.progInputFormat)
    private implicit val pdtct: BaseColumnType[ProgDataType]   = progDataTypesColumnType
    private implicit val jvct : BaseColumnType[JsValue]        = jsonValueColumnType

    private implicit val utpct: BaseColumnType[UnitTestPart]       =
      jsonColumnType(ProgrammingToolJsonProtocol.unitTestPartFormat)
    private implicit val ipct : BaseColumnType[ImplementationPart] =
      jsonColumnType(ProgrammingToolJsonProtocol.implementationPartFormat)


    private implicit val tct: BaseColumnType[Seq[ProgrammingExerciseTag]] =
      jsonSeqColumnType(ProgrammingExerciseTag.jsonFormat)

    private implicit val pssct : BaseColumnType[Seq[ProgSampleSolution]] =
      jsonSeqColumnType(ProgrammingToolJsonProtocol.sampleSolutionFormat)
    private implicit val pstdct: BaseColumnType[Seq[ProgSampleTestData]] =
      jsonSeqColumnType(ProgrammingToolJsonProtocol.progSampleTestDataFormat)

    private implicit val ucdct: BaseColumnType[UmlClassDiagram] = umlClassDiagramColumnType


    def functionName: Rep[String] = column[String]("function_name")

    def foldername: Rep[String] = column[String]("foldername")

    def filename: Rep[String] = column[String](filenameName)


    def inputTypes: Rep[Seq[ProgInput]] = column[Seq[ProgInput]]("inputs_json")

    def outputType: Rep[ProgDataType] = column[ProgDataType]("output_type_json")

    def baseDataAsJson: Rep[JsValue] = column[JsValue]("base_data_json")


    def unitTestPart: Rep[UnitTestPart] = column[UnitTestPart]("unit_test_part_json")

    def implementationPart: Rep[ImplementationPart] = column[ImplementationPart]("implementation_part_json")


    def tags: Rep[Seq[ProgrammingExerciseTag]] = column[Seq[ProgrammingExerciseTag]]("tags_json")


    def sampleSolutions: Rep[Seq[ProgSampleSolution]] = column[Seq[ProgSampleSolution]]("sample_solutions_json")

    def sampleTestData: Rep[Seq[ProgSampleTestData]] = column[Seq[ProgSampleTestData]]("sample_test_data_json")

    def maybeClassDiagram: Rep[Option[UmlClassDiagram]] = column[Option[UmlClassDiagram]]("class_diagram_json")


    override def * : ProvenShape[ProgExercise] = (
      id, collectionId, toolId, semanticVersion,
      title, author, text, state,
      functionName, foldername, filename,
      inputTypes, outputType,
      baseDataAsJson.?,
      unitTestPart, implementationPart,
      sampleSolutions, sampleTestData,
      tags,
      maybeClassDiagram
    ) <> (ProgExercise.tupled, ProgExercise.unapply)

  }

  protected class ImplementationFilesTable(tag: Tag) extends ExForeignKeyTable[DbExerciseFile](tag, "prog_impl_files") with ExerciseFilesTable[DbExerciseFile] {

    override def * : ProvenShape[DbExerciseFile] = (name, exerciseId, collectionId, content, fileType, editable) <> (DbExerciseFile.tupled, DbExerciseFile.unapply)

  }

  // Test data

  protected class ProgUserTestDataTable(tag: Tag) extends ExForeignKeyTable[DbProgUserTestData](tag, "prog_commited_testdata") {

    protected implicit val jvct: BaseColumnType[JsValue] = jsonValueColumnType


    def id: Rep[Int] = column[Int]("id")

    def inputAsJson: Rep[JsValue] = column[JsValue]("input_json")

    def output: Rep[JsValue] = column[JsValue]("output")

    def username: Rep[String] = column[String]("username")

    def state: Rep[ExerciseState] = column[ExerciseState]("approval_state")


    def pk: PrimaryKey = primaryKey("pk", (id, exerciseId, toolId, collectionId, exSemVer, username))

    def userFk: ForeignKeyQuery[UsersTable, User] = foreignKey("user_fk", username, users)(_.username)


    override def * : ProvenShape[DbProgUserTestData] = (id, exerciseId, collectionId, username, inputAsJson, output, state) <> (DbProgUserTestData.tupled, DbProgUserTestData.unapply)

  }

  // Solutions

  protected class ProgUserSolutionTable(tag: Tag) extends AUserSolutionsTable(tag, "prog_user_solutions") {

    private implicit val jvct: BaseColumnType[JsValue] = jsonValueColumnType


    def testData: Rep[JsValue] = column[JsValue]("test_data")


    def pk: PrimaryKey = primaryKey("prog_user_solutions_pk", (id, exerciseId, collectionId, username, part))


    override def * : ProvenShape[DbProgUserSolution] = (id, exerciseId, collectionId, username, part,
      testData, points, maxPoints) <> (DbProgUserSolution.tupled, DbProgUserSolution.unapply)

  }

  protected class ProgUserSolutionFilesTable(tag: Tag) extends Table[DbProgUserSolutionFile](tag, "prog_user_solution_files") with ExerciseFilesTable[DbProgUserSolutionFile] {

    def solutionId: Rep[Int] = column[Int]("sol_id")

    def exId: Rep[Int] = column[Int]("exercise_id")

    def collId: Rep[Int] = column[Int]("collection_id")

    def part: Rep[ProgExPart] = column[ProgExPart](partName)

    def username: Rep[String] = column[String](usernameName)


    override def * : ProvenShape[DbProgUserSolutionFile] = (name, solutionId, exId, collId, username, part, content,
      fileType, editable) <> (DbProgUserSolutionFile.tupled, DbProgUserSolutionFile.unapply)

  }

  // Exercise reviews

  protected class ProgExerciseReviewsTable(tag: Tag) extends ExerciseReviewsTable(tag, "prog_exercise_reviews") {

    //    override protected implicit val ptct: BaseColumnType[ProgExPart] = super.ptct


    override def * : ProvenShape[DbProgrammingExerciseReview] = (username, collectionId, exerciseId, exercisePart, difficulty,
      maybeDuration.?) <> (DbProgrammingExerciseReview.tupled, DbProgrammingExerciseReview.unapply)

  }

}
