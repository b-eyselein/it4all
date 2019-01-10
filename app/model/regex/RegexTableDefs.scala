package model.regex

import javax.inject.Inject
import model.persistence.SingleExerciseTableDefs
import model.regex.RegexConsts._
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile
import slick.lifted.ProvenShape

import scala.concurrent.{ExecutionContext, Future}

class RegexTableDefs @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(override implicit val executionContext: ExecutionContext)
  extends HasDatabaseConfigProvider[JdbcProfile] with SingleExerciseTableDefs[RegexExercise, RegexCompleteEx, String, RegexDBSolution, RegexExPart, RegexExerciseReview] {

  import profile.api._

  override protected type ExTableDef = RegexExerciseTable
  override protected type SolTableDef = RegexSolutionTable
  override protected type ReviewsTableDef = RegexExerciseReviewsTable

  override protected val exTable     : TableQuery[RegexExerciseTable]        = TableQuery[RegexExerciseTable]
  override protected val solTable    : TableQuery[RegexSolutionTable]        = TableQuery[RegexSolutionTable]
  override protected val reviewsTable: TableQuery[RegexExerciseReviewsTable] = TableQuery[RegexExerciseReviewsTable]

  private val regexSampleSolutionsTable: TableQuery[RegexSampleSolutionsTable] = TableQuery[RegexSampleSolutionsTable]
  private val regexTestDataTable       : TableQuery[RegexTestDataTable]        = TableQuery[RegexTestDataTable]

  override protected implicit val partTypeColumnType: BaseColumnType[RegexExPart] =
    MappedColumnType.base[RegexExPart, String](_.entryName, RegexExParts.withNameInsensitive)


  override protected def copyDBSolType(oldSol: RegexDBSolution, newId: Int): RegexDBSolution = oldSol.copy(id = newId)

  override protected def completeExForEx(ex: RegexExercise): Future[RegexCompleteEx] = for {
    sampleSolutions <- db.run(regexSampleSolutionsTable.filter {
      e => e.id === ex.id && e.exSemVer === ex.semanticVersion
    }.result)
    testData <- db.run(regexTestDataTable.filter {
      td => td.exerciseId === ex.id && td.exSemVer === ex.semanticVersion
    }.result)
  } yield RegexCompleteEx(ex, sampleSolutions, testData)

  override protected def saveExerciseRest(compEx: RegexCompleteEx): Future[Boolean] = for {
    samplesSaved <- saveSeq[RegexSampleSolution](compEx.sampleSolutions, sample => db.run(regexSampleSolutionsTable += sample))
    testDataSaved <- saveSeq[RegexTestData](compEx.testData, td => db.run(regexTestDataTable += td))
  } yield samplesSaved && testDataSaved

  // Other queries

  override def futureSampleSolutionsForExercisePart(exerciseId: Int, part: RegexExPart): Future[Seq[String]] =
    db.run(regexSampleSolutionsTable.filter(_.exerciseId === exerciseId).map(_.sample).result)

  // Tables

  class RegexExerciseTable(tag: Tag) extends ExerciseTableDef(tag, "regex_exercises") {

    override def * : ProvenShape[RegexExercise] = (id, title, author, text, state, semanticVersion) <> (RegexExercise.tupled, RegexExercise.unapply)

  }

  class RegexSampleSolutionsTable(tag: Tag) extends ExForeignKeyTable[RegexSampleSolution](tag, "regex_sample_solutions") {

    def id: Rep[Int] = column[Int](idName)

    def sample: Rep[String] = column[String](sampleName)


    override def * : ProvenShape[RegexSampleSolution] = (id, exerciseId, exSemVer, sample) <> (RegexSampleSolution.tupled, RegexSampleSolution.unapply)

  }

  class RegexTestDataTable(tag: Tag) extends ExForeignKeyTable[RegexTestData](tag, "regex_test_data") {

    def id: Rep[Int] = column[Int](idName)

    def data: Rep[String] = column[String]("data")

    def isIncluded: Rep[Boolean] = column[Boolean]("is_included")


    def * : ProvenShape[RegexTestData] = (id, exerciseId, exSemVer, data, isIncluded) <> (RegexTestData.tupled, RegexTestData.unapply)

  }

  class RegexSolutionTable(tag: Tag) extends PartSolutionsTable(tag, "regex_solutions") {

    def solution: Rep[String] = column[String]("solution")


    override def * : ProvenShape[RegexDBSolution] = (id, username, exerciseId, exSemVer, part, solution, points, maxPoints) <> (RegexDBSolution.tupled, RegexDBSolution.unapply)

  }

  class RegexExerciseReviewsTable(tag: Tag) extends ExerciseReviewsTable(tag, "regex_exercise_reviews") {

    override def * : ProvenShape[RegexExerciseReview] = (username, exerciseId, exerciseSemVer, exercisePart, difficulty,
      maybeDuration.?) <> (RegexExerciseReview.tupled, RegexExerciseReview.unapply)

  }

}
