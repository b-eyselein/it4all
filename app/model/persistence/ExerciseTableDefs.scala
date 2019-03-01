package model.persistence

import model.core.CoreConsts._
import model.learningPath.LearningPathTableDefs
import model.points.Points
import model._
import play.api.db.slick.HasDatabaseConfigProvider
import slick.jdbc.JdbcProfile
import slick.lifted.{ForeignKeyQuery, PrimaryKey}

import scala.concurrent.Future

// FIXME: remove this trait...

trait ExerciseTableDefs[PartType <: ExPart, ExType <: Exercise, CollType <: ExerciseCollection,
SolType, SampleSolType <: SampleSolution[SolType], UserSolType <: UserSolution[PartType, SolType], ReviewType <: ExerciseReview] extends LearningPathTableDefs {
  self: HasDatabaseConfigProvider[JdbcProfile] =>

  import profile.api._

  // Abstract types

  protected type DbExType <: ADbExercise

  protected type ExTableDef <: ExerciseInCollectionTable


  protected type CollTableDef <: ExerciseCollectionTable


  protected type DbSampleSolType <: ADbSampleSol[SolType]

  protected type DbSampleSolTable <: ASampleSolutionsTable


  protected type DbUserSolType <: ADbUserSol[SolType]

  protected type DbUserSolTable <: AUserSolutionsTable


  protected type DbReviewType <: DbExerciseReview[PartType]

  protected type ReviewsTable <: ExerciseReviewsTable

  // Table Queries

  protected val collTable: TableQuery[CollTableDef]

  protected val exTable: TableQuery[ExTableDef]

  protected val solTable: TableQuery[DbUserSolTable]

  protected val reviewsTable: TableQuery[ReviewsTable]

  // Helper methods

  protected val dbModels: ADbModels[ExType, DbExType, SampleSolType, DbSampleSolType, UserSolType, DbUserSolType]

  protected val exerciseReviewDbModels: AExerciseReviewDbModels[PartType, ReviewType, DbReviewType]

  protected def exDbValuesFromExercise(collId: Int, exercise: ExType): DbExType

  // Numbers

  def futureNumOfExes: Future[Int] = db.run(exTable.distinctOn(_.semanticVersion).length.result)

  // Reading

  def futureAllExes: Future[Seq[ExType]] = db.run(exTable.result) flatMap (exes => Future.sequence(exes map (ex => completeExForEx(collId = -1, ex))))

  def futureExerciseById(id: Int): Future[Option[ExType]] = db.run {
    exTable.filter(_.id === id).sortBy(_.semanticVersion.desc).result.headOption
  } flatMap {
    case Some(ex) => completeExForEx(collId = -1, ex) map Some.apply
    case None     => Future.successful(None)
  }

  def futureExerciseByIdAndVersion(id: Int, semVer: SemanticVersion): Future[Option[ExType]] = db.run {
    exTable.filter(e => e.id === id && e.semanticVersion === semVer).result.headOption
  } flatMap {
    case Some(ex) => completeExForEx(collId = -1, ex) map Some.apply
    case None     => Future.successful(None)
  }

  protected def completeExForEx(collId: Int, ex: DbExType): Future[ExType]

  // Saving

  def futureInsertExercise(collId: Int, compEx: ExType): Future[Boolean]

  //  = {
  //    val deleteOldExQuery = exTable.filter{
  //      dbEx: ExTableDef => dbEx.id === compEx.id && dbEx.semanticVersion === compEx.semanticVersion
  //    }.delete
  //    val insertNewExQuery = exTable += exDbValuesFromExercise(compEx)
  //
  //    db.run(deleteOldExQuery) flatMap { _ =>
  //      db.run(insertNewExQuery) flatMap {
  //        insertCount: Int => saveExerciseRest(compEx)
  //      }
  //    }
  //  }

  protected def saveExerciseRest(collId: Int, ex: ExType): Future[Boolean]

  // Implicit column types

  protected implicit val partTypeColumnType: BaseColumnType[PartType]

  protected implicit val solTypeColumnType: slick.ast.TypedType[SolType]

  protected implicit val difficultyColumnType: BaseColumnType[Difficulty] =
    MappedColumnType.base[Difficulty, String](_.entryName, Difficulties.withNameInsensitive)

  // Abstract table classes

  abstract class ExerciseCollectionTable(tag: Tag, tableName: String) extends Table[CollType](tag, tableName) {

    def id: Rep[Int] = column[Int](idName, O.PrimaryKey)

    def title: Rep[String] = column[String]("title")

    def author: Rep[String] = column[String]("author")

    def text: Rep[String] = column[String]("ex_text")

    def state: Rep[ExerciseState] = column[ExerciseState]("ex_state")

    def shortName: Rep[String] = column[String]("short_name")

  }

  abstract class ExerciseInCollectionTable(tag: Tag, name: String) extends HasBaseValuesTable[DbExType](tag, name) {

    def collectionId: Rep[Int] = column[Int]("collection_id")


    def pk: PrimaryKey = primaryKey("pk", (id, semanticVersion, collectionId))

    def scenarioFk: ForeignKeyQuery[CollTableDef, CollType] = foreignKey("scenario_fk", collectionId, collTable)(_.id)

  }

  abstract class ExForeignKeyTable[T](tag: Tag, tableName: String) extends Table[T](tag, tableName) {

    def exerciseId: Rep[Int] = column[Int]("exercise_id")

    def exSemVer: Rep[SemanticVersion] = column[SemanticVersion]("ex_sem_ver")

    def collectionId: Rep[Int] = column[Int]("collection_id")


    def exerciseFk: ForeignKeyQuery[ExTableDef, DbExType] = foreignKey("exercise_fk", (exerciseId, exSemVer, collectionId), exTable)(ex => (ex.id, ex.semanticVersion, ex.collectionId))

  }

  protected abstract class ASolutionsTable[S <: ADbSolution[SolType]](tag: Tag, name: String) extends ExForeignKeyTable[S](tag, name) {

  }

  protected abstract class ASampleSolutionsTable(tag: Tag, name: String) extends ASolutionsTable[DbSampleSolType](tag, name) {

    def id: Rep[Int] = column[Int]("id", O.PrimaryKey)

    def sample: Rep[SolType] = column[SolType](sampleName)(solTypeColumnType)

  }

  protected abstract class AUserSolutionsTable(tag: Tag, name: String) extends ASolutionsTable[DbUserSolType](tag, name) {

    def id: Rep[Int] = column[Int]("id", O.PrimaryKey, O.AutoInc)

    def username: Rep[String] = column[String]("username")

    def part: Rep[PartType] = column[PartType]("part")(partTypeColumnType)

    def points: Rep[Points] = column[Points](pointsName)

    def maxPoints: Rep[Points] = column[Points]("max_points")

    def solution: Rep[SolType] = column[SolType](solutionName)(solTypeColumnType)

    def userFk: ForeignKeyQuery[UsersTable, User] = foreignKey("user_fk", username, users)(_.username)

  }

  abstract class ExerciseReviewsTable(tag: Tag, tableName: String) extends ExForeignKeyTable[DbReviewType](tag, tableName) {

    def username: Rep[String] = column[String]("username")

    def exercisePart: Rep[PartType] = column[PartType](partName)

    def difficulty: Rep[Difficulty] = column[Difficulty](difficultyName)

    def maybeDuration: Rep[Int] = column[Int]("maybe_duration")


    def pk: PrimaryKey = primaryKey("pk", (exerciseId, collectionId, exercisePart))

  }


}
