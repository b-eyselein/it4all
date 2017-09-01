package controllers.xml

import controllers.xml.routes.XmlAdmin._
import controllers.core.RoutesObject
import play.api.mvc.Call

object XmlRoutesObject extends RoutesObject {

  val adminIndexRoute = adminIndex

  val exercisesRoutes = exercises
  
  val newExFormRoute = newExerciseForm

  val exportExesRoute = exportExercises
  
  val importExesRoute = importExercises
  
  val jsonSchemaRoute = getJSONSchemaFile
  
  val uploadFileRoute = uploadFile
  
  override def editExerciseFormRoute(id: Int) = editExerciseForm(id)

  override def editExerciseRoute(id: Int) = editExercise(id)
  
  override def deleteExerciseRoute(id: Int) = deleteExercise(id)

}