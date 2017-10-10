package controllers.xml

import model.tools.{IdExToolObject, ToolState}
import play.api.Configuration
import play.mvc.Call

class XmlToolObject(c: Configuration) extends IdExToolObject(c, "xml", "Xml", ToolState.LIVE) {

  // User

  override def indexCall: Call = controllers.xml.routes.XmlController.index()

  override def exerciseRoute(id: Int): Call = controllers.xml.routes.XmlController.exercise(id)

  override def exesListRoute(id: Int): Call = ??? //controllers.xml.routes.XmlController.exercises(id)

  override def correctLiveRoute(id: Int): Call = controllers.xml.routes.XmlController.correctLive(id)

  override def correctRoute(id: Int): Call = controllers.xml.routes.XmlController.correct(id)

  // Admin

  val restHeaders: List[String] = List("Typ", "Wurzelknoten")

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