package controllers.exes.idExes

import model.Enums.ToolState
import model.blanks.{BlanksConsts, BlanksExercise}
import model.{Consts, HasBaseValues}
import play.api.mvc.Call

object BlanksToolObject extends IdExToolObject {

  override type CompEx = BlanksExercise

  override val hasTags  : Boolean   = false
  override val toolname : String    = "Lückentext"
  override val exType   : String    = "blanks"
  override val consts   : Consts    = BlanksConsts
  override val toolState: ToolState = ToolState.ALPHA

  override def indexCall: Call = routes.BlanksController.index()

  override def exerciseRoute(exercise: HasBaseValues): Call = routes.BlanksController.exercise(exercise.id)

  override def exerciseListRoute(page: Int): Call = routes.BlanksController.exerciseList(page)

  override def correctLiveRoute(exercise: HasBaseValues): Call = routes.BlanksController.correctLive(exercise.id)

  override def correctRoute(exercise: HasBaseValues): Call = routes.BlanksController.correct(exercise.id)


  override val restHeaders: List[String] = List.empty

  override def adminIndexRoute: Call = routes.BlanksController.adminIndex()

  override def adminExesListRoute: Call = routes.BlanksController.adminExerciseList()

  override def newExFormRoute: Call = routes.BlanksController.adminNewExerciseForm()

  override def createNewExRoute: Call = routes.BlanksController.adminCreateExercise()

  override def importExesRoute: Call = routes.BlanksController.adminImportExercises()

  override def exportExesRoute: Call = routes.BlanksController.adminExportExercises()

  override def exportExesAsFileRoute: Call = routes.BlanksController.adminExportExercisesAsFile()

  override def changeExStateRoute(exercise: HasBaseValues): Call = routes.BlanksController.adminChangeExState(exercise.id)

  override def editExerciseFormRoute(exercise: HasBaseValues): Call = routes.BlanksController.adminEditExerciseForm(exercise.id)

  override def editExerciseRoute(exercise: HasBaseValues): Call = routes.BlanksController.adminEditExercise(exercise.id)

  override def deleteExerciseRoute(exercise: HasBaseValues): Call = routes.BlanksController.adminDeleteExercise(exercise.id)

}
