package controllers.web

import controllers.web.routes.WebAdmin._
import controllers.core.RoutesObject

object WebRoutesObject extends RoutesObject {

  // User

  override def correctLiveRoute(id: Int) = null

  override def correctRoute(id: Int) = null

  // Admin

  override val restHeaders = List("# Tasks Html / Js", "Text Html", "Text Js")

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