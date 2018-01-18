package controllers.exes.idPartExes

import model.core.tools.IdPartExToolObject
import model.web.{WebCompleteEx, WebConsts, WebExParts, WebExercise}
import model.{Consts, HasBaseValues}
import play.api.mvc.Call

import scala.language.postfixOps

object WebToolObject extends IdPartExToolObject {

  override type CompEx = WebCompleteEx

  override def exParts: Map[String, String] = WebExParts.values map (part => part.urlName -> part.partName) toMap

  override val hasTags : Boolean = true
  override val toolname: String  = "Web"
  override val exType  : String  = "web"
  override val consts  : Consts  = WebConsts

  override def indexCall: Call = routes.WebController.index()

  override def exerciseRoute(exercise: HasBaseValues, part: String): Call = routes.WebController.exercise(exercise.id, part)

  override def exerciseRoutes(exercise: WebCompleteEx): Map[Call, String] = exercise.ex match {
    // FIXME: user super method...
    case ex: WebExercise => Map.empty ++
      (if (ex.hasHtmlPart) Some((exerciseRoute(exercise.ex, "html"), "Html-Teil")) else None) ++
      (if (ex.hasJsPart) Some((exerciseRoute(exercise.ex, "js"), "Js-Teil")) else None)
    case _               => Map(exerciseRoute(exercise.ex, "html") -> "Html-Teil", exerciseRoute(exercise.ex, "js") -> "Js-Teil")
  }

  override def exerciseListRoute(page: Int): Call = routes.WebController.exerciseList(page)

  override def correctLiveRoute(exercise: HasBaseValues): Call = routes.WebController.correctLive(exercise.id)

  override def correctRoute(exercise: HasBaseValues): Call = routes.WebController.correct(exercise.id)


  override def adminIndexRoute: Call = routes.WebController.adminIndex()

  override def adminExesListRoute: Call = routes.WebController.adminExerciseList()

  override def newExFormRoute: Call = routes.WebController.adminNewExerciseForm()

  override def createNewExRoute: Call = routes.WebController.adminCreateExercise()

  override def importExesRoute: Call = routes.WebController.adminImportExercises()

  override def exportExesRoute: Call = routes.WebController.adminExportExercises()

  override def exportExesAsFileRoute: Call = routes.WebController.adminExportExercisesAsFile()

  override def changeExStateRoute(exercise: HasBaseValues): Call = routes.WebController.adminChangeExState(exercise.id)

  override def editExerciseFormRoute(exercise: HasBaseValues): Call = routes.WebController.adminEditExerciseForm(exercise.id)

  override def editExerciseRoute(exercise: HasBaseValues): Call = routes.WebController.adminEditExercise(exercise.id)

  override def deleteExerciseRoute(exercise: HasBaseValues): Call = routes.WebController.adminDeleteExercise(exercise.id)

}
