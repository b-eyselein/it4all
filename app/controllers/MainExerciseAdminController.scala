package controllers

import javax.inject.{Inject, Singleton}
import model.core.Repository
import model.learningPath.LearningPath
import model.toolMains.{AToolMain, ToolList}
import play.api.db.slick.DatabaseConfigProvider
import play.api.mvc.{ControllerComponents, EssentialAction}

import scala.concurrent.ExecutionContext

@Singleton
class MainExerciseAdminController @Inject()(cc: ControllerComponents, dbcp: DatabaseConfigProvider, tl: ToolList, val repository: Repository)(implicit ec: ExecutionContext)
  extends AExerciseController(cc, dbcp, tl) {

  override protected type ToolMainType = AToolMain

  override protected def getToolMain(toolType: String): Option[AToolMain] = toolList.toolMains.find(_.urlPart == toolType)

  override protected val adminRightsRequired: Boolean = true

  // Admin

//  def adminIndex(toolType: String): EssentialAction = futureWithUserWithToolMain(toolType) { (admin, toolMain) =>
//    implicit request => toolMain.adminIndexView(admin, toolList) map (html => Ok(html))
//  }


}
