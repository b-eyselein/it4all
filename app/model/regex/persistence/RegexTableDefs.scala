package model.regex.persistence

import javax.inject.Inject
import model.persistence.ExerciseCollectionTableDefs
import model.regex.RegexConsts._
import model.regex._
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.ast.{ScalaBaseType, TypedType}
import slick.jdbc.JdbcProfile
import slick.lifted.ProvenShape

import scala.concurrent.{ExecutionContext, Future}

class RegexTableDefs @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(override implicit val executionContext: ExecutionContext)
  extends HasDatabaseConfigProvider[JdbcProfile] with ExerciseCollectionTableDefs[RegexExercise, RegexExPart, RegexCollection, String, RegexSampleSolution, RegexDBSolution /*, RegexExerciseReview*/ ] {

  import profile.api._

  // Abstract types

  override protected type ExDbValues = DbRegexExercise

  override protected type ExTableDef = RegexExerciseTable
  override protected type CollTableDef = RegexCollectionsTable

  override protected type SamplesTableDef = RegexSampleSolutionsTable
  override protected type SolTableDef = RegexSolutionTable
  //  override protected type ReviewsTableDef = RegexExerciseReviewsTable

  // Table Queries

  override protected val collTable: TableQuery[RegexCollectionsTable] = TableQuery[RegexCollectionsTable]
  override protected val exTable  : TableQuery[RegexExerciseTable]    = TableQuery[RegexExerciseTable]
  override protected val solTable : TableQuery[RegexSolutionTable]    = TableQuery[RegexSolutionTable]
  //  override protected val reviewsTable: TableQuery[RegexExerciseReviewsTable] = TableQuery[RegexExerciseReviewsTable]

  private val regexSampleSolutionsTable: TableQuery[RegexSampleSolutionsTable] = TableQuery[RegexSampleSolutionsTable]
  private val regexTestDataTable       : TableQuery[RegexTestDataTable]        = TableQuery[RegexTestDataTable]

  // Helper methods

  override protected def exDbValuesFromExercise(compEx: RegexExercise): ExDbValues = RegexDbModels.dbExerciseFromExercise(compEx)

  override protected def copyDBSolType(oldSol: RegexDBSolution, newId: Int): RegexDBSolution = oldSol.copy(id = newId)

  // Queries

  override protected def completeExForEx(ex: DbRegexExercise): Future[RegexExercise] = for {
    sampleSolutions <- db.run(regexSampleSolutionsTable.filter {
      e => e.id === ex.id && e.exSemVer === ex.semanticVersion
    }.result)
    testData <- db.run(regexTestDataTable.filter {
      td => td.exerciseId === ex.id && td.exSemVer === ex.semanticVersion
    }.result)
  } yield RegexDbModels.exerciseFromDbExercise(ex, sampleSolutions, testData)

  override protected def saveExerciseRest(compEx: RegexExercise): Future[Boolean] = for {
    samplesSaved <- saveSeq[RegexSampleSolution](compEx.sampleSolutions, sample => db.run(regexSampleSolutionsTable += sample), Some("RegexSampleSolution"))
    testDataSaved <- saveSeq[RegexTestData](compEx.testData, td => db.run(regexTestDataTable += td), Some("RegexTestData"))
  } yield samplesSaved && testDataSaved

  // Other queries

  override def futureSampleSolutionsForExPart(collectionId: Int, exerciseId: Int, part: RegexExPart): Future[Seq[String]] =
    db.run(regexSampleSolutionsTable.filter {
      s => s.collectionId === collectionId && s.exerciseId === exerciseId
    }.map(_.sample).result)

  // Column types

  override protected implicit val solutionTypeColumnType: TypedType[String] = ScalaBaseType.stringType

  override protected implicit val partTypeColumnType: BaseColumnType[RegexExPart] =
    MappedColumnType.base[RegexExPart, String](_.entryName, RegexExParts.withNameInsensitive)

  // Tables

  class RegexCollectionsTable(tag: Tag) extends ExerciseCollectionTable(tag, "regex_collections") {

    override def * : ProvenShape[RegexCollection] = (id, title, author, text, state, shortName) <> (RegexCollection.tupled, RegexCollection.unapply)

  }

  class RegexExerciseTable(tag: Tag) extends ExerciseInCollectionTable(tag, "regex_exercises") {

    override def * : ProvenShape[DbRegexExercise] = (id, semanticVersion, collectionId, title, author, text, state) <> (DbRegexExercise.tupled, DbRegexExercise.unapply)

  }

  class RegexSampleSolutionsTable(tag: Tag) extends ACollectionSamplesTable(tag, "regex_sample_solutions") {

    override def * : ProvenShape[RegexSampleSolution] = (id, exerciseId, exSemVer, collectionId, sample) <> (RegexSampleSolution.tupled, RegexSampleSolution.unapply)

  }

  class RegexTestDataTable(tag: Tag) extends ExForeignKeyTable[RegexTestData](tag, "regex_test_data") {

    def id: Rep[Int] = column[Int](idName)

    def collectionId: Rep[Int] = column[Int]("collection_id")

    def data: Rep[String] = column[String](dataName)

    def isIncluded: Rep[Boolean] = column[Boolean]("is_included")


    def pk = primaryKey("pk", (id, exerciseId, exSemVer, collectionId))


    def * : ProvenShape[RegexTestData] = (id, exerciseId, exSemVer, collectionId, data, isIncluded) <> (RegexTestData.tupled, RegexTestData.unapply)

  }

  class RegexSolutionTable(tag: Tag) extends CollectionExSolutionsTable(tag, "regex_solutions") {

    def solution: Rep[String] = column[String]("solution")


    def pk = primaryKey("pk", id)


    override def * : ProvenShape[RegexDBSolution] = (id, username, exerciseId, exSemVer, collectionId, part,
      solution, points, maxPoints) <> (RegexDBSolution.tupled, RegexDBSolution.unapply)

  }

  //  class RegexExerciseReviewsTable(tag: Tag) extends ExerciseReviewsTable(tag, "regex_exercise_reviews") {
  //
  //    override def * : ProvenShape[RegexExerciseReview] = (username, exerciseId, exerciseSemVer, exercisePart, difficulty,
  //      maybeDuration.?) <> (RegexExerciseReview.tupled, RegexExerciseReview.unapply)
  //
  //  }

}
