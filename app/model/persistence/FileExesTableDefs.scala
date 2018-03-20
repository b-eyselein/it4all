package model.persistence

import model.{CompleteEx, Exercise}
import play.api.db.slick.HasDatabaseConfigProvider
import slick.jdbc.JdbcProfile

trait FileExesTableDefs[Ex <: Exercise, CompExType <: CompleteEx[Ex]] extends IdExerciseTableDefs[Ex, CompExType] {
  self: HasDatabaseConfigProvider[JdbcProfile] =>

}
