package model.toolMains

import model.{ExPart, User}
import play.api.i18n.MessagesProvider
import play.api.mvc.{Call, RequestHeader}
import play.twirl.api.Html

import scala.concurrent.{ExecutionContext, Future}

abstract class RandomExerciseToolMain(tn: String, up: String)(implicit ec: ExecutionContext) extends AToolMain(tn, up) {

  // Abstract types

  type PartType <: ExPart

  // Helper methods

  protected val exParts: Seq[PartType]

  def exTypeFromUrl(exType: String): Option[PartType] = exParts.find(_.urlName == exType)

  // Views

  def newExercise(user: User, exPart: PartType, option: Map[String, Seq[String]])
                 (implicit requestHeader: RequestHeader, messagesProvider: MessagesProvider): Html

  override def adminIndexView(admin: User, toolList: ToolList): Future[Html] =
    Future(views.html.admin.randomExes.randomExerciseAdminIndex(admin, statistics = Html(""), this, toolList))

  // Calls

  override def indexCall: Call = controllers.coll.routes.RandomExerciseController.index(this.urlPart)

}
