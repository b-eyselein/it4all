package model.rose.persistence

import javax.inject.Inject
import model.persistence.IdExerciseTableDefs
import model.programming.{ProgDataType, ProgDataTypes, ProgLanguage, ProgLanguages}
import model.rose._
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile
import slick.lifted.{PrimaryKey, ProvenShape}

import scala.concurrent.{ExecutionContext, Future}
import scala.language.{implicitConversions, postfixOps}

class RoseTableDefs @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(override implicit val executionContext: ExecutionContext)
  extends HasDatabaseConfigProvider[JdbcProfile] with IdExerciseTableDefs[RoseExercise, RoseExPart, String, RoseSolution, RoseExerciseReview] {

  import profile.api._

  // Abstract types

  override protected type ExDbValues = DbRoseExercise
  override protected type ExTableDef = RoseExercisesTable
  override protected type SolTableDef = RoseSolutionsTable
  override protected type ReviewsTableDef = RoseExerciseReviewsTable

  // Table Queries

  override protected val exTable      = TableQuery[RoseExercisesTable]
  override protected val solTable     = TableQuery[RoseSolutionsTable]
  override protected val reviewsTable = TableQuery[RoseExerciseReviewsTable]

  private val roseInputs  = TableQuery[RoseInputTypesTable]
  private val roseSamples = TableQuery[RoseSampleSolutionsTable]

  // Helper methods

  override protected def exDbValuesFromExercise(compEx: RoseExercise): DbRoseExercise = RoseDbModels.dbExerciseFromExercise(compEx)

  override protected def copyDBSolType(oldSol: RoseSolution, newId: Int): RoseSolution = oldSol.copy(id = newId)

  // Queries

  override protected def completeExForEx(ex: DbRoseExercise): Future[RoseExercise] = for {
    inputTypes <- db.run(roseInputs.filter(_.exerciseId === ex.id).result)
    samples <- db.run(roseSamples.filter(_.exerciseId === ex.id).result)
  } yield RoseDbModels.exerciseFromDbValues(ex, inputTypes, samples)

  override protected def saveExerciseRest(compEx: RoseExercise): Future[Boolean] = for {
    inputsSaved <- saveSeq[RoseInputType](compEx.inputTypes, it => db.run(roseInputs insertOrUpdate it))
    samplesSaved <- saveSeq[RoseSampleSolution](compEx.sampleSolutions, rss => db.run(roseSamples insertOrUpdate rss))
  } yield inputsSaved && samplesSaved


  override def futureSampleSolutionsForExercisePart(exerciseId: Int, part: RoseExPart): Future[Seq[String]] =
    db.run(roseSamples.filter(_.exerciseId === exerciseId).map(_.solution).result)

  // Implicit column types

  implicit val ProgLanguageColumnType: BaseColumnType[ProgLanguage] =
    MappedColumnType.base[ProgLanguage, String](_.entryName, ProgLanguages.withNameInsensitive)

  implicit val ProgDataTypesColumnType: BaseColumnType[ProgDataType] =
    MappedColumnType.base[ProgDataType, String](_.typeName, str => ProgDataTypes.byName(str) getOrElse ProgDataTypes.STRING)

  override protected implicit val partTypeColumnType: BaseColumnType[RoseExPart] =
    MappedColumnType.base[RoseExPart, String](_.entryName, RoseExParts.withNameInsensitive)

  // Tables

  class RoseExercisesTable(tag: Tag) extends ExerciseTableDef(tag, "rose_exercises") {

    def fieldWidth: Rep[Int] = column[Int]("field_width")

    def fieldHeight: Rep[Int] = column[Int]("field_height")

    def isMultiplayer: Rep[Boolean] = column[Boolean]("is_mp")


    override def * : ProvenShape[DbRoseExercise] = (id, semanticVersion, title, author, text, state, fieldWidth, fieldHeight, isMultiplayer) <> (DbRoseExercise.tupled, DbRoseExercise.unapply)

  }

  class RoseInputTypesTable(tag: Tag) extends ExForeignKeyTable[RoseInputType](tag, "rose_inputs") {

    def id: Rep[Int] = column[Int]("id")

    def name: Rep[String] = column[String]("input_name")

    def inputType: Rep[ProgDataType] = column[ProgDataType]("input_type")


    def pk: PrimaryKey = primaryKey("pk", (id, exerciseId, exSemVer))


    override def * : ProvenShape[RoseInputType] = (id, exerciseId, exSemVer, name, inputType) <> (RoseInputType.tupled, RoseInputType.unapply)

  }

  class RoseSampleSolutionsTable(tag: Tag) extends ExForeignKeyTable[RoseSampleSolution](tag, "rose_samples") {

    def id: Rep[Int] = column[Int]("id")

    def language: Rep[ProgLanguage] = column[ProgLanguage]("language")

    def solution: Rep[String] = column[String]("solution")


    def pk: PrimaryKey = primaryKey("pk", (id, exerciseId, exSemVer, language))


    override def * : ProvenShape[RoseSampleSolution] = (id, exerciseId, exSemVer, language, solution) <> (RoseSampleSolution.tupled, RoseSampleSolution.unapply)

  }

  class RoseSolutionsTable(tag: Tag) extends AUserSolutionsTable(tag, "rose_solutions") {

    def solution: Rep[String] = column[String]("solution")


    override def * : ProvenShape[RoseSolution] = (id, username, exerciseId, exSemVer, part, solution, points, maxPoints) <> (RoseSolution.tupled, RoseSolution.unapply)

  }

  class RoseExerciseReviewsTable(tag: Tag) extends ExerciseReviewsTable(tag, "rose_exercise_reviews") {

    override def * : ProvenShape[RoseExerciseReview] = (username, exerciseId, exerciseSemVer, exercisePart, difficulty, maybeDuration.?) <> (RoseExerciseReview.tupled, RoseExerciseReview.unapply)

  }

}
