package model.persistence

import model.{CompleteEx, Difficulties, Difficulty, ExPart, Exercise, ExerciseReview, ExerciseState, SemanticVersion}
import play.api.Logger
import play.api.db.slick.HasDatabaseConfigProvider
import slick.jdbc.JdbcProfile
import slick.lifted.{ForeignKeyQuery, PrimaryKey}

import scala.concurrent.Future

trait IdExerciseTableDefs[Ex <: Exercise, CompEx <: CompleteEx[Ex], PartType <: ExPart, ReviewType <: ExerciseReview[PartType]]
  extends ExerciseTableDefs[Ex, CompEx] {
  self: HasDatabaseConfigProvider[JdbcProfile] =>

  import profile.api._

  protected type ReviewsTableDef <: ExerciseReviewsTable

  protected val reviewsTable: TableQuery[ReviewsTableDef]

  // Numbers

  def futureHighestExerciseId: Future[Int] = db.run(exTable.map(_.id).max.result) map (_ getOrElse (-1))

  // Reading

  def futureAllReviews: Future[Seq[ReviewType]] = db.run(reviewsTable.result)

  def futureReviewsForExercise(id: Int): Future[Seq[ReviewType]] = db.run(reviewsTable.filter(_.exerciseId === id).result)

  // Update

  def futureSaveReview(review: ReviewType): Future[Boolean] = db.run(reviewsTable insertOrUpdate review) map (_ => true) recover {
    case e: Throwable =>
      Logger.error("Error while saving review", e)
      false
  }

  def updateExerciseState(id: Int, newState: ExerciseState): Future[Boolean] = db.run((for {
    ex <- exTable if ex.id === id
  } yield ex.state).update(newState)) map (_ => true) recover {
    case e: Throwable =>
      Logger.error(s"Could not update state of exercise $id", e)
      false
  }

  // Deletion

  def deleteExercise(id: Int): Future[Int] = db.run(exTable.filter(_.id === id).delete)

  // Implicit column types

  protected implicit val partTypeColumnType: BaseColumnType[PartType]

  protected implicit val difficultyColumnType: BaseColumnType[Difficulty] =
    MappedColumnType.base[Difficulty, String](_.entryName, Difficulties.withNameInsensitive)

  // Table definitions

  abstract class ExerciseTableDef(tag: Tag, tableName: String) extends HasBaseValuesTable[ExDbValues](tag, tableName) {

    def pk: PrimaryKey = primaryKey("pk", (id, semanticVersion))

  }

  abstract class ExerciseReviewsTable(tag: Tag, tableName: String) extends Table[ReviewType](tag, tableName) {

    def username: Rep[String] = column[String]("username")

    def exerciseId: Rep[Int] = column[Int]("exercise_id")

    def exerciseSemVer: Rep[SemanticVersion] = column[SemanticVersion]("ex_sem_ver")

    def exercisePart: Rep[PartType] = column[PartType]("exercise_part")

    def difficulty: Rep[Difficulty] = column[Difficulty]("difficulty")

    def maybeDuration: Rep[Int] = column[Int]("maybe_duration")


    def pk: PrimaryKey = primaryKey("pk", (exerciseId, exercisePart))

    def exerciseFk: ForeignKeyQuery[ExTableDef, ExDbValues] = foreignKey("exercise_fk", exerciseId, exTable)(_.id)

  }

}
