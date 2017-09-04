package controllers.core

import play.api.mvc.Call
import controllers.ReverseAdminController

abstract class RoutesObject {

  // User

  def correctLiveRoute(id: Int): Call = null

  def correctRoute(id: Int): Call = null

  // Admin

  val adminIndexRoute: Call

  val exercisesRoutes: Call

  val newExFormRoute: Call

  val exportExesRoute: Call

  val importExesRoute: Call

  val jsonSchemaRoute: Call

  val uploadFileRoute: Call

  def editExerciseFormRoute(id: Int): Call

  def editExerciseRoute(id: Int): Call

  def deleteExerciseRoute(id: Int): Call

}