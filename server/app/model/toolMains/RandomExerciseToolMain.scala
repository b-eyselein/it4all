package model.toolMains

import model.core.result.EvaluationResult
import play.api.mvc.Call

import scala.concurrent.ExecutionContext

abstract class RandomExerciseToolMain(tn: String, up: String)(implicit ec: ExecutionContext) extends AToolMain(tn, up) {

  override type ResultType = EvaluationResult

  // Calls

  override def indexCall: Call = ??? // controllers.coll.routes.RandomExerciseAdminController.adminIndex(this.urlPart)

}
