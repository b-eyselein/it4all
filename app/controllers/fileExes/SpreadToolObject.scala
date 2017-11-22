package controllers.fileExes

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


  override def indexCall: Call = controllers.fileExes.routes.SpreadController.index()

  override def exerciseRoute(exercise: HasBaseValues, fileExtension: String): Call = controllers.fileExes.routes.SpreadController.exercise(exercise.id, fileExtension)

  override def exerciseListRoute(page: Int): Call = controllers.fileExes.routes.SpreadController.exerciseList(page)

  override def uploadSolutionRoute(exercise: HasBaseValues, fileExtension: String): Call = controllers.fileExes.routes.SpreadController.uploadSolution(exercise.id, fileExtension)

  override def downloadCorrectedRoute(exercise: HasBaseValues, fileExtension: String): Call = controllers.fileExes.routes.SpreadController.downloadCorrected(exercise.id, fileExtension)


  override val restHeaders: List[String] = List("Musterloesungsdatei", "Vorlagendatei")

  override def adminIndexRoute: Call = controllers.fileExes.routes.SpreadController.adminIndex()

  override def adminExesListRoute: Call = controllers.fileExes.routes.SpreadController.adminExerciseList()

  override def newExFormRoute: Call = controllers.fileExes.routes.SpreadController.adminNewExerciseForm()

  override def importExesRoute: Call = controllers.fileExes.routes.SpreadController.adminImportExercises()

  override def exportExesRoute: Call = controllers.fileExes.routes.SpreadController.adminExportExercises()

  override def exportExesAsFileRoute: Call = controllers.fileExes.routes.SpreadController.adminExportExercisesAsFile()

  override def changeExStateRoute(exercise: HasBaseValues): Call = controllers.fileExes.routes.SpreadController.adminChangeExState(exercise.id)

  override def editExerciseFormRoute(exercise: HasBaseValues): Call = controllers.fileExes.routes.SpreadController.adminEditExerciseForm(exercise.id)

  override def editExerciseRoute(exercise: HasBaseValues): Call = controllers.fileExes.routes.SpreadController.adminEditExercise(exercise.id)

  override def deleteExerciseRoute(exercise: HasBaseValues): Call = controllers.fileExes.routes.SpreadController.adminDeleteExercise(exercise.id)

}
