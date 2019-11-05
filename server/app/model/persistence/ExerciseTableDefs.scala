package model.persistence

import model._
import model.core.CoreConsts._
import model.learningPath.LearningPathTableDefs
import model.points.Points
import play.api.db.slick.HasDatabaseConfigProvider
import slick.jdbc.JdbcProfile
import slick.lifted.{ForeignKeyQuery, PrimaryKey, ProvenShape}

import scala.concurrent.Future

trait ExerciseTableDefs[PartType <: ExPart, ExType <: Exercise, SolType, SampleSolType <: SampleSolution[SolType], UserSolType <: UserSolution[PartType, SolType], ReviewType <: ExerciseReview]
  extends LearningPathTableDefs
    with ExerciseTableDefQueries[PartType, ExType, SolType, SampleSolType, UserSolType, ReviewType] {
  self: HasDatabaseConfigProvider[JdbcProfile] =>

  import profile.api._

  // Abstract types

  protected type DbExType <: ADbExercise

  protected type ExTableDef <: ExerciseInCollectionTable


  protected type CollTableDef <: ExerciseCollectionsTable


  protected type DbSampleSolType <: ADbSampleSol

  protected type DbSampleSolTable <: ASampleSolutionsTable


  protected type DbUserSolType <: ADbUserSol[PartType]

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
  protected val exerciseReviewDbModels: AExerciseReviewDbModels[PartType, ReviewType, DbReviewType]

  protected final def exDbValuesFromExercise(exercise: ExType): DbExType = dbModels.dbExerciseFromExercise(exercise)

  // Reading

  protected def completeExForEx(collId: Int, ex: DbExType): Future[ExType]

  // Saving

  protected def saveExerciseRest(collId: Int, ex: ExType): Future[Boolean]

  // Implicit column types

  protected implicit val partTypeColumnType: BaseColumnType[PartType]

  protected implicit val difficultyColumnType: BaseColumnType[Difficulty] =
    MappedColumnType.base[Difficulty, String](_.entryName, Difficulties.withNameInsensitive)

  // Abstract table classes

  abstract class ExerciseCollectionsTable(tag: Tag, tableName: String) extends Table[ExerciseCollection](tag, tableName) {

    def id: Rep[Int] = column[Int](idName, O.PrimaryKey)

    def toolId: Rep[String] = column[String]("tool_id")

    def title: Rep[String] = column[String]("title")

    def author: Rep[String] = column[String]("author")

    def text: Rep[String] = column[String]("ex_text")

    def state: Rep[ExerciseState] = column[ExerciseState]("ex_state")

    def shortName: Rep[String] = column[String]("short_name")


    override final def * : ProvenShape[ExerciseCollection] = (id, toolId, title, author, text, state, shortName) <> (ExerciseCollection.tupled, ExerciseCollection.unapply)

  }

  abstract class ExerciseInCollectionTable(tag: Tag, name: String) extends Table[DbExType](tag, name) {

    def id: Rep[Int] = column[Int](idName)

    def collectionId: Rep[Int] = column[Int]("collection_id")

    def title: Rep[String] = column[String]("title")

    def author: Rep[String] = column[String]("author")

    def text: Rep[String] = column[String]("ex_text")

    def state: Rep[ExerciseState] = column[ExerciseState]("ex_state")

    def semanticVersion: Rep[SemanticVersion] = column[SemanticVersion]("semantic_version")


    def pk: PrimaryKey = primaryKey("pk", (id, semanticVersion, collectionId))

    def scenarioFk: ForeignKeyQuery[CollTableDef, ExerciseCollection] = foreignKey("scenario_fk", collectionId, collTable)(_.id)

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

    def id: Rep[Int] = column[Int]("id")

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


  // ExerciseFilesTable

  trait ExerciseFilesTable[EF] extends Table[EF] {

    def name: Rep[String] = column[String](nameName)

    def content: Rep[String] = column[String](contentName)

    def fileType: Rep[String] = column[String]("file_type")

    def editable: Rep[Boolean] = column[Boolean]("editable")

  }

}
