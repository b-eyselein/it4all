package controllers.exes.fileExes

import model.{Exercise, FileCompleteEx, HasBaseValues}
import model.core.tools.ExToolObject
import play.api.mvc.Call

trait FileExToolObject extends ExToolObject {

  override type CompEx <: FileCompleteEx[_ <: Exercise]

  val fileTypes: Map[String, String]

  def exerciseRoute(exercise: HasBaseValues, fileExtension: String): Call

  override def exerciseRoutes(exercise: CompEx): Map[Call, String] =
    fileTypes filter (ft => exercise.available(ft._1)) map (ft => (exerciseRoute(exercise.ex, ft._1), s"Mit ${ft._2} bearbeiten"))

  def uploadSolutionRoute(exercise: HasBaseValues, fileExtension: String): Call

  def downloadCorrectedRoute(exercise: HasBaseValues, fileExtension: String): Call

}