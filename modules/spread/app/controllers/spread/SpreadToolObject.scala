package controllers.spread

import controllers.spread.routes.SpreadAdmin._
import model.tools.ToolState
import controllers.spread.routes.SpreadController._
import controllers.core.ToolObject

object SpreadToolObject extends ToolObject("Tabellenkalkulation", ToolState.LIVE) {

  // User
  
  override def indexCall = index()

  def exerciseRoute(id: Int) = exercise(id)
  
  
  
  override def exesListRoute(id: Int) = exesListRoute(id)

  override def correctLiveRoute(id: Int) = null

  override def correctRoute(id: Int) = null

  // Admin

  override val restHeaders = List.empty

  override def adminIndexRoute = adminIndex

  override def exercisesRoute = exercises

  override def newExFormRoute = newExerciseForm

  override def exportExesRoute = exportExercises

  override def importExesRoute = importExercises

  override def jsonSchemaRoute = getJSONSchemaFile

  override def uploadFileRoute = uploadFile

  override def changeExStateRoute(id: Int) = changeExState(id)

  override def editExerciseFormRoute(id: Int) = editExerciseForm(id)

  override def editExerciseRoute(id: Int) = editExercise(id)

  override def deleteExerciseRoute(id: Int) = deleteExercise(id)

}