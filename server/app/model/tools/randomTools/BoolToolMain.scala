package model.tools.randomTools

import javax.inject.{Inject, Singleton}
import model.learningPath.LearningPathTableDefs
import model.toolMains.{RandomExerciseToolMain, ToolState}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import scala.concurrent.ExecutionContext


class BoolTableDefs @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(override implicit val executionContext: ExecutionContext)
  extends HasDatabaseConfigProvider[JdbcProfile] with LearningPathTableDefs


@Singleton
class BoolToolMain @Inject()(val tables: BoolTableDefs)(implicit ec: ExecutionContext)
  extends RandomExerciseToolMain("Boolesche Algebra", "bool") {

  override type Tables = BoolTableDefs

  // Other members

  override val toolState: ToolState = ToolState.LIVE

  override val hasPlayground: Boolean = true

}
