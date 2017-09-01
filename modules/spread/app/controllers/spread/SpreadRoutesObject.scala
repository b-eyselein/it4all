package controllers.spread

import controllers.spread.routes.SpreadAdmin._
import controllers.core.RoutesObject

object SpreadRoutesObject extends RoutesObject {

  val adminIndexRoute = index

  val exercisesRoutes = exercises

  override def editExerciseFormRoute(id: Int) = editExerciseForm(id)

  override def editExerciseRoute(id: Int) = editExercise(id)

  override def deleteExerciseRoute(id: Int) = deleteExercise(id)

}