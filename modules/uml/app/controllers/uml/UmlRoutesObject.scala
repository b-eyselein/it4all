package controllers.uml

import controllers.uml.routes.UmlAdmin._
import controllers.uml.routes.UmlController._
import controllers.core.RoutesObject

object UmlRoutesObject extends RoutesObject("Uml") {

  // User

  override def exerciseRoute(id: Int) = null

  override def exerciseRoutes(id: Int) =
    List((classSelection(id), "Mit Zwischenkorrektur"), (diagramDrawing(id), "Freies Erstellen"))
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