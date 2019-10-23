package model.toolMains

import model.{ExPart, User}
import play.api.mvc.Call
import play.twirl.api.Html

import scala.concurrent.{ExecutionContext, Future}

abstract class RandomExerciseToolMain(tn: String, up: String)(implicit ec: ExecutionContext) extends AToolMain(tn, up) {

  // Abstract types

  type PartType <: ExPart

  // Views

  override def adminIndexView(admin: User, toolList: ToolList): Future[Html] =
    Future(views.html.admin.randomExes.randomExerciseAdminIndex(admin, statistics = Html(""), this, toolList))

  // Calls

  override def indexCall: Call = controllers.coll.routes.RandomExerciseAdminController.adminIndex(this.urlPart)

}
