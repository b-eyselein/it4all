package model.persistence

import model.Enums.ExerciseState
import model.{CompleteEx, Exercise}
import play.api.Logger
import play.api.db.slick.HasDatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

trait IdExerciseTableDefs[Ex <: Exercise, CompEx <: CompleteEx[Ex]] extends ExerciseTableDefs[Ex, CompEx] {
  self: HasDatabaseConfigProvider[JdbcProfile] =>

  import profile.api._

  // Numbers

  def futureHighestExerciseId(implicit ec: ExecutionContext): Future[Int] = db.run(exTable.map(_.id).max.result) map (_ getOrElse (-1))

  // Update

  // Update
  def updateExerciseState(id: Int, newState: ExerciseState)(implicit ec: ExecutionContext): Future[Boolean] = db.run((for {
    ex <- exTable if ex.id === id
  } yield ex.state).update(newState)) map (_ => true) recover {
    case e: Throwable =>
      Logger.error(s"Could not update state of exercise $id", e)
      false
  }

  // Deletion

  def deleteExercise(id: Int): Future[Int] = db.run(exTable.filter(_.id === id).delete)

}