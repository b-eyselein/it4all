package controllers.uml

import controllers.uml.routes.UmlAdmin._
import controllers.uml.routes.UmlController._

import model.tools.{ToolState, IdExToolObject}
import model.UmlExPart

object UmlToolObject extends IdExToolObject("Uml", ToolState.LIVE) {

  // User

  override def indexCall = index()

  override def exerciseRoute(id: Int) = ???

  override def exerciseRoutes(id: Int) =
    List(
      (exercise(id, UmlExPart.CLASS_SELECTION.toString), "Mit Zwischenkorrektur"),
      (exercise(id, UmlExPart.DIAG_DRAWING.toString), "Freies Erstellen"))

  override def exesListRoute(id: Int) = exesListRoute(id)

  override def correctLiveRoute(id: Int) = ???

  override def correctRoute(id: Int) = ???

  // Admin

  val restHeaders = List.empty

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