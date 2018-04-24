package model.toolMains

import model.User
import model.core.ExPart
import play.api.libs.json.JsValue
import play.api.mvc.{AnyContent, Call, Request}
import play.twirl.api.Html

import scala.concurrent.{ExecutionContext, Future}

abstract class RandomExerciseToolMain(urlPart: String)(implicit ec: ExecutionContext) extends AToolMain(urlPart) {

  // Abstract types

  type PartType <: ExPart

  // Helper methods

  val exParts: Seq[PartType]

  def exTypeFromUrl(exType: String): Option[PartType] = exParts.find(_.urlName == exType)

  // Views

  def newExercise(user: User, exPart: PartType, option: Map[String, Seq[String]]): Html

  override def adminIndexView(admin: User): Future[Html] = Future(views.html.admin.randomExes.randomExerciseAdminIndex(admin, statistics = Html(""), this))

  // Correction

  def checkSolution(user: User, exPart: PartType, request: Request[AnyContent]): JsValue

  // Calls

  override def indexCall: Call = controllers.routes.MainExerciseController.index(this.urlPart)

}
