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

  private val regexTestDataTable: TableQuery[RegexTestDataTable] = TableQuery[RegexTestDataTable]

  // Helper methods

  override protected val dbModels               = RegexDbModels
  override protected val exerciseReviewDbModels = RegexExerciseReviewDbModels

  override protected def exDbValuesFromExercise(collId: Int, compEx: RegexExercise): DbExType = dbModels.dbExerciseFromExercise(collId, compEx)

  // Queries

  override protected def completeExForEx(collId: Int, ex: DbRegexExercise): Future[RegexExercise] = for {
    sampleSolutions <- db.run(sampleSolutionsTableQuery.filter {
      e => e.id === ex.id && e.exSemVer === ex.semanticVersion && e.collectionId === collId
    }.result.map(_ map StringSolutionDbModels.sampleSolFromDbSampleSol))
    testData <- db.run(regexTestDataTable.filter {
      td => td.exerciseId === ex.id && td.exSemVer === ex.semanticVersion && td.collectionId === collId
    }.result.map(_ map dbModels.testDataFromDbTestData))
  } yield dbModels.exerciseFromDbExercise(ex, sampleSolutions, testData)

  override protected def saveExerciseRest(collId: Int, ex: RegexExercise): Future[Boolean] = {
    val dbSamples = ex.sampleSolutions map (s => StringSolutionDbModels.dbSampleSolFromSampleSol(ex.id, ex.semanticVersion, collId, s))
    val dbTestdata = ex.testData map (td => dbModels.dbTestDataFromTestData(ex.id, ex.semanticVersion, collId, td))

    for {
      samplesSaved <- saveSeq[DbStringSampleSolution](dbSamples, s => db.run(sampleSolutionsTableQuery += s), Some("RegexSampleSolution"))
      testDataSaved <- saveSeq[DbRegexTestData](dbTestdata, td => db.run(regexTestDataTable += td), Some("RegexTestData"))
    } yield samplesSaved && testDataSaved
  }

  // Column types

  override protected implicit val partTypeColumnType: BaseColumnType[RegexExPart] =
    MappedColumnType.base[RegexExPart, String](_.entryName, RegexExParts.withNameInsensitive)

  // Tables

  class RegexCollectionsTable(tag: Tag) extends ExerciseCollectionTable(tag, "regex_collections") {

    override def * : ProvenShape[RegexCollection] = (id, title, author, text, state, shortName) <> (RegexCollection.tupled, RegexCollection.unapply)

  }

  class RegexExerciseTable(tag: Tag) extends ExerciseInCollectionTable(tag, "regex_exercises") {

    def maxPoints: Rep[Int] = column[Int]("max_points")

    override def * : ProvenShape[DbRegexExercise] = (id, semanticVersion, collectionId, title, author, text, state, maxPoints) <> (DbRegexExercise.tupled, DbRegexExercise.unapply)

  }


  class RegexTestDataTable(tag: Tag) extends ExForeignKeyTable[DbRegexTestData](tag, "regex_test_data") {

    def id: Rep[Int] = column[Int](idName)

    def data: Rep[String] = column[String](dataName)

    def isIncluded: Rep[Boolean] = column[Boolean]("is_included")


    def pk: PrimaryKey = primaryKey("pk", (id, exerciseId, exSemVer, collectionId))


    def * : ProvenShape[DbRegexTestData] = (id, exerciseId, exSemVer, collectionId, data, isIncluded) <> (DbRegexTestData.tupled, DbRegexTestData.unapply)

  }


  class RegexSampleSolutionsTable(tag: Tag) extends AStringSampleSolutionsTable(tag, "regex_sample_solutions")

  class RegexUserSolutionsTable(tag: Tag) extends AStringUserSolutionsTable(tag, "regex_solutions")


  class RegexExerciseReviewsTable(tag: Tag) extends ExerciseReviewsTable(tag, "regex_exercise_reviews") {

    override def * : ProvenShape[DbRegexExerciseReview] = (username, collectionId, exerciseId, exercisePart, difficulty, maybeDuration.?) <> (DbRegexExerciseReview.tupled, DbRegexExerciseReview.unapply)

  }

}
