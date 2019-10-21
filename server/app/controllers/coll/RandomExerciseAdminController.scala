package controllers.coll

import javax.inject.{Inject, Singleton}
import model.core.Repository
import model.toolMains.{RandomExerciseToolMain, ToolList}
import play.api.db.slick.DatabaseConfigProvider
import play.api.mvc.ControllerComponents

import scala.concurrent.ExecutionContext


@Singleton
class RandomExerciseAdminController @Inject()(cc: ControllerComponents, dbcp: DatabaseConfigProvider, tl: ToolList, val repository: Repository)(implicit ec: ExecutionContext)
  extends AToolAdminController(cc, dbcp, tl) {

  override protected type ToolMainType = RandomExerciseToolMain

  override protected def getToolMain(toolType: String): Option[RandomExerciseToolMain] = toolList.getRandomExToolMainOption(toolType)

}
