package controllers

import javax.inject.{Inject, Singleton}
import model.core.Repository
import model.feedback.{Feedback, FeedbackFormHelper}
import model.toolMains.{AToolMain, ToolList}
import play.api.data.Form
import play.api.db.slick.DatabaseConfigProvider
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class MainExerciseController @Inject()(cc: ControllerComponents, dbcp: DatabaseConfigProvider, tl: ToolList, val repository: Repository)(implicit ec: ExecutionContext)
  extends AExerciseController(cc, dbcp, tl) with play.api.i18n.I18nSupport {

  override protected type ToolMainType = AToolMain

  override protected def getToolMain(toolType: String): Option[AToolMain] = toolList.toolMains.find(_.urlPart == toolType)

  override protected val adminRightsRequired: Boolean = false

  // Evaluation

  def evaluate(toolType: String): EssentialAction = futureWithUserWithToolMain(toolType) { (user, toolMain) =>
    implicit request =>
      repository.futureMaybeFeedback(user, toolType) map { maybeFeedback =>

        val feedbackForm = maybeFeedback match {
          case None     => FeedbackFormHelper.feedbackFormMapping
          case Some(fb) => FeedbackFormHelper.feedbackFormMapping.fill(fb)
        }

        Ok(views.html.evaluation.eval(user, feedbackForm, maybeFeedback.getOrElse(Feedback()), toolMain))
      }
  }

  def submitEvaluation(toolType: String): EssentialAction = futureWithUserWithToolMain(toolType) { (user, toolMain) =>
    implicit request =>

      val onError: Form[Feedback] => Future[Result] = { formWithErrors =>
        formWithErrors.errors.foreach(println)
        Future.successful(BadRequest(views.html.evaluation.eval(user, formWithErrors, Feedback(), toolMain)))
      }

      val onRead: Feedback => Future[Result] = { feedback =>
        repository.futureUpsertFeedback(user.username, toolMain.urlPart, feedback) map {
          feedbackSaved => Ok(views.html.evaluation.submitEvaluation(user, feedback, feedbackSaved, toolMain))
        }
      }

      FeedbackFormHelper.feedbackFormMapping.bindFromRequest().fold(onError, onRead)
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