package model.rose

import javax.inject.Inject
import model.SemanticVersion
import model.persistence.SingleExerciseTableDefs
import model.programming.ProgDataTypes.ProgDataType
import model.programming.{ProgDataTypes, ProgLanguage, ProgLanguages}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile
import slick.lifted.{ForeignKeyQuery, PrimaryKey, ProvenShape}

import scala.concurrent.{ExecutionContext, Future}
import scala.language.{implicitConversions, postfixOps}

class RoseTableDefs @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(override implicit val executionContext: ExecutionContext)
  extends HasDatabaseConfigProvider[JdbcProfile] with SingleExerciseTableDefs[RoseExercise, RoseCompleteEx, String, RoseSolution, RoseExPart, RoseExerciseReview] {

  import profile.api._

  // Abstract types

  override protected type ExTableDef = RoseExercisesTable
  override protected type SolTableDef = RoseSolutionsTable
  override protected type ReviewsTableDef = RoseExerciseReviewsTable

  // Table Queries

  override protected val exTable      = TableQuery[RoseExercisesTable]
  override protected val solTable     = TableQuery[RoseSolutionsTable]
  override protected val reviewsTable = TableQuery[RoseExerciseReviewsTable]


  val roseInputs = TableQuery[RoseInputTypesTable]

  val roseSamples = TableQuery[RoseSampleSolutionsTable]

  val roseSolutions = TableQuery[RoseSolutionsTable]

  // Queries

  override protected def completeExForEx(ex: RoseExercise): Future[RoseCompleteEx] = for {
    inputTypes <- db.run(roseInputs.filter(_.exerciseId === ex.id).result)
    samples <- db.run(roseSamples.filter(_.exerciseId === ex.id).result)
  } yield RoseCompleteEx(ex, inputTypes, samples)

  override protected def saveExerciseRest(compEx: RoseCompleteEx): Future[Boolean] = for {
    inputsSaved <- saveSeq[RoseInputType](compEx.inputType, it => db.run(roseInputs insertOrUpdate it))
    samplesSaved <- saveSeq[RoseSampleSolution](compEx.sampleSolution, rss => db.run(roseSamples insertOrUpdate rss))
  } yield inputsSaved && samplesSaved

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


    override def * : ProvenShape[RoseExercise] = (id, semanticVersion, title, author, text, state, fieldWidth, fieldHeight, isMultiplayer) <> (RoseExercise.tupled, RoseExercise.unapply)

  }

  class RoseInputTypesTable(tag: Tag) extends Table[RoseInputType](tag, "rose_inputs") {

    def id: Rep[Int] = column[Int]("id")

    def exerciseId: Rep[Int] = column[Int]("exercise_id")

    def exSemVer: Rep[SemanticVersion] = column[SemanticVersion]("ex_sem_ver")

    def name: Rep[String] = column[String]("input_name")

    def inputType: Rep[ProgDataType] = column[ProgDataType]("input_type")


    def pk: PrimaryKey = primaryKey("pk", (id, exerciseId, exSemVer))

    def exerciseFk: ForeignKeyQuery[RoseExercisesTable, RoseExercise] = foreignKey("exercise_fk", (exerciseId, exSemVer), exTable)(ex => (ex.id, ex.semanticVersion))


    override def * : ProvenShape[RoseInputType] = (id, exerciseId, exSemVer, name, inputType) <> (RoseInputType.tupled, RoseInputType.unapply)

  }

  class RoseSampleSolutionsTable(tag: Tag) extends Table[RoseSampleSolution](tag, "rose_samples") {

    def exerciseId: Rep[Int] = column[Int]("exercise_id")

    def exSemVer: Rep[SemanticVersion] = column[SemanticVersion]("ex_sem_ver")

    def language: Rep[ProgLanguage] = column[ProgLanguage]("language")

    def solution: Rep[String] = column[String]("solution")


    def pk: PrimaryKey = primaryKey("pk", (exerciseId, exSemVer, language))

    def exerciseFk: ForeignKeyQuery[RoseExercisesTable, RoseExercise] = foreignKey("exercise_fk", (exerciseId, exSemVer), exTable)(ex => (ex.id, ex.semanticVersion))


    override def * : ProvenShape[RoseSampleSolution] = (exerciseId, exSemVer, language, solution) <> (RoseSampleSolution.tupled, RoseSampleSolution.unapply)

  }

  class RoseSolutionsTable(tag: Tag) extends PartSolutionsTable(tag, "rose_solutions") {

    def solution: Rep[String] = column[String]("solution")


    override def * : ProvenShape[RoseSolution] = (username, exerciseId, exSemVer, part, solution, points, maxPoints) <> (RoseSolution.tupled, RoseSolution.unapply)

  }

  class RoseExerciseReviewsTable(tag: Tag) extends ExerciseReviewsTable(tag, "rose_exercise_reviews") {

    override def * : ProvenShape[RoseExerciseReview] = (username, exerciseId, exerciseSemVer, exercisePart, difficulty, maybeDuration.?) <> (RoseExerciseReview.tupled, RoseExerciseReview.unapply)

  }

}