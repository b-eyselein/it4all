package controllers

import model.toolMains.{FixedExToolMain, ToolList}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.mvc._
import slick.jdbc.JdbcProfile

import scala.concurrent.ExecutionContext

abstract class AFixedExController(cc: ControllerComponents, dbcp: DatabaseConfigProvider, tl: ToolList)(implicit ec: ExecutionContext)
  extends AExerciseController(cc, dbcp, tl) with HasDatabaseConfigProvider[JdbcProfile] with Secured {

  override protected type ToolMainType <: FixedExToolMain

  protected def onNoSuchCollection(collId: Int): Result = NotFound(s"There is no collection with id '$collId'")

}
