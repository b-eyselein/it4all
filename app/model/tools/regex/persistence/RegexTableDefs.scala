package model.tools.regex.persistence

import javax.inject.Inject
import model.persistence._
import model.tools.regex.RegexConsts._
import model.tools.regex._
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile
import slick.lifted.{PrimaryKey, ProvenShape}

import scala.concurrent.{ExecutionContext, Future}

class RegexTableDefs @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(override implicit val executionContext: ExecutionContext)
  extends HasDatabaseConfigProvider[JdbcProfile]
    with StringSolutionExerciseTableDefs[RegexExPart, RegexExercise, RegexCollection, RegexExerciseReview] {

  import profile.api._

  // Abstract types

  override protected type DbExType = DbRegexExercise

  override protected type ExTableDef = RegexExerciseTable

  override protected type CollTableDef = RegexCollectionsTable


  override protected type DbSampleSolTable = RegexSampleSolutionsTable

  override protected type DbUserSolTable = RegexUserSolutionsTable


  override protected type DbReviewType = DbRegexExerciseReview

  override protected type ReviewsTable = RegexExerciseReviewsTable

  // Table Queries

  override protected val exTable  : TableQuery[RegexExerciseTable]    = TableQuery[RegexExerciseTable]
  override protected val collTable: TableQuery[RegexCollectionsTable] = TableQuery[RegexCollectionsTable]

  override protected val sampleSolutionsTableQuery: TableQuery[RegexSampleSolutionsTable] = TableQuery[RegexSampleSolutionsTable]
  override protected val userSolutionsTableQuery  : TableQuery[RegexUserSolutionsTable]   = TableQuery[RegexUserSolutionsTable]

  override protected val reviewsTable: TableQuery[RegexExerciseReviewsTable] = TableQuery[RegexExerciseReviewsTable]

  private val regexMatchTestDataTableQuery     : TableQuery[RegexMatchTestDataTable]      = TableQuery[RegexMatchTestDataTable]
  private val regexExtractionTestDataTableQuery: TableQuery[RegexExtractionTestDataTable] = TableQuery[RegexExtractionTestDataTable]

  // Helper methods

  override protected val dbModels               = RegexDbModels
  override protected val exerciseReviewDbModels = RegexExerciseReviewDbModels

  override protected def exDbValuesFromExercise(collId: Int, compEx: RegexExercise): DbExType = dbModels.dbExerciseFromExercise(collId, compEx)

  // Queries

  override protected def completeExForEx(collId: Int, ex: DbRegexExercise): Future[RegexExercise] = for {
    sampleSolutions <- futureSamplesForExercise(collId, ex.id)

    matchTestData <- db.run(
      regexMatchTestDataTableQuery
        .filter { td => td.exerciseId === ex.id && td.exSemVer === ex.semanticVersion && td.collectionId === collId }
        .result
    ).map(_.map(dbModels.matchTestDataFromDbMatchTestData))

    extractionTestData <- db.run(
      regexExtractionTestDataTableQuery
        .filter { etd => etd.exerciseId === ex.id && etd.exSemVer === ex.semanticVersion && etd.collectionId === collId }
        .result
    )

  } yield dbModels.exerciseFromDbExercise(ex, sampleSolutions, matchTestData, extractionTestData)

  override protected def saveExerciseRest(collId: Int, ex: RegexExercise): Future[Boolean] = {
    val dbSamples = ex.sampleSolutions.map(s => StringSolutionDbModels.dbSampleSolFromSampleSol(ex.id, ex.semanticVersion, collId, s))
    val dbMatchTestData = ex.matchTestData.map(mtd => dbModels.dbMatchTestDataFromMatchTestData(ex.id, ex.semanticVersion, collId, mtd))
    val dbExtractionTestData = ex.extractionTestData.map(etd => DbRegexExtractionTestData(etd.id, ex.id, ex.semanticVersion, collId, etd.base))

    for {
      samplesSaved <- saveSeq[DbStringSampleSolution](dbSamples, s => db.run(sampleSolutionsTableQuery += s), Some("RegexSampleSolution"))
      matchTestDataSaved <- saveSeq[DbRegexMatchTestData](dbMatchTestData, mtd => db.run(regexMatchTestDataTableQuery += mtd), Some("RegexMatchTestData"))
      extractionTestDataSaved <- saveSeq[DbRegexExtractionTestData](dbExtractionTestData, etd => db.run(regexExtractionTestDataTableQuery += etd), Some("RegexExtractionTestData"))
    } yield samplesSaved && matchTestDataSaved && extractionTestDataSaved
  }

  // Column types

  override protected implicit val partTypeColumnType: BaseColumnType[RegexExPart] =
    MappedColumnType.base[RegexExPart, String](_.entryName, RegexExParts.withNameInsensitive)

  private implicit val regexCorrectionTypeColumnType: BaseColumnType[RegexCorrectionType] =
    MappedColumnType.base[RegexCorrectionType, String](_.entryName, RegexCorrectionTypes.withNameInsensitive)

  // Tables

  class RegexCollectionsTable(tag: Tag) extends ExerciseCollectionTable(tag, "regex_collections") {

    override def * : ProvenShape[RegexCollection] = (id, title, author, text, state, shortName) <> (RegexCollection.tupled, RegexCollection.unapply)

  }

  class RegexExerciseTable(tag: Tag) extends ExerciseInCollectionTable(tag, "regex_exercises") {

    def maxPoints: Rep[Int] = column[Int]("max_points")

    def correctionType: Rep[RegexCorrectionType] = column[RegexCorrectionType]("correction_type")


    override def * : ProvenShape[DbRegexExercise] = (id, semanticVersion, collectionId, title, author, text, state, maxPoints, correctionType) <> (DbRegexExercise.tupled, DbRegexExercise.unapply)

  }


  class RegexMatchTestDataTable(tag: Tag) extends ExForeignKeyTable[DbRegexMatchTestData](tag, "regex_match_test_data") {

    def id: Rep[Int] = column[Int](idName)

    def data: Rep[String] = column[String](dataName)

    def isIncluded: Rep[Boolean] = column[Boolean]("is_included")


    def pk: PrimaryKey = primaryKey("pk", (id, exerciseId, exSemVer, collectionId))


    def * : ProvenShape[DbRegexMatchTestData] = (id, exerciseId, exSemVer, collectionId, data, isIncluded) <> (DbRegexMatchTestData.tupled, DbRegexMatchTestData.unapply)

  }


  class RegexExtractionTestDataTable(tag: Tag) extends ExForeignKeyTable[DbRegexExtractionTestData](tag, "regex_extraction_test_data") {

    def id: Rep[Int] = column[Int](idName)

    def base: Rep[String] = column[String]("base")


    def pk: PrimaryKey = primaryKey("pk", (id, exerciseId, exSemVer, collectionId))


    override def * : ProvenShape[DbRegexExtractionTestData] = (id, exerciseId, exSemVer, collectionId, base) <> (DbRegexExtractionTestData.tupled, DbRegexExtractionTestData.unapply)

  }


  class RegexSampleSolutionsTable(tag: Tag) extends AStringSampleSolutionsTable(tag, "regex_sample_solutions")

  class RegexUserSolutionsTable(tag: Tag) extends AStringUserSolutionsTable(tag, "regex_user_solutions")


  class RegexExerciseReviewsTable(tag: Tag) extends ExerciseReviewsTable(tag, "regex_exercise_reviews") {

    override def * : ProvenShape[DbRegexExerciseReview] = (username, collectionId, exerciseId, exercisePart, difficulty, maybeDuration.?) <> (DbRegexExerciseReview.tupled, DbRegexExerciseReview.unapply)

  }

}
