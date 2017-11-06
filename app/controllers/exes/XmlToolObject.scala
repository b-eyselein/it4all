package controllers.exes

import model.Enums.ToolState
import model.core.HasBaseValues
import model.core.tools.IdExToolObject
import play.api.mvc.Call

object XmlToolObject extends IdExToolObject("xml", "Xml", ToolState.LIVE) {

  // User

  override def indexCall: Call = controllers.exes.routes.XmlController.index()

  override def exerciseRoute(exercise: HasBaseValues): Call = controllers.exes.routes.XmlController.exercise(exercise.id)

  override def exesListRoute(page: Int): Call = ??? //controllers.exes.routes.XmlController.exercises(exercise.id)

  override def correctLiveRoute(exercise: HasBaseValues): Call = controllers.exes.routes.XmlController.correctLive(exercise.id)

  override def correctRoute(exercise: HasBaseValues): Call = controllers.exes.routes.XmlController.correct(exercise.id)

  // Admin

  val restHeaders: List[String] = List("Typ", "Wurzelknoten")

  override def adminIndexRoute: Call = controllers.exes.routes.XmlController.adminIndex()

  override def adminExesListRoute: Call = controllers.exes.routes.XmlController.exercises()

  override def newExFormRoute: Call = controllers.exes.routes.XmlController.newExerciseForm()

  //  override def exportExesRoute: Call = controllers.exes.routes.XmlController.exportExercises()

  //  override def importExesRoute: Call = controllers.exes.routes.XmlController.importExercises()

  //  override def jsonSchemaRoute: Call = controllers.exes.routes.XmlController.getJSONSchemaFile()

  //  override def uploadFileRoute: Call = controllers.exes.routes.XmlController.uploadFile()

  override def changeExStateRoute(exercise: HasBaseValues): Call = controllers.exes.routes.XmlController.changeExState(exercise.id)

  override def editExerciseFormRoute(exercise: HasBaseValues): Call = controllers.exes.routes.XmlController.editExerciseForm(exercise.id)

  override def editExerciseRoute(exercise: HasBaseValues): Call = controllers.exes.routes.XmlController.editExercise(exercise.id)

  override def deleteExerciseRoute(exercise: HasBaseValues): Call = controllers.exes.routes.XmlController.deleteExercise(exercise.id)

}
