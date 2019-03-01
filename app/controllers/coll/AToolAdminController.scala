package controllers.coll

import controllers.AExerciseController
import model.core.Repository
import model.learningPath.LearningPath
import model.toolMains.ToolList
import play.api.db.slick.DatabaseConfigProvider
import play.api.mvc.{ControllerComponents, EssentialAction}

import scala.concurrent.ExecutionContext

abstract class AToolAdminController(cc: ControllerComponents, dbcp: DatabaseConfigProvider, tl: ToolList, repository: Repository)(implicit ec: ExecutionContext)
  extends AExerciseController(cc, dbcp, tl) {

  override protected val adminRightsRequired: Boolean = true

  def adminIndex(toolType: String): EssentialAction = futureWithUserWithToolMain(toolType) { (admin, toolMain) =>
    implicit request => toolMain.adminIndexView(admin, toolList) map (html => Ok(html))
  }

  def readLearningPaths(toolType: String): EssentialAction = futureWithUserWithToolMain(toolType) { (admin, toolMain) =>
    implicit request =>
      val readLearningPaths: Seq[LearningPath] = toolMain.readLearningPaths

      toolMain.futureSaveLearningPaths(readLearningPaths) map {
        _ => Ok(views.html.admin.learningPathRead(admin, readLearningPaths, toolMain))
      }
  }

}
