package controllers.exes

import model.Enums.ToolState
import model.core.HasBaseValues
import model.core.tools.IdPartExToolObject
import play.api.mvc.Call

object SpreadToolObject extends IdPartExToolObject("spread", "Tabellenkalkulation", ToolState.LIVE) {

  // User

  override def indexCall: Call = controllers.exes.routes.SpreadController.index()

  override def exerciseRoute(exercise: HasBaseValues, part: String): Call = controllers.exes.routes.SpreadController.exercise(exercise.id, part)

  override def exerciseRoutes(exercise: HasBaseValues): List[(Call, String)] = List(
    (controllers.exes.routes.SpreadController.exercise(exercise.id, "xlsx"), "Excel"),
    (controllers.exes.routes.SpreadController.exercise(exercise.id, "ods"), "OpenOffice"))

  override def exesListRoute(page: Int): Call = ???

  override def correctLiveRoute(exercise: HasBaseValues, part: String): Call = ???

  override def correctRoute(exercise: HasBaseValues, part: String): Call = ???

  // Admin

  val restHeaders: List[String] = List("Musterloesungsdatei", "Vorlagendatei")

  override def adminIndexRoute: Call = controllers.exes.routes.SpreadController.adminIndex()

  override def adminExesListRoute: Call = controllers.exes.routes.SpreadController.exercises()

  override def newExFormRoute: Call = controllers.exes.routes.SpreadController.newExerciseForm()

  //  override def exportExesRoute: Call = controllers.exes.routes.SpreadController.exportExercises()

  //  override def importExesRoute: Call = controllers.exes.routes.SpreadController.importExercises()

  //  override def jsonSchemaRoute: Call = controllers.exes.routes.SpreadController.getJSONSchemaFile()

  //  override def uploadFileRoute: Call = controllers.exes.routes.SpreadController.uploadFile()

  override def changeExStateRoute(exercise: HasBaseValues): Call = controllers.exes.routes.SpreadController.changeExState(exercise.id)

  override def editExerciseFormRoute(exercise: HasBaseValues): Call = controllers.exes.routes.SpreadController.editExerciseForm(exercise.id)

  override def editExerciseRoute(exercise: HasBaseValues): Call = controllers.exes.routes.SpreadController.editExercise(exercise.id)

  override def deleteExerciseRoute(exercise: HasBaseValues): Call = controllers.exes.routes.SpreadController.deleteExercise(exercise.id)

}
