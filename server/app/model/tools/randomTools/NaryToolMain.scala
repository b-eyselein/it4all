package model.tools.randomTools

import javax.inject.{Inject, Singleton}
import model.learningPath.LearningPathTableDefs
import model.toolMains.{RandomExerciseToolMain, ToolState}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import scala.concurrent.ExecutionContext
import scala.language.implicitConversions


class NaryTableDefs @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(override implicit val executionContext: ExecutionContext)
  extends HasDatabaseConfigProvider[JdbcProfile] with LearningPathTableDefs

@Singleton
class NaryToolMain @Inject()(val tables: NaryTableDefs)(implicit ec: ExecutionContext)
  extends RandomExerciseToolMain("Zahlensysteme", "nary") {

  override type Tables = NaryTableDefs

  // Other members

  override val toolState: ToolState = ToolState.LIVE

}
