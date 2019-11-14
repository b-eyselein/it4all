package model.tools.regex.persistence

import javax.inject.Inject
import model.persistence._
import model.tools.regex._
import model.{ExParts, StringSampleSolution}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile
import slick.lifted.ProvenShape

import scala.concurrent.{ExecutionContext, Future}

class RegexTableDefs @Inject()(override protected val dbConfigProvider: DatabaseConfigProvider)(override implicit val executionContext: ExecutionContext)
  extends HasDatabaseConfigProvider[JdbcProfile]
    with StringSolutionExerciseTableDefs[RegexExPart, RegexExercise, RegexExerciseReview] {

  import profile.api._

  // Abstract types

  override protected type DbExType = RegexExercise

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

  // Helper methods

  override protected val exParts: ExParts[RegexExPart] = RegexExParts

  override protected val dbModels               = RegexDbModels
  override protected val exerciseReviewDbModels = RegexExerciseReviewDbModels

  // Queries

  override protected def completeExForEx(collId: Int, ex: RegexExercise): Future[RegexExercise] =
    Future.successful(ex)

  override protected def saveExerciseRest(collId: Int, ex: RegexExercise): Future[Boolean] = Future.successful(true)

  // Column types

  private val regexCorrectionTypeColumnType: BaseColumnType[RegexCorrectionType] =
    MappedColumnType.base[RegexCorrectionType, String](_.entryName, RegexCorrectionTypes.withNameInsensitive)

  private val matchTestDataSeqColumnType: BaseColumnType[Seq[RegexMatchTestData]] =
    jsonSeqColumnType(RegexCompleteResultJsonProtocol.regexMatchTestDataFormat)

  private val extractionTestDataSeqColumnType: BaseColumnType[Seq[RegexExtractionTestData]] =
    jsonSeqColumnType(RegexCompleteResultJsonProtocol.regexExtractionTestDataFormat)

  override protected implicit val partTypeColumnType: BaseColumnType[RegexExPart] = jsonColumnType(exParts.jsonFormat)

  // Tables

  protected class RegexCollectionsTable(tag: Tag) extends ExerciseCollectionsTable(tag, "regex_collections")

  protected class RegexExerciseTable(tag: Tag) extends ExerciseInCollectionTable(tag, "regex_exercises") {

    private implicit val rctct: BaseColumnType[RegexCorrectionType] = regexCorrectionTypeColumnType

    private implicit val ssct: BaseColumnType[Seq[StringSampleSolution]] = stringSampleSolutionColumnType

    private implicit val mtdsct: BaseColumnType[Seq[RegexMatchTestData]] = matchTestDataSeqColumnType

    private implicit val etdsct: BaseColumnType[Seq[RegexExtractionTestData]] = extractionTestDataSeqColumnType


    def maxPoints: Rep[Int] = column[Int]("max_points")

    def correctionType: Rep[RegexCorrectionType] = column[RegexCorrectionType]("correction_type")

    def sampleSolutions: Rep[Seq[StringSampleSolution]] = column[Seq[StringSampleSolution]]("samples_json")

    def matchTestData: Rep[Seq[RegexMatchTestData]] = column[Seq[RegexMatchTestData]]("match_test_data_json")

    def extractionTestData: Rep[Seq[RegexExtractionTestData]] = column[Seq[RegexExtractionTestData]]("extraction_test_data_json")


    override def * : ProvenShape[RegexExercise] = (id, collectionId, semanticVersion, title, author, text, state,
      maxPoints, correctionType, sampleSolutions, matchTestData, extractionTestData) <> (RegexExercise.tupled, RegexExercise.unapply)

  }


  protected class RegexSampleSolutionsTable(tag: Tag) extends AStringSampleSolutionsTable(tag, "regex_sample_solutions")

  protected class RegexUserSolutionsTable(tag: Tag) extends AStringUserSolutionsTable(tag, "regex_user_solutions")


  protected class RegexExerciseReviewsTable(tag: Tag) extends ExerciseReviewsTable(tag, "regex_exercise_reviews") {

    //    override protected implicit val ptct: BaseColumnType[RegexExPart] = super.ptct


    override def * : ProvenShape[DbRegexExerciseReview] = (username, collectionId, exerciseId, exercisePart, difficulty,
      maybeDuration.?) <> (DbRegexExerciseReview.tupled, DbRegexExerciseReview.unapply)

  }

}
