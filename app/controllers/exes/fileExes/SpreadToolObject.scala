package controllers.exes.fileExes

import model.spread.{SpreadConsts, SpreadExercise}
import model.{Consts, HasBaseValues}
import play.api.mvc.Call


object SpreadToolObject extends FileExToolObject {

  override type CompEx = SpreadExercise

  override val fileTypes: Map[String, String] = Map("xlsx" -> "MS Excel", "ods" -> "OpenOffice")

  override val hasTags : Boolean = false
  override val toolname: String  = "Tabellenkalkulation"
  override val exType  : String  = "spread"
  override val consts  : Consts  = SpreadConsts


  override def indexCall: Call = routes.SpreadController.index()

  override def exerciseRoute(exercise: HasBaseValues, fileExtension: String): Call = routes.SpreadController.exercise(exercise.id, fileExtension)

  override def exerciseListRoute(page: Int): Call = routes.SpreadController.exerciseList(page)

  override def uploadSolutionRoute(exercise: HasBaseValues, fileExtension: String): Call = routes.SpreadController.uploadSolution(exercise.id, fileExtension)

  override def downloadCorrectedRoute(exercise: HasBaseValues, fileExtension: String): Call = routes.SpreadController.downloadCorrected(exercise.id, fileExtension)


  override def adminIndexRoute: Call = routes.SpreadController.adminIndex()

  override def adminExesListRoute: Call = routes.SpreadController.adminExerciseList()

  override def newExFormRoute: Call = routes.SpreadController.adminNewExerciseForm()

  override def createNewExRoute: Call = routes.SpreadController.adminCreateExercise()

  override def importExesRoute: Call = routes.SpreadController.adminImportExercises()

  override def exportExesRoute: Call = routes.SpreadController.adminExportExercises()

  override def exportExesAsFileRoute: Call = routes.SpreadController.adminExportExercisesAsFile()

  override def changeExStateRoute(exercise: HasBaseValues): Call = routes.SpreadController.adminChangeExState(exercise.id)

  override def editExerciseFormRoute(exercise: HasBaseValues): Call = routes.SpreadController.adminEditExerciseForm(exercise.id)

  override def editExerciseRoute(exercise: HasBaseValues): Call = routes.SpreadController.adminEditExercise(exercise.id)

  override def deleteExerciseRoute(exercise: HasBaseValues): Call = routes.SpreadController.adminDeleteExercise(exercise.id)

}
