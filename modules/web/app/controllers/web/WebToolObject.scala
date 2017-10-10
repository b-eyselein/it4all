package controllers.web

import model.tools.{IdExToolObject, ToolState}
import play.api.Configuration
import play.api.mvc.Call

class WebToolObject(c: Configuration) extends IdExToolObject(c, "web", "Web", ToolState.LIVE) {

  // User

  override def indexCall: Call = controllers.web.routes.WebController.index()

  override def exerciseRoute(id: Int): Call = null // controllers.web.routes.WebController.exercise(id)

  override def exerciseRoutes(id: Int) = List(
    (controllers.web.routes.WebController.exercise(id, "html"), "Html-Teil"),
    (controllers.web.routes.WebController.exercise(id, "js"), "Js-Teil"))

  override def exesListRoute(id: Int): Call = exesListRoute(id)

  override def correctLiveRoute(id: Int): Call = controllers.web.routes.WebController.correctLive(id, "html")

  override def correctRoute(id: Int): Call = controllers.web.routes.WebController.correct(id, "html")

  // Admin

  val restHeaders: List[String] = List("# Tasks Html / Js", "Text Html / Js")

  override def adminIndexRoute: Call = controllers.xml.routes.XmlAdmin.adminIndex()

  override def exercisesRoute: Call = controllers.xml.routes.XmlAdmin.exercises()

  override def newExFormRoute: Call = controllers.xml.routes.XmlAdmin.newExerciseForm()

  override def exportExesRoute: Call = controllers.xml.routes.XmlAdmin.exportExercises()

  override def importExesRoute: Call = controllers.xml.routes.XmlAdmin.importExercises()

  override def jsonSchemaRoute: Call = controllers.xml.routes.XmlAdmin.getJSONSchemaFile()

  override def uploadFileRoute: Call = controllers.xml.routes.XmlAdmin.uploadFile()

  override def changeExStateRoute(id: Int): Call = controllers.xml.routes.XmlAdmin.changeExState(id)

  override def editExerciseFormRoute(id: Int): Call = controllers.xml.routes.XmlAdmin.editExerciseForm(id)

  override def editExerciseRoute(id: Int): Call = controllers.xml.routes.XmlAdmin.editExercise(id)

  override def deleteExerciseRoute(id: Int): Call = controllers.xml.routes.XmlAdmin.deleteExercise(id)

}
