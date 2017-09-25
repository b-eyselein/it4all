package controllers.web

import controllers.web.routes.WebAdmin._
import controllers.web.routes.WebController._
import controllers.core.ToolObject
import model.tools.ToolState

object WebToolObject extends ToolObject("Web", ToolState.LIVE) {

  // User
  
  override def indexCall = index()

  override def exerciseRoute(id: Int) = null // exercise(id)

  override def exerciseRoutes(id: Int) = List((exercise(id, "html"), "Html-Teil"), (exercise(id, "js"), "Js-Teil"))
  
  override def exesListRoute(id: Int) = exesListRoute(id)

  override def correctLiveRoute(id: Int) = correctLive(id, "html")

  override def correctRoute(id: Int) = correct(id, "html")

  // Admin

  override val restHeaders = List("# Tasks Html / Js", "Text Html / Js")

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
