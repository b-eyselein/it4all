package controllers.exes

import model.Enums.ToolState
import model.core.HasBaseValues
import model.core.tools.IdPartExToolObject
import play.api.mvc.Call

object WebToolObject extends IdPartExToolObject("web", "Web", ToolState.LIVE) {

  // User

  override def indexCall: Call = controllers.exes.routes.WebController.index()

  override def exerciseRoute(exercise: HasBaseValues, part: String): Call = controllers.exes.routes.WebController.exercise(exercise.id, part)

  override def exerciseRoutes(exercise: HasBaseValues) = List(
    (controllers.exes.routes.WebController.exercise(exercise.id, "html"), "Html-Teil"),
    (controllers.exes.routes.WebController.exercise(exercise.id, "js"), "Js-Teil"))

  override def exesListRoute(page: Int): Call = null // controllers.exes.routes.WebController.exercises(exercise.id)

  override def correctLiveRoute(exercise: HasBaseValues, part: String): Call = controllers.exes.routes.WebController.correctLive(exercise.id, "html")

  override def correctRoute(exercise: HasBaseValues, part: String): Call = controllers.exes.routes.WebController.correct(exercise.id, "html")

  // Admin

  override val restHeaders: List[String] = List("# Tasks Html / Js", "Text Html / Js")

  override def adminIndexRoute: Call = controllers.exes.routes.WebController.adminIndex()

  override def adminExesListRoute: Call = controllers.exes.routes.WebController.exercises()

  override def newExFormRoute: Call = controllers.exes.routes.WebController.newExerciseForm()

  override def exportExesRoute: Call = controllers.exes.routes.WebController.exportExercises()

  override def importExesRoute: Call = controllers.exes.routes.WebController.importExercises()

  //  override def jsonSchemaRoute: Call = controllers.exes.routes.WebController.getJSONSchemaFile()

  //  override def uploadFileRoute: Call = controllers.exes.routes.WebController.uploadFile()

  override def changeExStateRoute(exercise: HasBaseValues): Call = controllers.exes.routes.WebController.changeExState(exercise.id)

  override def editExerciseFormRoute(exercise: HasBaseValues): Call = controllers.exes.routes.WebController.editExerciseForm(exercise.id)

  override def editExerciseRoute(exercise: HasBaseValues): Call = controllers.exes.routes.WebController.editExercise(exercise.id)

  override def deleteExerciseRoute(exercise: HasBaseValues): Call = controllers.exes.routes.WebController.deleteExercise(exercise.id)

}
