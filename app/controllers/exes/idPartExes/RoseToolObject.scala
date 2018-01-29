package controllers.exes.idPartExes

import model.Enums.ToolState
import model.{Consts, Enums}
import model.core.tools.IdPartExToolObject
import model.rose.{RoseCompleteEx, RoseConsts, RoseExPart, RoseExParts}
import play.api.mvc.Call

object RoseToolObject extends IdPartExToolObject[RoseExPart] {

  override type CompEx = RoseCompleteEx

  override val hasTags  : Boolean         = true
  override val toolname : String          = "Rose"
  override val exType   : String          = "rose"
  override val consts   : Consts          = RoseConsts
  override val toolState: Enums.ToolState = ToolState.ALPHA


  override def exParts: Seq[RoseExPart] = RoseExParts.values

  override def exerciseRoute(id: Int, part: String): Call = routes.RoseController.exercise(id, part)

  override def exerciseListRoute(page: Int): Call = routes.RoseController.exerciseList(page)

  override def correctRoute(id: Int): Call = routes.RoseController.correct(id)

  override def correctLiveRoute(id: Int): Call = routes.RoseController.correctLive(id)

  override def adminIndexRoute: Call = routes.RoseController.adminIndex()

  override def adminExesListRoute: Call = routes.RoseController.adminExerciseList()

  override def newExFormRoute: Call = routes.RoseController.adminNewExerciseForm()

  override def createNewExRoute: Call = routes.RoseController.adminCreateExercise()

  override def exportExesRoute: Call = routes.RoseController.adminExportExercises()

  override def exportExesAsFileRoute: Call = routes.RoseController.adminExportExercisesAsFile()

  override def importExesRoute: Call = routes.RoseController.adminImportExercises()

  override def changeExStateRoute(id: Int): Call = routes.RoseController.adminChangeExState(id)

  override def editExerciseFormRoute(id: Int): Call = routes.RoseController.adminEditExerciseForm(id)

  override def editExerciseRoute(id: Int): Call = routes.RoseController.adminEditExercise(id)

  override def deleteExerciseRoute(id: Int): Call = routes.RoseController.adminDeleteExercise(id)

  override def indexCall: Call = routes.RoseController.index()

}
