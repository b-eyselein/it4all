package controllers

import javax.inject.{Inject, Singleton}
import model.core.Repository
import model.feedback.{Feedback, FeedbackFormHelper}
import model.learningPath.LearningPath
import model.toolMains.{AToolMain, ToolList}
import play.api.data.Form
import play.api.db.slick.DatabaseConfigProvider
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class MainExerciseController @Inject()(cc: ControllerComponents, dbcp: DatabaseConfigProvider, tl: ToolList, val repository: Repository)(implicit ec: ExecutionContext)
  extends AExerciseController(cc, dbcp, tl) {

  override protected type ToolMainType = AToolMain

  override protected def getToolMain(toolType: String): Option[AToolMain] = toolList.toolMains.find(_.urlPart == toolType)

  def index(toolType: String): EssentialAction = futureWithUserWithToolMain(toolType) { (user, toolMain) =>
    implicit request => toolMain.futureLearningPaths map (paths => Ok(views.html.exercises.exerciseIndex(user, toolMain, paths)))
  }

  // Evaluation

  def evaluate(toolType: String): EssentialAction = futureWithUserWithToolMain(toolType) { (user, toolMain) =>
    implicit request =>
      repository.futureMaybeFeedback(user, toolType) map { maybeFeedback =>
        Ok(views.html.evaluation.eval(user, maybeFeedback getOrElse Feedback(user.username, toolMain.urlPart), toolMain))
      }
  }

  def submitEvaluation(toolType: String): EssentialAction = futureWithUserWithToolMain(toolType) { (user, toolMain) =>
    implicit request =>

      val onError: Form[Feedback] => Future[Result] = { _ => Future.successful(BadRequest("TODO!")) }

      val onRead: Feedback => Future[Result] = { feedback =>
        repository.saveFeedback(feedback) map {
          feedbackSaved => Ok(views.html.evaluation.submitEvaluation(user, feedback, feedbackSaved, toolMain))
        }
      }

      FeedbackFormHelper(user.username, toolMain.urlPart).feedbackFormMapping.bindFromRequest().fold(onError, onRead)
  }

  // Admin

  def adminIndex(toolType: String): EssentialAction = futureWithAdminWithToolMain(toolType) { (admin, toolMain) =>
    implicit request => toolMain.adminIndexView(admin, toolList) map (html => Ok(html))
  }

  def readLearningPaths(toolType: String): EssentialAction = futureWithUserWithToolMain(toolType) { (user, toolMain) =>
    implicit request =>
      val readLearningPaths: Seq[LearningPath] = toolMain.readLearningPaths

      toolMain.futureSaveLearningPaths(readLearningPaths) map {
        _ => Ok(views.html.admin.learningPathRead(user, readLearningPaths, toolMain))
      }
  }

  def learningPath(toolType: String, id: Int): EssentialAction = futureWithUserWithToolMain(toolType) { (user, toolMain) =>
    implicit request =>
      toolMain.futureLearningPathById(id) map {
        case None     => BadRequest("No such learning path!")
        case Some(lp) => Ok(views.html.learningPath.learningPath(user, lp, toolMain))
      }
  }

  // User

  def playground(toolType: String): EssentialAction = withUserWithToolMain(toolType) { (user, toolMain) =>
    implicit request =>
      if (toolMain.hasPlayground) {
        Ok(toolMain.playground(user))
      } else {
        Redirect(toolMain.indexCall)
      }
  }

}
