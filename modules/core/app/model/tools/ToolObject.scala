package model.tools

import play.api.mvc.Call

abstract class ToolObject(
  val exType: String,
  val state: ToolState = ToolState.ALPHA,
  val decoration: String = null,
  val pluralName: String = "Aufgaben") {

  ToolList.register(this)

  // User

  def indexCall: Call = null

  def exerciseRoute(id: Int): Call

  def exerciseRoutes(id: Int): List[(Call, String)] = List((exerciseRoute(id), "Aufgabe bearbeiten"))

  def exesListRoute(id: Int): Call

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