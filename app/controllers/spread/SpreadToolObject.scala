package controllers.spread

import model.Enums.ToolState
import model.core.tools.IdPartExToolObject
import play.api.mvc.Call

object SpreadToolObject extends IdPartExToolObject("spread", "Tabellenkalkulation", ToolState.LIVE) {

  // User

  override def indexCall: Call = controllers.spread.routes.SpreadController.index()

  override def exerciseRoute(id: Int, part: String): Call = controllers.spread.routes.SpreadController.exercise(id, part)

  override def exerciseRoutes(id: Int): List[(Call, String)] = List(
    (controllers.spread.routes.SpreadController.exercise(id, "xlsx"), "Excel"),
    (controllers.spread.routes.SpreadController.exercise(id, "ods"), "OpenOffice"))

  override def exesListRoute(id: Int): Call = ???

  override def correctLiveRoute(id: Int, part: String): Call = ???

  override def correctRoute(id: Int, part: String): Call = ???

  // Admin

  val restHeaders: List[String] = List("Musterloesungsdatei", "Vorlagendatei")

  override def adminIndexRoute: Call = controllers.spread.routes.SpreadAdmin.adminIndex()

  override def exercisesRoute: Call = controllers.spread.routes.SpreadAdmin.exercises()

  override def newExFormRoute: Call = controllers.spread.routes.SpreadAdmin.newExerciseForm()

  override def exportExesRoute: Call = controllers.spread.routes.SpreadAdmin.exportExercises()

  override def importExesRoute: Call = controllers.spread.routes.SpreadAdmin.importExercises()

  override def jsonSchemaRoute: Call = controllers.spread.routes.SpreadAdmin.getJSONSchemaFile()

  override def uploadFileRoute: Call = controllers.spread.routes.SpreadAdmin.uploadFile()

  override def changeExStateRoute(id: Int): Call = controllers.spread.routes.SpreadAdmin.changeExState(id)

  override def editExerciseFormRoute(id: Int): Call = controllers.spread.routes.SpreadAdmin.editExerciseForm(id)

  override def editExerciseRoute(id: Int): Call = controllers.spread.routes.SpreadAdmin.editExercise(id)

  override def deleteExerciseRoute(id: Int): Call = controllers.spread.routes.SpreadAdmin.deleteExercise(id)

}