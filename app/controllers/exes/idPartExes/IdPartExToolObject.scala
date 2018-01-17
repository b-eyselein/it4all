package controllers.exes.idPartExes

import model.HasBaseValues
import model.core.tools.ExToolObject
import play.api.mvc.Call

trait IdPartExToolObject extends ExToolObject {

  def exParts: Map[String, String]

  def exerciseRoute(exercise: HasBaseValues, part: String): Call

  override def exerciseRoutes(exercise: CompEx): Map[Call, String] = exParts map (exPart => (exerciseRoute(exercise.ex, exPart._1.toLowerCase), exPart._2))

  def correctLiveRoute(exercise: HasBaseValues, part: String): Call

  def correctRoute(exercise: HasBaseValues, part: String): Call

}