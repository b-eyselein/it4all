package controllers.exes.fileExes

import model.Consts
import model.Enums.ToolState
import model.core.tools.FileExToolObject
import model.mindmap.{MindmapConsts, MindmapExercise}
import play.api.mvc.Call

object MindMapToolObject extends FileExToolObject {

  override type CompEx = MindmapExercise

  override val fileTypes: Map[String, String] = Map("mmap" -> "MindJet MindManager")

  override val hasTags  : Boolean   = false
  override val toolname : String    = "Mindmap"
  override val exType   : String    = "mindmap"
  override val toolState: ToolState = ToolState.ALPHA
  override val consts   : Consts    = MindmapConsts


  override def indexCall: Call = routes.MindmapController.index()

  override def exerciseRoute(id: Int, fileType: String): Call = routes.MindmapController.downloadTemplate(id, fileType)

  override def exerciseListRoute(page: Int): Call = routes.MindmapController.exerciseList(page)

  override def uploadSolutionRoute(id: Int, fileExtension: String): Call = routes.MindmapController.uploadSolution(id, fileExtension)

  override def downloadCorrectedRoute(id: Int, fileType: String): Call = routes.MindmapController.downloadCorrected(id, fileType)


  override def adminIndexRoute: Call = routes.MindmapController.adminIndex()

  override def adminExesListRoute: Call = routes.MindmapController.adminExerciseList()

  override def newExFormRoute: Call = routes.MindmapController.adminNewExerciseForm()

  override def createNewExRoute: Call = routes.MindmapController.adminCreateExercise()

  override def importExesRoute: Call = routes.MindmapController.adminImportExercises()

  override def exportExesRoute: Call = routes.MindmapController.adminExportExercises()

  override def exportExesAsFileRoute: Call = routes.MindmapController.adminExportExercisesAsFile()

  override def changeExStateRoute(id: Int): Call = routes.MindmapController.adminChangeExState(id)

  override def editExerciseFormRoute(id: Int): Call = routes.MindmapController.adminEditExerciseForm(id)

  override def editExerciseRoute(id: Int): Call = routes.MindmapController.adminEditExercise(id)

  override def deleteExerciseRoute(id: Int): Call = routes.MindmapController.adminDeleteExercise(id)

  override def correctRoute(id: Int): Call = ???

  override def correctLiveRoute(id: Int): Call = ???
}
