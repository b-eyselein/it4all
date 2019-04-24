package controllers.coll

import controllers.AExerciseController
import model.core.{CommonUtils, ReadAndSaveResult, ReadAndSaveSuccess}
import model.learningPath.LearningPath
import model.toolMains.ToolList
import play.api.Logger
import play.api.db.slick.DatabaseConfigProvider
import play.api.mvc.{ControllerComponents, EssentialAction, Result}
import play.twirl.api.Html

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

abstract class AToolAdminController(cc: ControllerComponents, dbcp: DatabaseConfigProvider, tl: ToolList)(implicit ec: ExecutionContext)
  extends AExerciseController(cc, dbcp, tl) {

  private val logger = Logger(classOf[AToolAdminController])

  override protected val adminRightsRequired: Boolean = true

  // Routes

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

  // Helper methods

  protected def readSaveAndPreview[ReadType](readFunc: => Seq[Try[ReadType]], saveFunc: ReadType => Future[Boolean],
                                             previewFunc: ReadAndSaveResult[ReadType] => Html): Future[Result] = {

    val readTries: Seq[Try[ReadType]] = readFunc

    val (readSuccesses, readFailures) = CommonUtils.splitTriesNew(readTries)

    val readAndSaveSuccesses: Future[Seq[(ReadType, Boolean)]] = Future.sequence(readSuccesses.map { readCollection =>
      saveFunc(readCollection) map (saved => (readCollection, saved))
    })

    readAndSaveSuccesses.map { saveResults: Seq[(ReadType, Boolean)] =>
      val readAndSaveResult = ReadAndSaveResult(saveResults map {
        sr => new ReadAndSaveSuccess[ReadType](sr._1, sr._2)
      }, readFailures)

      for (failure <- readAndSaveResult.failures) {
        logger.error("There has been an error reading a yaml object: ", failure.exception)
      }

      Ok(previewFunc(readAndSaveResult))
    }
  }

}

