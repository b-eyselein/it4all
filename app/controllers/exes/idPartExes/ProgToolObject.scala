package controllers.exes.idPartExes

import model.Enums.ToolState
import model.core.tools.IdPartExToolObject
import model.programming.{ProgCompleteEx, ProgConsts, ProgExPart, ProgExParts}
import model.{Consts, HasBaseValues}
import play.api.mvc.Call

object ProgToolObject extends IdPartExToolObject[ProgExPart] {

  override type CompEx = ProgCompleteEx

  override def exParts: Seq[ProgExPart] = ProgExParts.values

  override val toolname : String    = "Programmierung"
  override val exType   : String    = "prog"
  override val consts   : Consts    = ProgConsts
  override val toolState: ToolState = ToolState.BETA

  override def indexCall: Call = routes.ProgController.index()

  override def exerciseRoute(id: Int, part: String): Call = routes.ProgController.exercise(id, part)

  override def exerciseListRoute(page: Int): Call = routes.ProgController.exerciseList(page)

  override def correctLiveRoute(id: Int): Call = routes.ProgController.correctLive(id)

  override def correctRoute(id: Int): Call = routes.ProgController.correct(id)


  override def adminIndexRoute: Call = routes.ProgController.adminIndex()

  override def adminExesListRoute: Call = routes.ProgController.adminExerciseList()

  override def newExFormRoute: Call = routes.ProgController.adminNewExerciseForm()

  override def createNewExRoute: Call = routes.ProgController.adminCreateExercise()

  override def importExesRoute: Call = routes.ProgController.adminImportExercises()

  override def exportExesRoute: Call = routes.ProgController.adminExportExercises()

  override def exportExesAsFileRoute: Call = routes.ProgController.adminExportExercisesAsFile()

  override def changeExStateRoute(id: Int): Call = routes.ProgController.adminChangeExState(id)

  override def editExerciseFormRoute(id: Int): Call = routes.ProgController.adminEditExerciseForm(id)

  override def editExerciseRoute(id: Int): Call = routes.ProgController.adminEditExercise(id)

  override def deleteExerciseRoute(id: Int): Call = routes.ProgController.adminDeleteExercise(id)

}
