package model.toolMains

import model.User
import model.core.result.EvaluationResult
import play.api.mvc.Call
import play.twirl.api.Html

import scala.concurrent.{ExecutionContext, Future}

abstract class RandomExerciseToolMain(tn: String, up: String)(implicit ec: ExecutionContext) extends AToolMain(tn, up) {

  override type ResultType = EvaluationResult

  // Views

  override def adminIndexView(admin: User, toolList: ToolList): Future[Html] = Future.successful(Html(""))

  // Calls

  override def indexCall: Call = ??? // controllers.coll.routes.RandomExerciseAdminController.adminIndex(this.urlPart)

}
