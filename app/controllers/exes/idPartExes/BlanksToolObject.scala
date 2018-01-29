package controllers.exes.idPartExes

import model.Enums.ToolState
import model.blanks.BlanksExParts.BlanksExPart
import model.blanks.{BlanksCompleteExercise, BlanksExParts}
import model.core.tools.IdPartExToolObject
import model.{Consts, HasBaseValues}
import play.api.mvc.Call

import scala.language.postfixOps

object BlanksToolObject extends IdPartExToolObject[BlanksExPart] {

  override type CompEx = BlanksCompleteExercise

  override def exParts: Seq[BlanksExPart] = BlanksExParts.values

  override val hasTags  : Boolean   = false
  override val toolname : String    = "Lückentext"
  override val exType   : String    = "blanks"
  override val consts   : Consts    = BlanksConsts
  override val toolState: ToolState = ToolState.ALPHA

  override def indexCall: Call = routes.BlanksController.index()

  override def exerciseRoute(id: Int, part: String): Call = routes.BlanksController.exercise(id, part)

  override def exerciseListRoute(page: Int): Call = routes.BlanksController.exerciseList(page)

  override def correctLiveRoute(id: Int): Call = routes.BlanksController.correctLive(id)

  override def correctRoute(id: Int): Call = routes.BlanksController.correct(id)


  override def adminIndexRoute: Call = routes.BlanksController.adminIndex()

  override def adminExesListRoute: Call = routes.BlanksController.adminExerciseList()

  override def newExFormRoute: Call = routes.BlanksController.adminNewExerciseForm()

  override def createNewExRoute: Call = routes.BlanksController.adminCreateExercise()

  override def importExesRoute: Call = routes.BlanksController.adminImportExercises()

  override def exportExesRoute: Call = routes.BlanksController.adminExportExercises()

  override def exportExesAsFileRoute: Call = routes.BlanksController.adminExportExercisesAsFile()

  override def changeExStateRoute(id: Int): Call = routes.BlanksController.adminChangeExState(id)

  override def editExerciseFormRoute(id: Int): Call = routes.BlanksController.adminEditExerciseForm(id)

  override def editExerciseRoute(id: Int): Call = routes.BlanksController.adminEditExercise(id)

  override def deleteExerciseRoute(id: Int): Call = routes.BlanksController.adminDeleteExercise(id)

}
