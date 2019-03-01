package model.tools.programming.persistence

import model.persistence.ExerciseCollectionTableDefs
import model.tools.programming.ProgConsts._
import model.tools.programming._
import model.tools.uml.UmlClassDiagram
import model.{ExerciseState, User}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.libs.json.{JsValue, Json}
import slick.ast.TypedType
import slick.jdbc.JdbcProfile
import slick.lifted.{ForeignKeyQuery, PrimaryKey, ProvenShape}

import scala.concurrent.{ExecutionContext, Future}

class ProgTableDefs @javax.inject.Inject()(protected val dbConfigProvider: DatabaseConfigProvider)
                                          (override implicit val executionContext: ExecutionContext)
  extends HasDatabaseConfigProvider[JdbcProfile]
    with ExerciseCollectionTableDefs[ProgExercise, ProgExPart, ProgCollection, ProgSolution, ProgSampleSolution, ProgUserSolution, ProgExerciseReview] {

  import profile.api._

  // Abstract types

  override protected type DbExType = DbProgExercise

  override protected type ExTableDef = ProgExercisesTable


  override protected type CollTableDef = ProgCollectionsTable


  override protected type DbSampleSolType = DbProgSampleSolution

  override protected type DbSampleSolTable = ProgSampleSolutionsTable


  override protected type DbUserSolType = DbProgUserSolution

  override protected type DbUserSolTable = ProgSolutionTable


  override protected type DbReviewType = DbProgrammingExerciseReview

  override protected type ReviewsTable = ProgExerciseReviewsTable

  // Table Queries

  override protected val exTable      = TableQuery[ProgExercisesTable]
  override protected val collTable    = TableQuery[ProgCollectionsTable]
  override protected val solTable     = TableQuery[ProgSolutionTable]
  override protected val reviewsTable = TableQuery[ProgExerciseReviewsTable]

  private val inputTypesQuery   = TableQuery[InputTypesTable]
  private val sampleSolutions   = TableQuery[ProgSampleSolutionsTable]
  private val sampleTestData    = TableQuery[ProgSampleTestDataTable]
  // TODO:  private val commitedTestData = TableQuery[CommitedTestDataTable]
  private val umlClassDiagParts = TableQuery[UmlClassDiagPartsTable]

  // Helper methods

  override protected val dbModels               = ProgDbModels
  override protected val exerciseReviewDbModels = ProgExerciseReviewDbModels

  override def copyDbUserSolType(oldSol: DbProgUserSolution, newId: Int): DbProgUserSolution = oldSol.copy(id = newId)

  override protected def exDbValuesFromExercise(collId: Int, compEx: ProgExercise): DbProgExercise =
    dbModels.dbExerciseFromExercise(collId, compEx)

  // Queries

  override def completeExForEx(collId: Int, ex: DbProgExercise): Future[ProgExercise] = for {
    samples <- db.run(sampleSolutions.filter(_.exerciseId === ex.id).result) map (_ map dbModels.sampleSolFromDbSampleSol)
    inputTypes <- db.run(inputTypesQuery.filter(_.exerciseId === ex.id).result) map (_ map dbModels.progInputFromDbProgInput)
    sampleTestData <- db.run(sampleTestData.filter(_.exerciseId === ex.id).result) map (_ map dbModels.sampleTestDataFromDbSampleTestData)
    maybeClassDiagram <- db.run(umlClassDiagParts.filter(_.exerciseId === ex.id).result.headOption).map(_.map(_.classDiagram))
  } yield dbModels.exerciseFromDbValues(ex, inputTypes, samples, sampleTestData, maybeClassDiagram)

  override def saveExerciseRest(collId: Int, ex: ProgExercise): Future[Boolean] = {
    val dbSamples = ex.sampleSolutions map (s => dbModels.dbSampleSolFromSampleSol(ex.id, ex.semanticVersion, collId, s))
    val dbProgInputs = ex.inputTypes map (it => dbModels.dbProgInputFromProgInput(ex.id, ex.semanticVersion, collId, it))
    val dbSampleTestData = ex.sampleTestData map (std => dbModels.dbSampleTestDataFromSampleTestData(ex.id, ex.semanticVersion, collId, std))
    val dbProgUmlClassDiagram = ex.maybeClassDiagramPart.map(mcd => dbModels.dbProgUmlClassDiagramFromUmlClassDiagram(ex.id, ex.semanticVersion, collId, mcd)).toList

    for {
      samplesSaved <- saveSeq[DbProgSampleSolution](dbSamples, i => db.run(sampleSolutions += i))
      inputTypesSaved <- saveSeq[DbProgInput](dbProgInputs, i => db.run(inputTypesQuery += i))
      sampleTestDataSaved <- saveSeq[DbProgSampleTestData](dbSampleTestData, i => db.run(sampleTestData += i))
      classDiagPartSaved <- saveSeq[DbProgUmlClassDiagram](dbProgUmlClassDiagram, i => db.run(umlClassDiagParts += i))
    } yield samplesSaved && inputTypesSaved && sampleTestDataSaved && classDiagPartSaved
  }

  override def futureSampleSolutionsForExPart(collId: Int, id: Int, part: ProgExPart): Future[Seq[String]] =
  //  FIXME:  db.run(sampleSolutions.filter(_.exerciseId === id).map(_.sample).result.map(_.solution))
    ???

  // Implicit column types

  private implicit val jsonColumnType: BaseColumnType[JsValue] = MappedColumnType.base[JsValue, String](_.toString, Json.parse)

  private implicit val progLanguageColumnType: BaseColumnType[ProgLanguage] =
    MappedColumnType.base[ProgLanguage, String](_.entryName, ProgLanguages.withNameInsensitive)

  private implicit val progDataTypesColumnType: BaseColumnType[ProgDataType] =
    MappedColumnType.base[ProgDataType, String](_.typeName, str => ProgDataTypes.byName(str) getOrElse ProgDataTypes.STRING)

  override protected implicit val partTypeColumnType: BaseColumnType[ProgExPart] =
    MappedColumnType.base[ProgExPart, String](_.entryName, ProgExParts.withNameInsensitive)

  override protected implicit val solTypeColumnType: TypedType[ProgSolution] =
    MappedColumnType.base[ProgSolution, String](progSolution => {
      //FIXME: implement!
      progSolution.implementation
    },
      solutionStr => {
        ProgSolution(solutionStr, testData = Seq.empty, extendedUnitTests = false, ProgLanguages.StandardLanguage)
        //        val solution: ProgSolution = part match {
        //          case ProgExParts.TestdataCreation => ??? // ProgTestDataSolution(???, language)
        //          case _                            => ProgStringSolution(solutionStr, extendedUnitTests, language)
        //        }

      })

  // Tables

  class ProgCollectionsTable(tag: Tag) extends ExerciseCollectionTable(tag, "prog_collections") {

    def * : ProvenShape[ProgCollection] = (id, title, author, text, state, shortName) <> (ProgCollection.tupled, ProgCollection.unapply)

  }

  class ProgExercisesTable(tag: Tag) extends ExerciseInCollectionTable(tag, "prog_exercises") {

    def folderIdentifier: Rep[String] = column[String]("folder_identifier")

    def functionName: Rep[String] = column[String]("function_name")

    def outputType: Rep[ProgDataType] = column[ProgDataType]("output_type")

    def baseDataAsJson: Rep[JsValue] = column[JsValue]("base_data_json")


    override def * : ProvenShape[DbProgExercise] = (id, semanticVersion, collectionId, title, author, text, state,
      folderIdentifier, functionName, outputType, baseDataAsJson.?) <> (DbProgExercise.tupled, DbProgExercise.unapply)

  }

  class InputTypesTable(tag: Tag) extends ExForeignKeyTable[DbProgInput](tag, "prog_input_types") {

    def id: Rep[Int] = column[Int](idName)

    def collectionId: Rep[Int] = column[Int]("collection_id")

    def inputName: Rep[String] = column[String]("input_name")

    def inputType: Rep[ProgDataType] = column[ProgDataType]("input_type")


    def pk: PrimaryKey = primaryKey("pk", (id, exerciseId, exSemVer))


    override def * : ProvenShape[DbProgInput] = (id, exerciseId, exSemVer, collectionId, inputName, inputType) <> (DbProgInput.tupled, DbProgInput.unapply)

  }

  class ProgSampleSolutionsTable(tag: Tag) extends ASampleSolutionsTable(tag, "prog_sample_solutions") {

    def language: Rep[ProgLanguage] = column[ProgLanguage](languageName)

    def base: Rep[String] = column[String](baseName)


    def pk: PrimaryKey = primaryKey("pk", (exerciseId, exSemVer, language))


    override def * : ProvenShape[DbProgSampleSolution] = ??? //(id, exerciseId, exSemVer, collectionId, language, base, sample) <> (DbProgSampleSolution.tupled, DbProgSampleSolution.unapply)

  }

  // Test data

  abstract class ITestDataTable[T <: DbProgTestData](tag: Tag, name: String) extends ExForeignKeyTable[T](tag, name) {

    def id: Rep[Int] = column[Int]("id")

    def collectionId: Rep[Int] = column[Int]("collection_id")

    def inputAsJson: Rep[JsValue] = column[JsValue]("input_json")

    def output: Rep[JsValue] = column[JsValue]("output")

  }

  class ProgSampleTestDataTable(tag: Tag) extends ITestDataTable[DbProgSampleTestData](tag, "prog_sample_testdata") {

    def pk: PrimaryKey = primaryKey("pk", (id, exerciseId, exSemVer))


    override def * : ProvenShape[DbProgSampleTestData] = (id, exerciseId, exSemVer, collectionId, inputAsJson, output) <> (DbProgSampleTestData.tupled, DbProgSampleTestData.unapply)

  }

  class ProgUserTestDataTable(tag: Tag) extends ITestDataTable[DbProgUserTestData](tag, "prog_commited_testdata") {

    def username: Rep[String] = column[String]("username")

    def state: Rep[ExerciseState] = column[ExerciseState]("approval_state")


    def pk: PrimaryKey = primaryKey("pk", (id, exerciseId, exSemVer, username))

    def userFk: ForeignKeyQuery[UsersTable, User] = foreignKey("user_fk", username, users)(_.username)


    override def * : ProvenShape[DbProgUserTestData] = (id, exerciseId, exSemVer, collectionId, username, inputAsJson, output, state) <> (DbProgUserTestData.tupled, DbProgUserTestData.unapply)

  }

  class UmlClassDiagPartsTable(tag: Tag) extends ExForeignKeyTable[DbProgUmlClassDiagram](tag, "prog_uml_cd_parts") {

    def collectionId: Rep[Int] = column[Int]("collection_id")

    def classDiagram: Rep[UmlClassDiagram] = column[UmlClassDiagram]("class_diagram")


    def pk: PrimaryKey = primaryKey("pk", (exerciseId, exSemVer))


    override def * : ProvenShape[DbProgUmlClassDiagram] = (exerciseId, exSemVer, collectionId, classDiagram) <> (DbProgUmlClassDiagram.tupled, DbProgUmlClassDiagram.unapply)

  }

  class ProgSolutionTable(tag: Tag) extends AUserSolutionsTable(tag, "prog_solutions") {

    def implementation: Rep[String] = column[String]("implementation")

    def testData: Rep[JsValue] = column[JsValue]("test_data")

    def extendedUnitTests: Rep[Boolean] = column[Boolean]("extended_unit_tests")

    def language: Rep[ProgLanguage] = column[ProgLanguage]("language")


    //    override def * = ???

    override def * : ProvenShape[DbProgUserSolution] = (id, exerciseId, exSemVer, collectionId, username, part, implementation, testData,
      language, extendedUnitTests, points, maxPoints) <> (DbProgUserSolution.tupled, DbProgUserSolution.unapply)

  }

  class ProgExerciseReviewsTable(tag: Tag) extends ExerciseReviewsTable(tag, "prog_exercise_reviews") {

    override def * : ProvenShape[DbProgrammingExerciseReview] = (username, collectionId, exerciseId, exercisePart, difficulty,
      maybeDuration.?) <> (DbProgrammingExerciseReview.tupled, DbProgrammingExerciseReview.unapply)

  }

}
