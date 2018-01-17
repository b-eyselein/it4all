package controllers.exes.idExes

import model.HasBaseValues
import model.core.tools.ExToolObject
import play.api.mvc.Call

trait IdExToolObject extends ExToolObject {

  def exerciseRoute(exercise: HasBaseValues): Call

  override def exerciseRoutes(exercise: CompEx): Map[Call, String] = Map(exerciseRoute(exercise.ex) -> "Aufgabe bearbeiten")

  def correctRoute(exercise: HasBaseValues): Call

  def correctLiveRoute(exercise: HasBaseValues): Call

}