package controllers.idPartExes

import model.web.{WebCompleteEx, WebConsts, WebExercise}
import model.{Consts, HasBaseValues}
import play.api.mvc.Call

object WebToolObject extends IdPartExToolObject {

  override type CompEx = WebCompleteEx

  override val hasTags : Boolean = true
  override val toolname: String  = "Web"
  override val exType  : String  = "web"
  override val consts  : Consts  = WebConsts

  override def indexCall: Call = controllers.idPartExes.routes.WebController.index()

  override def exerciseRoute(exercise: HasBaseValues, part: String): Call = controllers.idPartExes.routes.WebController.exercise(exercise.id, part)

  override def exerciseRoutes(exercise: WebCompleteEx): List[(Call, String)] = exercise.ex match {
    case ex: WebExercise => List.empty ++
      (if (ex.hasHtmlPart) Some((exerciseRoute(exercise.ex, "html"), "Html-Teil")) else None) ++
      (if (ex.hasJsPart) Some((exerciseRoute(exercise.ex, "js"), "Js-Teil")) else None)
    case _               => List((exerciseRoute(exercise.ex, "html"), "Html-Teil"), (exerciseRoute(exercise.ex, "js"), "Js-Teil"))
  }

  override def exerciseListRoute(page: Int): Call = controllers.idPartExes.routes.WebController.exerciseList(page)

  override def correctLiveRoute(exercise: HasBaseValues, part: String): Call = controllers.idPartExes.routes.WebController.correctLive(exercise.id, "html")

  override def correctRoute(exercise: HasBaseValues, part: String): Call = controllers.idPartExes.routes.WebController.correct(exercise.id, "html")


  override val restHeaders: List[String] = List("# Tasks Html / Js", "Text Html / Js")

  override def adminIndexRoute: Call = controllers.idPartExes.routes.WebController.adminIndex()

  override def adminExesListRoute: Call = controllers.idPartExes.routes.WebController.adminExerciseList()

  override def newExFormRoute: Call = controllers.idPartExes.routes.WebController.adminNewExerciseForm()

  override def importExesRoute: Call = controllers.idPartExes.routes.WebController.adminImportExercises()

  override def exportExesRoute: Call = controllers.idPartExes.routes.WebController.adminExportExercises()

  override def exportExesAsFileRoute: Call = controllers.idPartExes.routes.WebController.adminExportExercisesAsFile()

  override def changeExStateRoute(exercise: HasBaseValues): Call = controllers.idPartExes.routes.WebController.adminChangeExState(exercise.id)

  override def editExerciseFormRoute(exercise: HasBaseValues): Call = controllers.idPartExes.routes.WebController.adminEditExerciseForm(exercise.id)

  override def editExerciseRoute(exercise: HasBaseValues): Call = controllers.idPartExes.routes.WebController.adminEditExercise(exercise.id)

  override def deleteExerciseRoute(exercise: HasBaseValues): Call = controllers.idPartExes.routes.WebController.adminDeleteExercise(exercise.id)

}
