package model.toolMains

import model.User
import model.core.ExPart
import play.api.libs.json.JsValue
import play.api.mvc.{AnyContent, Request}
import play.twirl.api.Html

abstract class RandomExerciseToolMain(urlPart: String) extends AToolMain(urlPart) {

  // Abstract types

  type PartType <: ExPart

  // Helper methods

  val exParts: Seq[PartType]

  def exTypeFromUrl(exType: String): Option[PartType] = exParts.find(_.urlName == exType)

  // Views

  def newExercise(user: User, exPart: PartType, option: Map[String, Seq[String]]): Html

  def index(user: User): Html

  // Correction

  def checkSolution(user: User, exPart: PartType, request: Request[AnyContent]): JsValue

}
