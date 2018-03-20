package controllers

import model.core.{ExerciseFormMappings, FileUtils}
import model.toolMains.FixedExToolMain
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.mvc._
import slick.jdbc.JdbcProfile

import scala.concurrent.ExecutionContext

abstract class AFixedExController(cc: ControllerComponents, dbcp: DatabaseConfigProvider)(implicit ec: ExecutionContext)
  extends AExerciseController(cc, dbcp) with HasDatabaseConfigProvider[JdbcProfile] with Secured with FileUtils with ExerciseFormMappings {

  override protected type ToolMainType <: FixedExToolMain

}
