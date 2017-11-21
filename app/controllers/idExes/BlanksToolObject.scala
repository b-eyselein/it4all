package controllers.idExes

import model.Enums.ToolState
import model.blanks.{BlanksConsts, BlanksExercise}
import model.{Consts, HasBaseValues}
import play.api.mvc.Call

object BlanksToolObject extends IdExToolObject {

  override type CompEx = BlanksExercise

  override val hasTags  : Boolean   = false
  override val toolname : String    = "Lückentext"
  override val exType   : String    = "blanks"
  override val toolState: ToolState = ToolState.ALPHA
  override val consts   : Consts    = BlanksConsts

  override def indexCall: Call = controllers.idExes.routes.BlanksController.index()

  override def exerciseRoute(exercise: HasBaseValues): Call = controllers.idExes.routes.BlanksController.exercise(exercise.id)

  override def exerciseListRoute(page: Int): Call = controllers.idExes.routes.BlanksController.exerciseList(page)

  override def correctLiveRoute(exercise: HasBaseValues): Call = controllers.idExes.routes.BlanksController.correctLive(exercise.id)

  override def correctRoute(exercise: HasBaseValues): Call = controllers.idExes.routes.BlanksController.correct(exercise.id)


  override val restHeaders: List[String] = List.empty

  override def adminIndexRoute: Call = controllers.idExes.routes.BlanksController.adminIndex()

  override def adminExesListRoute: Call = controllers.idExes.routes.BlanksController.adminExerciseList()

  override def newExFormRoute: Call = controllers.idExes.routes.BlanksController.adminNewExerciseForm()

  override def importExesRoute: Call = controllers.idExes.routes.BlanksController.adminImportExercises()

  override def exportExesRoute: Call = controllers.idExes.routes.BlanksController.adminExportExercises()

  override def exportExesAsFileRoute: Call = controllers.idExes.routes.BlanksController.adminExportExercisesAsFile()

  override def changeExStateRoute(exercise: HasBaseValues): Call = controllers.idExes.routes.BlanksController.adminChangeExState(exercise.id)

  override def editExerciseFormRoute(exercise: HasBaseValues): Call = controllers.idExes.routes.BlanksController.adminEditExerciseForm(exercise.id)

  override def editExerciseRoute(exercise: HasBaseValues): Call = controllers.idExes.routes.BlanksController.adminEditExercise(exercise.id)

  override def deleteExerciseRoute(exercise: HasBaseValues): Call = controllers.idExes.routes.BlanksController.adminDeleteExercise(exercise.id)

}
