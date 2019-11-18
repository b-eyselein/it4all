package model.tools.rose.persistence

import javax.inject.Inject
import model.core.CoreConsts.{sampleName, solutionName}
import model.persistence.ExerciseTableDefs
import model.tools.programming.{ProgDataType, ProgLanguage, ProgLanguages, ProgrammingToolJsonProtocol}
import model.tools.rose._
import model.{ExParts, SemanticVersion}
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

  override protected type DbExType = DbRoseExercise

  override protected type ExTableDef = RoseExercisesTable


  override protected type CollTableDef = RoseExerciseCollectionsTable


  override protected type DbSampleSolType = DbRoseSampleSolution

  override protected type DbSampleSolTable = RoseSampleSolutionsTable


  override protected type DbUserSolType = DbRoseUserSolution

  override protected type DbUserSolTable = RoseSolutionsTable


  override protected type DbReviewType = DbRoseExerciseReview

  override protected type ReviewsTable = RoseExerciseReviewsTable

  // Table Queries
  override protected val exTable  : TableQuery[RoseExercisesTable]           = TableQuery[RoseExercisesTable]
  override protected val collTable: TableQuery[RoseExerciseCollectionsTable] = TableQuery[RoseExerciseCollectionsTable]

  override protected val sampleSolutionsTableQuery: TableQuery[RoseSampleSolutionsTable] = TableQuery[RoseSampleSolutionsTable]
  override protected val userSolutionsTableQuery  : TableQuery[RoseSolutionsTable]       = TableQuery[RoseSolutionsTable]

  override protected val reviewsTable: TableQuery[RoseExerciseReviewsTable] = TableQuery[RoseExerciseReviewsTable]

  private val roseInputs = TableQuery[RoseInputTypesTable]

  // Helper methods

  override protected val exParts: ExParts[RoseExPart] = RoseExParts

  override protected val dbModels               = RoseDbModels
  override protected val exerciseReviewDbModels = RoseExerciseReviewDbModels

  override protected def copyDbUserSolType(oldSol: DbRoseUserSolution, newId: Int): DbRoseUserSolution = oldSol.copy(id = newId)

  // Queries

  override protected def completeExForEx(collId: Int, ex: DbRoseExercise): Future[RoseExercise] = for {
    inputTypes <- db.run(roseInputs.filter(_.exerciseId === ex.id).result).map(_.map(RoseDbModels.inputTypeFromDbInputType))
    samples <- db.run(sampleSolutionsTableQuery.filter(_.exerciseId === ex.id).result).map(_.map(RoseSolutionDbModels.sampleSolFromDbSampleSol))
  } yield RoseDbModels.exerciseFromDbValues(ex, inputTypes, samples)


  override protected def saveExerciseRest(collId: Int, ex: RoseExercise): Future[Boolean] = {
    val dbSamples = ex.sampleSolutions.map(s => RoseSolutionDbModels.dbSampleSolFromSampleSol(ex.id, ex.semanticVersion, collId, s))
    val dbInputs  = ex.inputTypes.map(it => RoseDbModels.dbInputTypeFromInputType(ex.id, ex.semanticVersion, collId, it))

    for {
      inputsSaved <- saveSeq[DbRoseInputType](dbInputs, it => db.run(roseInputs insertOrUpdate it))
      samplesSaved <- saveSeq[DbRoseSampleSolution](dbSamples, rss => db.run(sampleSolutionsTableQuery insertOrUpdate rss))
    } yield inputsSaved && samplesSaved
  }


  override def futureSampleSolutionsForExPart(collId: Int, exerciseId: Int, part: RoseExPart): Future[Seq[RoseSampleSolution]] =
    db.run(sampleSolutionsTableQuery
      .filter { s => s.exerciseId === exerciseId && s.collectionId === collId }
      .result)
      .map(_.map(RoseSolutionDbModels.sampleSolFromDbSampleSol))

  def futureMaybeOldSolution(username: String, scenarioId: Int, exerciseId: Int, part: RoseExPart): Future[Option[RoseUserSolution]] = {
    // FIXME: implement?!
    Future.successful(None)
    // ???
  }

  def futureSaveUserSolution(exId: Int, exSemVer: SemanticVersion, collId: Int, username: String, sol: RoseUserSolution): Future[Boolean] = {
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

    def fieldWidth: Rep[Int] = column[Int]("field_width")

    def fieldHeight: Rep[Int] = column[Int]("field_height")

    def isMultiplayer: Rep[Boolean] = column[Boolean]("is_mp")


    override def * : ProvenShape[DbRoseExercise] = (id, collectionId, semanticVersion, title, author, text, state,
      fieldWidth, fieldHeight, isMultiplayer) <> (DbRoseExercise.tupled, DbRoseExercise.unapply)

  }

  protected class RoseInputTypesTable(tag: Tag) extends ExForeignKeyTable[DbRoseInputType](tag, "rose_inputs") {

    private implicit val pdtct: BaseColumnType[ProgDataType] = progDataTypeColumnType


    def id: Rep[Int] = column[Int]("id")

    def name: Rep[String] = column[String]("input_name")

    def inputType: Rep[ProgDataType] = column[ProgDataType]("input_type")


    def pk: PrimaryKey = primaryKey("pk", (id, exerciseId, exSemVer))


    override def * : ProvenShape[DbRoseInputType] = (id, exerciseId, exSemVer, collectionId, name, inputType) <> (DbRoseInputType.tupled, DbRoseInputType.unapply)

  }

  protected class RoseSampleSolutionsTable(tag: Tag) extends ASampleSolutionsTable(tag, "rose_sample_solutions") {

    private implicit val plct: BaseColumnType[ProgLanguage] = progLanguageColumnType


    def sample: Rep[String] = column[String](sampleName)

    def language: Rep[ProgLanguage] = column[ProgLanguage]("language")


    def pk: PrimaryKey = primaryKey("pk", (id, exerciseId, exSemVer, language))


    override def * : ProvenShape[DbRoseSampleSolution] = (id, exerciseId, exSemVer, collectionId, language, sample) <> (DbRoseSampleSolution.tupled, DbRoseSampleSolution.unapply)

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
