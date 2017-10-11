package controllers.programming

import model.tools.{IdExToolObject, ToolState}
import play.api.mvc.Call

object ProgToolObject extends IdExToolObject("prog", "Programmierung", ToolState.LIVE) {

  // User

  override def indexCall: Call = controllers.programming.routes.ProgController.index()

  override def exerciseRoute(id: Int): Call = controllers.programming.routes.ProgController.exercise(id)

  override def exerciseRoutes(id: Int) = List(
    (controllers.programming.routes.ProgController.testData(id), "Testdaten erstellen"),
    (controllers.programming.routes.ProgController.exercise(id), "Aufgabe bearbeiten"))

  override def exesListRoute(id: Int): Call = exesListRoute(id)

  override def correctLiveRoute(id: Int): Call = controllers.programming.routes.ProgController.correctLive(id)

  override def correctRoute(id: Int): Call = controllers.programming.routes.ProgController.correct(id)

  // Admin

  val restHeaders: List[String] = List.empty

  override def adminIndexRoute: Call = controllers.programming.routes.ProgAdmin.adminIndex()

  override def exercisesRoute: Call = controllers.programming.routes.ProgAdmin.exercises()

  override def newExFormRoute: Call = controllers.programming.routes.ProgAdmin.newExerciseForm()

  override def exportExesRoute: Call = controllers.programming.routes.ProgAdmin.exportExercises()

  override def importExesRoute: Call = controllers.programming.routes.ProgAdmin.importExercises()

  override def jsonSchemaRoute: Call = controllers.programming.routes.ProgAdmin.getJSONSchemaFile()

  override def uploadFileRoute: Call = controllers.programming.routes.ProgAdmin.uploadFile()

  override def changeExStateRoute(id: Int): Call = controllers.programming.routes.ProgAdmin.changeExState(id)

  override def editExerciseFormRoute(id: Int): Call = controllers.programming.routes.ProgAdmin.editExerciseForm(id)

  override def editExerciseRoute(id: Int): Call = controllers.programming.routes.ProgAdmin.editExercise(id)

  override def deleteExerciseRoute(id: Int): Call = controllers.programming.routes.ProgAdmin.deleteExercise(id)

}