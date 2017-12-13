package controllers.exes.fileExes

import model.Enums.ToolState
import model.mindmap.{MindmapConsts, MindmapExercise}
import model.{Consts, HasBaseValues}
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

  override def exerciseRoute(exercise: HasBaseValues, fileType: String): Call = routes.MindmapController.downloadTemplate(exercise.id, fileType)

  override def exerciseListRoute(page: Int): Call = routes.MindmapController.exerciseList(page)

  override def uploadSolutionRoute(exercise: HasBaseValues, fileExtension: String): Call = routes.MindmapController.uploadSolution(exercise.id, fileExtension)

  override def downloadCorrectedRoute(exercise: HasBaseValues, fileType: String): Call = routes.MindmapController.downloadCorrected(exercise.id, fileType)


  override def adminIndexRoute: Call = routes.MindmapController.adminIndex()

  override def adminExesListRoute: Call = routes.MindmapController.adminExerciseList()

  override def newExFormRoute: Call = routes.MindmapController.adminNewExerciseForm()

  override def createNewExRoute: Call = routes.MindmapController.adminCreateExercise()

  override def importExesRoute: Call = routes.MindmapController.adminImportExercises()

  override def exportExesRoute: Call = routes.MindmapController.adminExportExercises()

  override def exportExesAsFileRoute: Call = routes.MindmapController.adminExportExercisesAsFile()

  override def changeExStateRoute(exercise: HasBaseValues): Call = routes.MindmapController.adminChangeExState(exercise.id)

  override def editExerciseFormRoute(exercise: HasBaseValues): Call = routes.MindmapController.adminEditExerciseForm(exercise.id)

  override def editExerciseRoute(exercise: HasBaseValues): Call = routes.MindmapController.adminEditExercise(exercise.id)

  override def deleteExerciseRoute(exercise: HasBaseValues): Call = routes.MindmapController.adminDeleteExercise(exercise.id)

}
