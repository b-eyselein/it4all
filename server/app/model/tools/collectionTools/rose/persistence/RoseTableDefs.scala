package model.tools.collectionTools.rose.persistence

import javax.inject.Inject
import model.SemanticVersion
import model.core.CoreConsts.solutionName
import model.persistence.ExerciseTableDefs
import model.tools.collectionTools.ExParts
import model.tools.collectionTools.programming.{ProgDataType, ProgLanguage, ProgLanguages, ProgrammingToolJsonProtocol}
import model.tools.collectionTools.rose._
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile
import slick.lifted.{PrimaryKey, ProvenShape}

import scala.concurrent.{ExecutionContext, Future}
import scala.language.{implicitConversions, postfixOps}

class RoseTableDefs @Inject()(override protected val dbConfigProvider: DatabaseConfigProvider)(override implicit val executionContext: ExecutionContext)
  extends HasDatabaseConfigProvider[JdbcProfile]
    with ExerciseTableDefs[RoseExPart, RoseExercise, String, RoseSampleSolution, RoseUserSolution, RoseExerciseReview] {

  import profile.api._

  // Abstract types

  override protected type DbExType = RoseExercise

  override protected type ExTableDef = RoseExercisesTable

  override protected type CollTableDef = RoseExerciseCollectionsTable


  override protected type DbUserSolType = DbRoseUserSolution

  override protected type DbUserSolTable = RoseSolutionsTable


  override protected type DbReviewType = DbRoseExerciseReview

  override protected type ReviewsTable = RoseExerciseReviewsTable

  // Table Queries
  override protected val exTable  : TableQuery[RoseExercisesTable]           = TableQuery[RoseExercisesTable]
  override protected val collTable: TableQuery[RoseExerciseCollectionsTable] = TableQuery[RoseExerciseCollectionsTable]

  override protected val userSolutionsTableQuery  : TableQuery[RoseSolutionsTable]       = TableQuery[RoseSolutionsTable]

  override protected val reviewsTable: TableQuery[RoseExerciseReviewsTable] = TableQuery[RoseExerciseReviewsTable]

  // Helper methods

  override protected val exParts: ExParts[RoseExPart] = RoseExParts

  override protected val dbModels               = RoseDbModels
  override protected val exerciseReviewDbModels = RoseExerciseReviewDbModels

  override protected def copyDbUserSolType(oldSol: DbRoseUserSolution, newId: Int): DbRoseUserSolution = oldSol.copy(id = newId)

  // Queries

  override protected def completeExForEx(collId: Int, ex: RoseExercise): Future[RoseExercise] = Future.successful(ex)

  override protected def saveExerciseRest(collId: Int, ex: RoseExercise): Future[Boolean] =
    Future.successful(true)


  override def futureMaybeOldSolution(username: String, scenarioId: Int, exerciseId: Int, part: RoseExPart): Future[Option[RoseUserSolution]] = {
    // FIXME: implement?!
    Future.successful(None)
    // ???
  }

  override def futureSaveUserSolution(exId: Int, exSemVer: SemanticVersion, collId: Int, username: String, sol: RoseUserSolution): Future[Boolean] = {
    // FIXME: implement ?!
    Future.successful(false)
    // ???
  }

  // Implicit column types

  private val progLanguageColumnType: BaseColumnType[ProgLanguage] =
    MappedColumnType.base[ProgLanguage, String](_.entryName, ProgLanguages.withNameInsensitive)

  private val progDataTypeColumnType: BaseColumnType[ProgDataType] =
    jsonColumnType(ProgrammingToolJsonProtocol.progDataTypeFormat)

  override protected implicit val partTypeColumnType: BaseColumnType[RoseExPart] = jsonColumnType(exParts.jsonFormat)

  // Tables

  protected class RoseExerciseCollectionsTable(tag: Tag) extends ExerciseCollectionsTable(tag, "rose_collections")

  protected class RoseExercisesTable(tag: Tag) extends ExerciseInCollectionTable(tag, "rose_exercises") {

    private implicit val ritct: BaseColumnType[Seq[RoseInputType]] = jsonSeqColumnType(RoseToolJsonProtocol.roseInputTypeFormat)

    private implicit val ssct: BaseColumnType[Seq[RoseSampleSolution]] = jsonSeqColumnType(RoseToolJsonProtocol.sampleSolutionFormat)


    def fieldWidth: Rep[Int] = column[Int]("field_width")

    def fieldHeight: Rep[Int] = column[Int]("field_height")

    def isMultiplayer: Rep[Boolean] = column[Boolean]("is_mp")

    def inputTypes: Rep[Seq[RoseInputType]] = column[Seq[RoseInputType]]("input_types_json")

    def sampleSolutions: Rep[Seq[RoseSampleSolution]] = column[Seq[RoseSampleSolution]]("sample_solutions_json")


    override def * : ProvenShape[RoseExercise] = (
      id, collectionId, toolId, semanticVersion,
      title, author, text, state,
      fieldWidth, fieldHeight, isMultiplayer,
      inputTypes, sampleSolutions
    ) <> (RoseExercise.tupled, RoseExercise.unapply)

  }

  protected class RoseInputTypesTable(tag: Tag) extends ExForeignKeyTable[DbRoseInputType](tag, "rose_inputs") {

    private implicit val pdtct: BaseColumnType[ProgDataType] = progDataTypeColumnType


    def id: Rep[Int] = column[Int]("id")

    def name: Rep[String] = column[String]("input_name")

    def inputType: Rep[ProgDataType] = column[ProgDataType]("input_type")


    def pk: PrimaryKey = primaryKey("pk", (id, exerciseId, exSemVer))


    override def * : ProvenShape[DbRoseInputType] = (id, exerciseId, exSemVer, collectionId, name, inputType) <> (DbRoseInputType.tupled, DbRoseInputType.unapply)

  }

  protected class RoseSolutionsTable(tag: Tag) extends AUserSolutionsTable(tag, "rose_user_solutions") {

    private implicit val plct: BaseColumnType[ProgLanguage] = progLanguageColumnType


    def solution: Rep[String] = column[String](solutionName)

    def language: Rep[ProgLanguage] = column[ProgLanguage]("language")


    override def * : ProvenShape[DbRoseUserSolution] = (id, exerciseId, exSemVer, collectionId, username, part,
      language, solution, points, maxPoints) <> (DbRoseUserSolution.tupled, DbRoseUserSolution.unapply)

  }

  protected class RoseExerciseReviewsTable(tag: Tag) extends ExerciseReviewsTable(tag, "rose_exercise_reviews") {

    override def * : ProvenShape[DbRoseExerciseReview] = (username, collectionId, exerciseId, exercisePart, difficulty, maybeDuration.?) <> (DbRoseExerciseReview.tupled, DbRoseExerciseReview.unapply)

  }

}
