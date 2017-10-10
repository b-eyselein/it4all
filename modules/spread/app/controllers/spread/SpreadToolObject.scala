package controllers.spread

import model.tools.{IdExToolObject, ToolState}
import play.api.Configuration
import play.api.mvc.Call

class SpreadToolObject(c: Configuration) extends IdExToolObject(c, "spread", "Tabellenkalkulation", ToolState.LIVE) {

  // User

  override def indexCall: Call = controllers.spread.routes.SpreadController.index()

  def exerciseRoute(id: Int): Call = controllers.spread.routes.SpreadController.exercise(id)

  override def exesListRoute(id: Int): Call = ???

  override def correctLiveRoute(id: Int): Call = ???

  override def correctRoute(id: Int): Call = ???

  // Admin

  val restHeaders: List[String] = List.empty

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