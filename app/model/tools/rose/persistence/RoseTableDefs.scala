package model.tools.rose.persistence

import javax.inject.Inject
import model.persistence.ExerciseCollectionTableDefs
import model.tools.programming.{ProgDataType, ProgDataTypes, ProgLanguage, ProgLanguages}
import model.tools.rose._
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.ast.{ScalaBaseType, TypedType}
import slick.jdbc.JdbcProfile
import slick.lifted.{PrimaryKey, ProvenShape}

import scala.concurrent.{ExecutionContext, Future}
import scala.language.{implicitConversions, postfixOps}

class RoseTableDefs @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(override implicit val executionContext: ExecutionContext)
  extends HasDatabaseConfigProvider[JdbcProfile]
    with ExerciseCollectionTableDefs[RoseExercise, RoseExPart, RoseCollection, String, RoseSampleSolution, RoseUserSolution, RoseExerciseReview] {

  import profile.api._

  // Abstract types

  override protected type DbExType = DbRoseExercise

  override protected type ExTableDef = RoseExercisesTable


  override protected type CollTableDef = RoseCollectionsTable


  override protected type DbSampleSolType = DbRoseSampleSolution

  override protected type DbSampleSolTable = RoseSampleSolutionsTable


  override protected type DbUserSolType = DbRoseUserSolution

  override protected type DbUserSolTable = RoseSolutionsTable


  override protected type DbReviewType = DbRoseExerciseReview

  override protected type ReviewsTable = RoseExerciseReviewsTable

  // Table Queries
  override protected val collTable   : TableQuery[RoseCollectionsTable]     = TableQuery[RoseCollectionsTable]
  override protected val exTable     : TableQuery[RoseExercisesTable]       = TableQuery[RoseExercisesTable]
  override protected val solTable    : TableQuery[RoseSolutionsTable]       = TableQuery[RoseSolutionsTable]
  override protected val reviewsTable: TableQuery[RoseExerciseReviewsTable] = TableQuery[RoseExerciseReviewsTable]

  private val roseInputs  = TableQuery[RoseInputTypesTable]
  private val roseSamples = TableQuery[RoseSampleSolutionsTable]

  // Helper methods

  override protected val dbModels               = RoseDbModels
  override protected val exerciseReviewDbModels = RoseExerciseReviewDbModels

  override protected def copyDbUserSolType(oldSol: DbRoseUserSolution, newId: Int): DbRoseUserSolution = oldSol.copy(id = newId)

  override protected def exDbValuesFromExercise(collId: Int, compEx: RoseExercise): DbRoseExercise =
    dbModels.dbExerciseFromExercise(collId, compEx)

  // Queries

  override protected def completeExForEx(collId: Int, ex: DbRoseExercise): Future[RoseExercise] = for {
    inputTypes <- db.run(roseInputs.filter(_.exerciseId === ex.id).result) map (_ map dbModels.inputTypeFromDbInputType)
    samples <- db.run(roseSamples.filter(_.exerciseId === ex.id).result) map (_ map dbModels.sampleSolFromDbSampleSol)
  } yield dbModels.exerciseFromDbValues(ex, inputTypes, samples)


  override protected def saveExerciseRest(collId: Int, ex: RoseExercise): Future[Boolean] = {
    val dbSamples = ex.sampleSolutions map (s => dbModels.dbSampleSolFromSampleSol(ex.id, ex.semanticVersion, collId, s))
    val dbInputs = ex.inputTypes map (it => dbModels.dbInputTypeFromInputType(ex.id, ex.semanticVersion, collId, it))

    for {
      inputsSaved <- saveSeq[DbRoseInputType](dbInputs, it => db.run(roseInputs insertOrUpdate it))
      samplesSaved <- saveSeq[DbRoseSampleSolution](dbSamples, rss => db.run(roseSamples insertOrUpdate rss))
    } yield inputsSaved && samplesSaved
  }


  override def futureSampleSolutionsForExPart(collId: Int, exerciseId: Int, part: RoseExPart): Future[Seq[String]] = db.run(
    roseSamples
      .filter {
        s => s.exerciseId === exerciseId && s.collectionId === collId
      }
      .map(_.sample)
      .result
  )

  // Implicit column types

  implicit val ProgLanguageColumnType: BaseColumnType[ProgLanguage] =
    MappedColumnType.base[ProgLanguage, String](_.entryName, ProgLanguages.withNameInsensitive)

  implicit val ProgDataTypesColumnType: BaseColumnType[ProgDataType] =
    MappedColumnType.base[ProgDataType, String](_.typeName, str => ProgDataTypes.byName(str) getOrElse ProgDataTypes.STRING)

  override protected implicit val partTypeColumnType: BaseColumnType[RoseExPart] =
    MappedColumnType.base[RoseExPart, String](_.entryName, RoseExParts.withNameInsensitive)

  override protected implicit val solTypeColumnType: TypedType[String] = ScalaBaseType.stringType

  // Tables

  class RoseCollectionsTable(tag: Tag) extends ExerciseCollectionTable(tag, "rose_collections") {

    def * = (id, title, author, text, state, shortName) <> (RoseCollection.tupled, RoseCollection.unapply)

  }

  class RoseExercisesTable(tag: Tag) extends ExerciseInCollectionTable(tag, "rose_exercises") {

    def fieldWidth: Rep[Int] = column[Int]("field_width")

    def fieldHeight: Rep[Int] = column[Int]("field_height")

    def isMultiplayer: Rep[Boolean] = column[Boolean]("is_mp")


    override def * : ProvenShape[DbRoseExercise] = (id, semanticVersion, title, author, text, state, fieldWidth, fieldHeight, isMultiplayer) <> (DbRoseExercise.tupled, DbRoseExercise.unapply)

  }

  class RoseInputTypesTable(tag: Tag) extends ExForeignKeyTable[DbRoseInputType](tag, "rose_inputs") {

    def id: Rep[Int] = column[Int]("id")

    def collectionId: Rep[Int] = column[Int]("coll_id")

    def name: Rep[String] = column[String]("input_name")

    def inputType: Rep[ProgDataType] = column[ProgDataType]("input_type")


    def pk: PrimaryKey = primaryKey("pk", (id, exerciseId, exSemVer))


    override def * : ProvenShape[DbRoseInputType] = (id, exerciseId, exSemVer, collectionId, name, inputType) <> (DbRoseInputType.tupled, DbRoseInputType.unapply)

  }

  class RoseSampleSolutionsTable(tag: Tag) extends ASampleSolutionsTable(tag, "rose_samples") {

    def language: Rep[ProgLanguage] = column[ProgLanguage]("language")


    def pk: PrimaryKey = primaryKey("pk", (id, exerciseId, exSemVer, language))


    override def * : ProvenShape[DbRoseSampleSolution] = (id, exerciseId, exSemVer, collectionId, language, sample) <> (DbRoseSampleSolution.tupled, DbRoseSampleSolution.unapply)

  }

  class RoseSolutionsTable(tag: Tag) extends AUserSolutionsTable(tag, "rose_solutions") {

    def language: Rep[ProgLanguage] = column[ProgLanguage]("language")

    override def * : ProvenShape[DbRoseUserSolution] = (id, exerciseId, exSemVer, collectionId, username, part,
      language, solution, points, maxPoints) <> (DbRoseUserSolution.tupled, DbRoseUserSolution.unapply)

  }

  class RoseExerciseReviewsTable(tag: Tag) extends ExerciseReviewsTable(tag, "rose_exercise_reviews") {

    override def * : ProvenShape[DbRoseExerciseReview] = (username, collectionId, exerciseId, exercisePart, difficulty, maybeDuration.?) <> (DbRoseExerciseReview.tupled, DbRoseExerciseReview.unapply)

  }

}
