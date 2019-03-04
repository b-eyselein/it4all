package model.persistence

import model._
import model.core.CoreConsts._
import model.learningPath.LearningPathTableDefs
import model.points.Points
import play.api.db.slick.HasDatabaseConfigProvider
import slick.jdbc.JdbcProfile
import slick.lifted.{ForeignKeyQuery, PrimaryKey}

import scala.concurrent.Future

trait ExerciseTableDefs[PartType <: ExPart, ExType <: Exercise, CollType <: ExerciseCollection, SolType, SampleSolType <: SampleSolution[SolType], UserSolType <: UserSolution[PartType, SolType], ReviewType <: ExerciseReview]
  extends LearningPathTableDefs
    with ExerciseTableDefQueries[PartType, ExType, CollType, SolType, SampleSolType, UserSolType, ReviewType] {
  self: HasDatabaseConfigProvider[JdbcProfile] =>

  import profile.api._

  // Abstract types

  protected type DbExType <: ADbExercise

  protected type ExTableDef <: ExerciseInCollectionTable


  protected type CollTableDef <: ExerciseCollectionTable


  protected type DbSampleSolType <: ADbSampleSol[SolType]

  protected type DbSampleSolTable <: ASampleSolutionsTable


  protected type DbUserSolType <: ADbUserSol[PartType, SolType]

  protected type DbUserSolTable <: AUserSolutionsTable


  protected type DbReviewType <: DbExerciseReview[PartType]

  protected type ReviewsTable <: ExerciseReviewsTable

  // Table Queries

  protected val collTable: TableQuery[CollTableDef]
  protected val exTable  : TableQuery[ExTableDef]

  protected val sampleSolutionsTableQuery: TableQuery[DbSampleSolTable]
  protected val userSolutionsTableQuery  : TableQuery[DbUserSolTable]

  protected val reviewsTable: TableQuery[ReviewsTable]

  // Helper methods

  protected val dbModels              : ADbModels[ExType, DbExType]
  protected val solutionDbModels      : ASolutionDbModels[SolType, PartType, SampleSolType, DbSampleSolType, UserSolType, DbUserSolType]
  protected val exerciseReviewDbModels: AExerciseReviewDbModels[PartType, ReviewType, DbReviewType]

  protected def exDbValuesFromExercise(collId: Int, exercise: ExType): DbExType

  // Reading

  protected def completeExForEx(collId: Int, ex: DbExType): Future[ExType]

  // Saving

  protected def saveExerciseRest(collId: Int, ex: ExType): Future[Boolean]

  // Implicit column types

  protected implicit val partTypeColumnType: BaseColumnType[PartType]

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


  protected abstract class ASampleSolutionsTable(tag: Tag, name: String) extends ExForeignKeyTable[DbSampleSolType](tag, name) {

    def id: Rep[Int] = column[Int]("id", O.PrimaryKey)

  }

  protected abstract class AUserSolutionsTable(tag: Tag, name: String) extends ExForeignKeyTable[DbUserSolType](tag, name) {

    def id: Rep[Int] = column[Int]("id", O.PrimaryKey, O.AutoInc)

    def username: Rep[String] = column[String]("username")

    def part: Rep[PartType] = column[PartType]("part")(partTypeColumnType)

    def points: Rep[Points] = column[Points](pointsName)

    def maxPoints: Rep[Points] = column[Points]("max_points")


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
