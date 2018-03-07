package controllers

import javax.inject._

import model.Enums.EvaluatedAspect
import model.User
import model.core.Repository
import model.feedback._
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.mvc._
import slick.jdbc.JdbcProfile

import scala.concurrent.ExecutionContext

class EvaluationController @Inject()(cc: ControllerComponents, val dbConfigProvider: DatabaseConfigProvider, val repository: Repository)
                                    (implicit ec: ExecutionContext)
  extends AbstractController(cc) with HasDatabaseConfigProvider[JdbcProfile] with Secured {

  def index: EssentialAction = withUser { user =>
    implicit request =>
      //      val toEvaluate: Seq[Feedback] = Feedback.EvaluatedTool.values.map(tool => {
      //        val key = new FeedbackKey(user.name, tool)
      //        Option(Feedback.finder.byId(key)).getOrElse(new Feedback(key))
      //      }).toList
      val toEvaluate: Seq[Feedback] = Seq.empty
      Ok(views.html.evaluation.eval(user, toEvaluate))
  }

  def submit: EssentialAction = withUser { user =>
    implicit request =>
      val evaluation: Seq[Feedback] = EvaluatedTool.values map (tool => readFeedback(user, tool))

      //      evaluation.foreach(_.save)

      Ok(views.html.evaluation.submit(user, evaluation))
  }

  def readFeedback(user: User, tool: EvaluatedTool)(implicit request: Request[AnyContent]): Feedback = {
    //    val key = new FeedbackKey(user.name, tool)
    //    val feedback = Option(Feedback.finder.byId(key)).getOrElse(new Feedback(key))

    val evaluatedTool = tool.toString.toLowerCase

    for (evaledAspect <- EvaluatedAspect.values) {
      //      val markStr = singleStrForm(evaledAspect.toString.toLowerCase + "-" + evaluatedTool.toLowerCase).bindFromRequest.get.str
      //      feedback.set(evaledAspect, Mark.valueOf(markStr.toUpperCase))
    }

    //    feedback.comment = singleStrForm("comment-" + evaluatedTool).bindFromRequest.get.str

    //    feedback
    null
  }

}
