package model.persistence

import model.Enums.ExerciseState
import model.learningPath.LearningPathTableDefs
import model.toolMains.ToolList.STEP
import model.{CompleteEx, Exercise}
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

  def futureCompleteExesForPage(page: Int): Future[Seq[CompEx]] =
    db.run(exTable.filter(_.state === ExerciseState.APPROVED).result) flatMap { allExes =>
      val (sliceStart, sliceEnd) = (Math.max(0, (page - 1) * STEP), Math.min(page * STEP, allExes.size))
      Future.sequence(allExes slice(sliceStart, sliceEnd) map completeExForEx)
    }

  def futureCompleteExById(id: Int): Future[Option[CompEx]] = db.run(exTable.filter(_.id === id).result.headOption) flatMap {
    case Some(ex) => completeExForEx(ex) map Some.apply
    case None     => Future(None)
  }

  protected def completeExForEx(ex: Ex): Future[CompEx]

  // Saving

  def saveCompleteEx(compEx: CompEx): Future[Boolean] = db.run(exTable.filter(_.id === compEx.ex.id).delete) flatMap { _ =>
    db.run(exTable += compEx.ex) flatMap { _ => saveExerciseRest(compEx) } recover { case e: Throwable =>
      Logger.error("Could not save exercise", e)
      false
    }
  }

  protected def saveExerciseRest(compEx: CompEx): Future[Boolean]

  // Update

  def futureUpdateExercise(ex: Ex): Future[Boolean] = db.run(exTable insertOrUpdate ex) map (_ => true) recover { case e: Throwable =>
    Logger.error(s"Could not update exercise ${ex.id}", e)
    false
  }
}