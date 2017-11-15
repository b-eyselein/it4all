package controllers.exes

import model.Enums.ToolState
import model.HasBaseValues
import model.core.tools.IdPartExToolObject
import model.web.WebExercise
import play.api.mvc.Call

object WebToolObject extends IdPartExToolObject("web", "Web", ToolState.LIVE) {

  override def indexCall: Call = controllers.exes.routes.WebController.index()

  override def exerciseRoute(exercise: HasBaseValues, part: String): Call = controllers.exes.routes.WebController.exercise(exercise.id, part)

  override def exerciseRoutes(exercise: HasBaseValues): List[(Call, String)] = exercise match {
    case ex: WebExercise => List.empty ++
      (if (ex.hasHtmlPart) Some((exerciseRoute(exercise, "html"), "Html-Teil")) else None) ++
      (if (ex.hasJsPart) Some((exerciseRoute(exercise, "js"), "Js-Teil")) else None)
    case _               => List((exerciseRoute(exercise, "html"), "Html-Teil"), (exerciseRoute(exercise, "js"), "Js-Teil"))
  }

  // only self reference...
  override def exesListRoute(page: Int): Call = ??? // controllers.exes.routes.WebController.exercises(exercise.id)

  override def correctLiveRoute(exercise: HasBaseValues, part: String): Call = controllers.exes.routes.WebController.correctLive(exercise.id, "html")

  override def correctRoute(exercise: HasBaseValues, part: String): Call = controllers.exes.routes.WebController.correct(exercise.id, "html")


  override val restHeaders: List[String] = List("# Tasks Html / Js", "Text Html / Js")

  override def adminIndexRoute: Call = controllers.exes.routes.WebController.adminIndex()

  override def adminExesListRoute: Call = controllers.exes.routes.WebController.exercises()

  override def newExFormRoute: Call = controllers.exes.routes.WebController.newExerciseForm()

  override def importExesRoute: Call = controllers.exes.routes.WebController.importExercises()

  override def exportExesRoute: Call = controllers.exes.routes.WebController.exportExercises()

  override def exportExesAsFileRoute: Call = controllers.exes.routes.WebController.exportExercisesAsFile()

  override def changeExStateRoute(exercise: HasBaseValues): Call = controllers.exes.routes.WebController.changeExState(exercise.id)

  override def editExerciseFormRoute(exercise: HasBaseValues): Call = controllers.exes.routes.WebController.editExerciseForm(exercise.id)

  override def editExerciseRoute(exercise: HasBaseValues): Call = controllers.exes.routes.WebController.editExercise(exercise.id)

  override def deleteExerciseRoute(exercise: HasBaseValues): Call = controllers.exes.routes.WebController.deleteExercise(exercise.id)

}
