package controllers.programming

import controllers.programming.routes.ProgAdmin._
import controllers.core.RoutesObject

object ProgRoutesObject extends RoutesObject {

  val adminIndexRoute = index

  val exercisesRoutes = exercises

  override def editExerciseFormRoute(id: Int) = editExerciseForm(id)

  override def editExerciseRoute(id: Int) = editExercise(id)

  override def deleteExerciseRoute(id: Int) = deleteExercise(id)
  
}