package controllers.exes.idPartExes

import model.core.tools.IdPartExToolObject
import model.web.{WebCompleteEx, WebConsts, WebExPart, WebExParts}
import model.{Consts, HasBaseValues}
import play.api.mvc.Call

import scala.language.postfixOps

object WebToolObject extends IdPartExToolObject[WebExPart] {

  override type CompEx = WebCompleteEx

  override def exParts: Seq[WebExPart] = WebExParts.values

  override val hasTags : Boolean = true
  override val toolname: String  = "Web"
  override val exType  : String  = "web"
  override val consts  : Consts  = WebConsts

  override def indexCall: Call = routes.WebController.index()

  override def exerciseRoute(id: Int, part: String): Call = routes.WebController.exercise(id, part)

  override def exerciseListRoute(page: Int): Call = routes.WebController.exerciseList(page)

  override def correctLiveRoute(id: Int): Call = routes.WebController.correctLive(id)

  override def correctRoute(id: Int): Call = routes.WebController.correct(id)


  override def adminIndexRoute: Call = routes.WebController.adminIndex()

  override def adminExesListRoute: Call = routes.WebController.adminExerciseList()

  override def newExFormRoute: Call = routes.WebController.adminNewExerciseForm()

  override def createNewExRoute: Call = routes.WebController.adminCreateExercise()

  override def importExesRoute: Call = routes.WebController.adminImportExercises()

  override def exportExesRoute: Call = routes.WebController.adminExportExercises()

  override def exportExesAsFileRoute: Call = routes.WebController.adminExportExercisesAsFile()

  override def changeExStateRoute(id: Int): Call = routes.WebController.adminChangeExState(id)

  override def editExerciseFormRoute(id: Int): Call = routes.WebController.adminEditExerciseForm(id)

  override def editExerciseRoute(id: Int): Call = routes.WebController.adminEditExercise(id)

  override def deleteExerciseRoute(id: Int): Call = routes.WebController.adminDeleteExercise(id)

}
