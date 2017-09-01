package controllers.web

import controllers.web.routes.WebAdmin._
import controllers.core.RoutesObject

object WebRoutesObject extends RoutesObject {

  val adminIndexRoute = index

  val exercisesRoutes = exercises

  override def editExerciseFormRoute(id: Int) = editExerciseForm(id)
  
  override def editExerciseRoute(id: Int) = editExercise(id)

  override def deleteExerciseRoute(id: Int) = deleteExercise(id)
  
}