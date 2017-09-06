package controllers.core

import play.api.mvc.Call

abstract class RoutesObject {

  // User

  def correctLiveRoute(id: Int): Call

  def correctRoute(id: Int): Call

  // Admin

  val restHeaders: List[String]

  def adminIndexRoute: Call
  
  def exercisesRoute: Call

  def newExFormRoute: Call

  def exportExesRoute: Call

  def importExesRoute: Call

  def jsonSchemaRoute: Call

  def uploadFileRoute: Call
  
  def changeExStateRoute(id: Int): Call

  def editExerciseFormRoute(id: Int): Call

  def editExerciseRoute(id: Int): Call

  def deleteExerciseRoute(id: Int): Call

}