package model.toolMains

import model.User
import model.core.ExPart
import model.learningPath.LearningPath
import play.api.libs.json.JsValue
import play.api.mvc.{AnyContent, Call, Request}
import play.twirl.api.Html

import scala.concurrent.Future

abstract class RandomExerciseToolMain(urlPart: String) extends AToolMain(urlPart) {

  // Abstract types

  type PartType <: ExPart

  // Helper methods

  val exParts: Seq[PartType]

  def exTypeFromUrl(exType: String): Option[PartType] = exParts.find(_.urlName == exType)

  def readLearningPaths: Seq[LearningPath]

  // DB

  def futureLearningPaths: Future[Seq[LearningPath]] = tables.futureLearningPaths

  def futureLearningPathById(id: Int): Future[Option[LearningPath]] = tables.futureLearningPathById(id)

  // Views

  def newExercise(user: User, exPart: PartType, option: Map[String, Seq[String]]): Html

  def index(user: User, learningPathBase: Seq[LearningPath]): Html

  // Correction

  def checkSolution(user: User, exPart: PartType, request: Request[AnyContent]): JsValue

  // Calls

  override def indexCall: Call = controllers.routes.RandomExerciseController.index(this.urlPart)

}
