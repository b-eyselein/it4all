package model.tools.randomTools

import javax.inject.{Inject, Singleton}
import model.learningPath.LearningPathTableDefs
import model.tools.{ToolConsts, ToolState}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import scala.concurrent.ExecutionContext

object NaryConsts extends ToolConsts {

  override val toolName: String = "Zahlensysteme"
  override val toolId  : String = "nary"

  override val toolState: ToolState = ToolState.LIVE

}


class NaryTableDefs @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(override implicit val executionContext: ExecutionContext)
  extends HasDatabaseConfigProvider[JdbcProfile] with LearningPathTableDefs


@Singleton
class NaryToolMain @Inject()(val tables: NaryTableDefs)(implicit ec: ExecutionContext)
  extends RandomExerciseToolMain(NaryConsts) {

  override type Tables = NaryTableDefs

}
