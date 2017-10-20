package controllers


import controllers.core.BaseController
import model.feedback.Feedback.EvaluatedTool
import model.feedback.{EvaluatedAspect, Feedback, FeedbackKey, Mark}
import model.user.User
import play.api.mvc.{AnyContent, ControllerComponents, Request}
import play.mvc.Security

@Security.Authenticated(classOf[model.Secured])
class EvaluationController(cc: ControllerComponents) extends BaseController(cc) {


  def index = Action { implicit request =>
    val user = getUser
    val toEvaluate: List[Feedback] = Feedback.EvaluatedTool.values.map(tool => {
      val key = new FeedbackKey(user.name, tool)
      Option(Feedback.finder.byId(key)).getOrElse(new Feedback(key))
    }).toList

    Ok(views.html.evaluation.eval.render(user, toEvaluate))
  }

  def submit = Action { implicit request =>
    val user = getUser

    val evaluation: List[Feedback] = EvaluatedTool.values.map(tool => readFeedback(user, tool)).toList

    evaluation.foreach(_.save)

    Ok(views.html.evaluation.submit.render(user, evaluation))
  }

  def readFeedback(user: User, tool: EvaluatedTool)(implicit request: Request[AnyContent]): Feedback = {
    val key = new FeedbackKey(user.name, tool)
    val feedback = Option(Feedback.finder.byId(key)).getOrElse(new Feedback(key))

    val evaluatedTool = tool.toString.toLowerCase

    for (evaledAspect <- EvaluatedAspect.values) {
      val markStr = singleStrForm(evaledAspect.toString.toLowerCase + "-" + evaluatedTool.toLowerCase).bindFromRequest.get.str
      feedback.set(evaledAspect, Mark.valueOf(markStr.toUpperCase))
    }

    feedback.comment = singleStrForm("comment-" + evaluatedTool).bindFromRequest.get.str

    feedback
  }

}
