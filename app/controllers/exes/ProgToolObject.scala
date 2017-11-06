package controllers.exes

import model.Enums.ToolState
import model.core.HasBaseValues
import model.core.tools.IdExToolObject
import play.api.mvc.Call

object ProgToolObject extends IdExToolObject("prog", "Programmierung", ToolState.LIVE) {

  // User

  override def indexCall: Call = controllers.exes.routes.ProgController.index()

  override def exerciseRoute(exercise: HasBaseValues): Call = controllers.exes.routes.ProgController.exercise(exercise.id)

  override def exerciseRoutes(exercise: HasBaseValues) = List(
    (controllers.exes.routes.ProgController.testData(exercise.id), "Testdaten erstellen"),
    (controllers.exes.routes.ProgController.exercise(exercise.id), "Aufgabe bearbeiten"))

  override def exesListRoute(page: Int): Call = ???

  override def correctLiveRoute(exercise: HasBaseValues): Call = controllers.exes.routes.ProgController.correctLive(exercise.id)

  override def correctRoute(exercise: HasBaseValues): Call = controllers.exes.routes.ProgController.correct(exercise.id)

  // Admin

  val restHeaders: List[String] = List("Funktionsname", "Anzahl Inputs")

  override def adminIndexRoute: Call = controllers.exes.routes.ProgController.adminIndex()

  override def adminExesListRoute: Call = controllers.exes.routes.ProgController.exercises()

  override def newExFormRoute: Call = controllers.exes.routes.ProgController.newExerciseForm()

  override def exportExesRoute: Call = controllers.exes.routes.ProgController.exportExercises()

  override def importExesRoute: Call = controllers.exes.routes.ProgController.importExercises()

  //  override def jsonSchemaRoute: Call = controllers.exes.routes.ProgController.getJSONSchemaFile()

  //  override def uploadFileRoute: Call = controllers.exes.routes.ProgController.uploadFile()

  override def changeExStateRoute(exercise: HasBaseValues): Call = controllers.exes.routes.ProgController.changeExState(exercise.id)

  override def editExerciseFormRoute(exercise: HasBaseValues): Call = controllers.exes.routes.ProgController.editExerciseForm(exercise.id)

  override def editExerciseRoute(exercise: HasBaseValues): Call = controllers.exes.routes.ProgController.editExercise(exercise.id)

  override def deleteExerciseRoute(exercise: HasBaseValues): Call = controllers.exes.routes.ProgController.deleteExercise(exercise.id)

}
