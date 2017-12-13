package controllers.exes.idPartExes

import model.Enums.ToolState
import model.uml.UmlEnums.UmlExPart
import model.uml.{UmlCompleteEx, UmlConsts}
import model.{Consts, HasBaseValues}
import play.api.mvc.Call

object UmlToolObject extends IdPartExToolObject {

  override type CompEx = UmlCompleteEx

  override def exParts: Map[String, String] = Map(UmlExPart.CLASS_SELECTION.name -> "Mit Zwischenkorrektur", UmlExPart.DIAG_DRAWING.name -> "Freies Erstellen")

  override val hasTags  : Boolean   = false
  override val toolname : String    = "Uml"
  override val exType   : String    = "uml"
  override val consts   : Consts    = UmlConsts
  override val toolState: ToolState = ToolState.BETA

  override def indexCall: Call = routes.UmlController.index()

  override def exerciseRoute(exercise: HasBaseValues, part: String): Call = routes.UmlController.exercise(exercise.id, part)

  override def exerciseListRoute(page: Int): Call = routes.UmlController.exerciseList(page)

  // not in routes...
  override def correctLiveRoute(exercise: HasBaseValues, part: String): Call = ??? // routes.UmlController.correctLive(exercise.id, part)

  override def correctRoute(exercise: HasBaseValues, part: String): Call = routes.UmlController.correct(exercise.id, part)


  override def adminIndexRoute: Call = routes.UmlController.adminIndex()

  override def adminExesListRoute: Call = routes.UmlController.adminExerciseList()

  override def newExFormRoute: Call = routes.UmlController.adminNewExerciseForm()

  override def createNewExRoute: Call = routes.UmlController.adminCreateExercise()

  override def exportExesRoute: Call = routes.UmlController.adminExportExercises()

  override def exportExesAsFileRoute: Call = routes.UmlController.adminExportExercisesAsFile()

  override def importExesRoute: Call = routes.UmlController.adminImportExercises()

  override def changeExStateRoute(exercise: HasBaseValues): Call = routes.UmlController.adminChangeExState(exercise.id)

  override def editExerciseFormRoute(exercise: HasBaseValues): Call = routes.UmlController.adminEditExerciseForm(exercise.id)

  override def editExerciseRoute(exercise: HasBaseValues): Call = routes.UmlController.adminEditExercise(exercise.id)

  override def deleteExerciseRoute(exercise: HasBaseValues): Call = routes.UmlController.adminDeleteExercise(exercise.id)

}
