package controllers.web

import model.Enums.ToolState
import model.core.tools.IdPartExToolObject
import play.api.mvc.Call

object WebToolObject extends IdPartExToolObject("web", "Web", ToolState.LIVE) {

  // User

  override def indexCall: Call = controllers.web.routes.WebController.index()

  override def exerciseRoute(id: Int, part: String): Call = controllers.web.routes.WebController.exercise(id, part)

  override def exerciseRoutes(id: Int) = List(
    (controllers.web.routes.WebController.exercise(id, "html"), "Html-Teil"),
    (controllers.web.routes.WebController.exercise(id, "js"), "Js-Teil"))

  override def exesListRoute(id: Int): Call = null // controllers.web.routes.WebController.exercises(id)

  override def correctLiveRoute(id: Int, part: String): Call = controllers.web.routes.WebController.correctLive(id, "html")

  override def correctRoute(id: Int, part: String): Call = controllers.web.routes.WebController.correct(id, "html")

  // Admin

  val restHeaders: List[String] = List("# Tasks Html / Js", "Text Html / Js")

  override def adminIndexRoute: Call = controllers.web.routes.WebAdmin.adminIndex()

  override def exercisesRoute: Call = controllers.web.routes.WebAdmin.exercises()

  override def newExFormRoute: Call = controllers.web.routes.WebAdmin.newExerciseForm()

  override def exportExesRoute: Call = controllers.web.routes.WebAdmin.exportExercises()

  override def importExesRoute: Call = controllers.web.routes.WebAdmin.importExercises()

  override def jsonSchemaRoute: Call = controllers.web.routes.WebAdmin.getJSONSchemaFile()

  override def uploadFileRoute: Call = controllers.web.routes.WebAdmin.uploadFile()

  override def changeExStateRoute(id: Int): Call = controllers.web.routes.WebAdmin.changeExState(id)

  override def editExerciseFormRoute(id: Int): Call = controllers.web.routes.WebAdmin.editExerciseForm(id)

  override def editExerciseRoute(id: Int): Call = controllers.web.routes.WebAdmin.editExercise(id)

  override def deleteExerciseRoute(id: Int): Call = controllers.web.routes.WebAdmin.deleteExercise(id)

}
