package controllers.uml

import model.UmlExPart
import model.tools.{IdPartExToolObject, ToolState}
import play.api.mvc.Call

object UmlToolObject extends IdPartExToolObject("uml", "Uml", ToolState.LIVE) {

  // User

  override def indexCall: Call = controllers.uml.routes.UmlController.index()

  override def exerciseRoute(id: Int, part: String): Call = controllers.uml.routes.UmlController.exercise(id, part)

  override def exerciseRoutes(id: Int) = List(
    (controllers.uml.routes.UmlController.exercise(id, UmlExPart.CLASS_SELECTION.toString), "Mit Zwischenkorrektur"),
    (controllers.uml.routes.UmlController.exercise(id, UmlExPart.DIAG_DRAWING.toString), "Freies Erstellen"))

  override def exesListRoute(id: Int): Call = ???

  override def correctLiveRoute(id: Int, part: String): Call = ???

  override def correctRoute(id: Int, part: String): Call = ???

  // Admin

  val restHeaders: List[String] = List("Klassenwahl", "Diagrammzeichnen")

  override def adminIndexRoute: Call = controllers.uml.routes.UmlAdmin.adminIndex()

  override def exercisesRoute: Call = controllers.uml.routes.UmlAdmin.exercises()

  override def newExFormRoute: Call = controllers.uml.routes.UmlAdmin.newExerciseForm()

  override def exportExesRoute: Call = controllers.uml.routes.UmlAdmin.exportExercises()

  override def importExesRoute: Call = controllers.uml.routes.UmlAdmin.importExercises()

  override def jsonSchemaRoute: Call = controllers.uml.routes.UmlAdmin.getJSONSchemaFile()

  override def uploadFileRoute: Call = controllers.uml.routes.UmlAdmin.uploadFile()

  override def changeExStateRoute(id: Int): Call = controllers.uml.routes.UmlAdmin.changeExState(id)

  override def editExerciseFormRoute(id: Int): Call = controllers.uml.routes.UmlAdmin.editExerciseForm(id)

  override def editExerciseRoute(id: Int): Call = controllers.uml.routes.UmlAdmin.editExercise(id)

  override def deleteExerciseRoute(id: Int): Call = controllers.uml.routes.UmlAdmin.deleteExercise(id)

}