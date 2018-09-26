package model.persistence

import model.ExerciseState.APPROVED
import model.core.CoreConsts.STEP
import model.learningPath.LearningPathTableDefs
import model.{CompleteEx, Exercise, SemanticVersion}
import play.api.Logger
import play.api.db.slick.HasDatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.Future

trait ExerciseTableDefs[Ex <: Exercise, CompEx <: CompleteEx[Ex]] extends LearningPathTableDefs {
  self: HasDatabaseConfigProvider[JdbcProfile] =>

  import profile.api._

  protected type ExTableDef <: HasBaseValuesTable[Ex]

  protected val exTable: TableQuery[ExTableDef]

  // Numbers

  def futureNumOfExes: Future[Int] = db.run(exTable.length.result)

  // Reading

  def futureCompleteExes: Future[Seq[CompEx]] = db.run(exTable.result) flatMap (exes => Future.sequence(exes map completeExForEx))

  def futureCompleteExesForPage(page: Int): Future[Seq[CompEx]] = db.run(exTable.result) flatMap { allExes =>
    val approvedExes = allExes.filter(_.state == APPROVED)

    val (sliceStart, sliceEnd) = (Math.max(0, (page - 1) * STEP), Math.min(page * STEP, approvedExes.size))
    Future.sequence(approvedExes slice(sliceStart, sliceEnd) map completeExForEx)
  }

  def futureCompleteExById(id: Int): Future[Option[CompEx]] = db.run(exTable.filter(_.id === id).result.headOption) flatMap {
    case Some(ex) => completeExForEx(ex) map Some.apply
    case None     => Future(None)
  }

  protected def completeExForEx(ex: Ex): Future[CompEx]

  // Saving

  def futureSaveCompleteEx(compEx: CompEx): Future[Boolean] = db.run(exTable.filter(_.id === compEx.ex.id).delete) flatMap { _ =>
    db.run(exTable += compEx.ex) flatMap { _ => saveExerciseRest(compEx) } recover { case e: Throwable =>
      Logger.error("Could not save exercise", e)
      false
    }
  }

  def futureInsertCompleteEx(compEx: CompEx): Future[Boolean] = db.run(exTable += compEx.ex) flatMap {
    insertCount => saveExerciseRest(compEx)
  }

  protected def saveExerciseRest(compEx: CompEx): Future[Boolean]

  // Update

  def futureUpdateExercise(ex: Ex): Future[Boolean] = db.run(exTable insertOrUpdate ex) map (_ => true) recover { case e: Throwable =>
    Logger.error(s"Could not update exercise ${ex.id}", e)
    false
  }

  // Abstract table classes

  abstract class ExForeignKeyTable[T](tag: Tag, tableName: String) extends Table[T](tag, tableName) {

    def exerciseId = column[Int]("exercise_id")

    def exSemVer = column[SemanticVersion]("ex_sem_ver")


    def exerciseFk = foreignKey("exercise_fk", (exerciseId, exSemVer), exTable)(ex => (ex.id, ex.semanticVersion))

  }

}