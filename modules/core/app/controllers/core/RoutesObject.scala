package controllers.core

import play.api.mvc.Call

abstract class RoutesObject {

  val adminIndexRoute: Call

  val exercisesRoutes: Call

  def editExerciseFormRoute(id: Int): Call

  def editExerciseRoute(id: Int): Call
  
  def deleteExerciseRoute(id: Int): Call

}