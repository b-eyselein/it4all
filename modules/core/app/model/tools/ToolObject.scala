package model.tools

import play.api.mvc.Call

abstract sealed class ToolObject(val exType: String, val state: ToolState, val decoration: String) {
  
  ToolList.register(this)

  def indexCall: Call
  
}

abstract class RandomExToolObject(e: String, s: ToolState, d: String = null) extends ToolObject(e, s, d)

abstract class IdedExToolObject(e: String, s: ToolState = ToolState.ALPHA, d: String = null,
  val pluralName: String = "Aufgaben") extends ToolObject(e, s, d) {

  // User

  def exerciseRoute(id: Int): Call

  def exerciseRoutes(id: Int): List[(Call, String)] = List((exerciseRoute(id), "Aufgabe bearbeiten"))

  def exesListRoute(id: Int): Call

  def correctLiveRoute(id: Int): Call

  def correctRoute(id: Int): Call

  // Admin

  def restHeaders: List[String]

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