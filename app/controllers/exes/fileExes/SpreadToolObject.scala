package controllers.exes.fileExes

import model.Consts
import model.core.tools.FileExToolObject
import model.spread.{SpreadConsts, SpreadExercise}
import play.api.mvc.Call

object SpreadToolObject extends FileExToolObject {

  override type CompEx = SpreadExercise

  override val fileTypes: Map[String, String] = Map("xlsx" -> "MS Excel", "ods" -> "OpenOffice")

  override val hasTags : Boolean = false
  override val toolname: String  = "Tabellenkalkulation"
  override val exType  : String  = "spread"
  override val consts  : Consts  = SpreadConsts


  override def indexCall: Call = routes.SpreadController.index()

  override def exerciseRoute(id: Int, fileExtension: String): Call = routes.SpreadController.exercise(id, fileExtension)

  override def exerciseListRoute(page: Int): Call = routes.SpreadController.exerciseList(page)

  override def uploadSolutionRoute(id: Int, fileExtension: String): Call = routes.SpreadController.uploadSolution(id, fileExtension)

  override def downloadCorrectedRoute(id: Int, fileExtension: String): Call = routes.SpreadController.downloadCorrected(id, fileExtension)


  override def adminIndexRoute: Call = routes.SpreadController.adminIndex()

  override def adminExesListRoute: Call = routes.SpreadController.adminExerciseList()

  override def newExFormRoute: Call = routes.SpreadController.adminNewExerciseForm()

  override def createNewExRoute: Call = routes.SpreadController.adminCreateExercise()

  override def importExesRoute: Call = routes.SpreadController.adminImportExercises()

  override def exportExesRoute: Call = routes.SpreadController.adminExportExercises()

  override def exportExesAsFileRoute: Call = routes.SpreadController.adminExportExercisesAsFile()

  override def changeExStateRoute(id: Int): Call = routes.SpreadController.adminChangeExState(id)

  override def editExerciseFormRoute(id: Int): Call = routes.SpreadController.adminEditExerciseForm(id)

  override def editExerciseRoute(id: Int): Call = routes.SpreadController.adminEditExercise(id)

  override def deleteExerciseRoute(id: Int): Call = routes.SpreadController.adminDeleteExercise(id)

  override def correctRoute(id: Int): Call = ???

  override def correctLiveRoute(id: Int): Call = ???
}
