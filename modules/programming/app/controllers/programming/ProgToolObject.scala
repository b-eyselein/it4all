package controllers.programming

import controllers.programming.routes.ProgController._
import model.tools.ToolState
import controllers.programming.routes.ProgAdmin._
import controllers.core.ToolObject

object ProgToolObject extends ToolObject("Programmierung", ToolState.LIVE) {

  // User
  
  override def indexCall = index()

  override def exerciseRoute(id: Int) = exercise(id)

  override def exerciseRoutes(id: Int) = List((testData(id), "Testdaten erstellen"), (exercise(id), "Aufgabe bearbeiten"))
  
  override def exesListRoute(id: Int) = exesListRoute(id)

  override def correctLiveRoute(id: Int) = correctLive(id)

  override def correctRoute(id: Int) = correct(id)

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