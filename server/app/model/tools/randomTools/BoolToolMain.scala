package model.tools.randomTools

import javax.inject.{Inject, Singleton}
import model.learningPath.LearningPathTableDefs
import model.tools.{ToolConsts, ToolState}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import scala.concurrent.ExecutionContext

object BoolConsts extends ToolConsts {

  override val toolName: String = "Boolesche Algebra"
  override val toolId  : String = "bool"

  override val toolState: ToolState = ToolState.LIVE

}


class BoolTableDefs @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(override implicit val executionContext: ExecutionContext)
  extends HasDatabaseConfigProvider[JdbcProfile] with LearningPathTableDefs


@Singleton
class BoolToolMain @Inject()(val tables: BoolTableDefs)(implicit ec: ExecutionContext)
  extends RandomExerciseToolMain(BoolConsts) {

  override type Tables = BoolTableDefs

}
