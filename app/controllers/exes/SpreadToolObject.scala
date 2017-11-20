package controllers.exes

import model.Enums.ToolState
import model.HasBaseValues
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

  override val restHeaders: List[String] = List("Musterloesungsdatei", "Vorlagendatei")

  override def adminIndexRoute: Call = controllers.exes.routes.SpreadController.adminIndex()

  override def adminExesListRoute: Call = controllers.exes.routes.SpreadController.adminExerciseList()

  override def newExFormRoute: Call = controllers.exes.routes.SpreadController.adminNewExerciseForm()

  override def importExesRoute: Call = controllers.exes.routes.SpreadController.adminImportExercises()

  override def exportExesRoute: Call = controllers.exes.routes.SpreadController.adminExportExercises()

  override def exportExesAsFileRoute: Call = controllers.exes.routes.SpreadController.adminExportExercisesAsFile()

  override def changeExStateRoute(exercise: HasBaseValues): Call = controllers.exes.routes.SpreadController.adminChangeExState(exercise.id)

  override def editExerciseFormRoute(exercise: HasBaseValues): Call = controllers.exes.routes.SpreadController.adminEditExerciseForm(exercise.id)

  override def editExerciseRoute(exercise: HasBaseValues): Call = controllers.exes.routes.SpreadController.adminEditExercise(exercise.id)

  override def deleteExerciseRoute(exercise: HasBaseValues): Call = controllers.exes.routes.SpreadController.adminDeleteExercise(exercise.id)

}
