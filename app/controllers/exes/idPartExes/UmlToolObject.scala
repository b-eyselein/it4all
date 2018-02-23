package controllers.exes.idPartExes

import model.Enums.ToolState
import model.core.tools.IdPartExToolObject
import model.uml.{UmlCompleteEx, UmlConsts, UmlExPart, UmlExParts}
import model.{Consts, HasBaseValues}
import play.api.mvc.Call

object UmlToolObject extends IdPartExToolObject[UmlExPart] {

  override type CompEx = UmlCompleteEx

  override def exParts: Seq[UmlExPart] = UmlExParts.values

  override val toolname : String    = "Uml"
  override val exType   : String    = "uml"
  override val consts   : Consts    = UmlConsts
  override val toolState: ToolState = ToolState.BETA

  override def indexCall: Call = routes.UmlController.index()

  override def exerciseRoute(id: Int, part: String): Call = routes.UmlController.exercise(id, part)

  override def exerciseListRoute(page: Int): Call = routes.UmlController.exerciseList(page)

  // not in routes...
  override def correctLiveRoute(id: Int): Call = ??? // routes.UmlController.correctLive(id, part)

  override def correctRoute(id: Int): Call = routes.UmlController.correct(id)


  override def adminIndexRoute: Call = routes.UmlController.adminIndex()

  override def adminExesListRoute: Call = routes.UmlController.adminExerciseList()

  override def newExFormRoute: Call = routes.UmlController.adminNewExerciseForm()

  override def createNewExRoute: Call = routes.UmlController.adminCreateExercise()

  override def exportExesRoute: Call = routes.UmlController.adminExportExercises()

  override def exportExesAsFileRoute: Call = routes.UmlController.adminExportExercisesAsFile()

  override def importExesRoute: Call = routes.UmlController.adminImportExercises()

  override def changeExStateRoute(id: Int): Call = routes.UmlController.adminChangeExState(id)

  override def editExerciseFormRoute(id: Int): Call = routes.UmlController.adminEditExerciseForm(id)

  override def editExerciseRoute(id: Int): Call = routes.UmlController.adminEditExercise(id)

  override def deleteExerciseRoute(id: Int): Call = routes.UmlController.adminDeleteExercise(id)

}
