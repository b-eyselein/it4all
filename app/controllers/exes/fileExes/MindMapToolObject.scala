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


  override def indexCall: Call = controllers.exes.fileExes.routes.MindmapController.index()

  override def exerciseRoute(exercise: HasBaseValues, fileType: String): Call = controllers.exes.fileExes.routes.MindmapController.downloadTemplate(exercise.id, fileType)

  override def exerciseListRoute(page: Int): Call = controllers.exes.fileExes.routes.MindmapController.exerciseList(page)

  override def uploadSolutionRoute(exercise: HasBaseValues, fileExtension: String): Call = controllers.exes.fileExes.routes.MindmapController.uploadSolution(exercise.id, fileExtension)

  override def downloadCorrectedRoute(exercise: HasBaseValues, fileType: String): Call = controllers.exes.fileExes.routes.MindmapController.downloadCorrected(exercise.id, fileType)


  override val restHeaders: List[String] = List.empty

  override def adminIndexRoute: Call = controllers.exes.fileExes.routes.MindmapController.adminIndex()

  override def adminExesListRoute: Call = controllers.exes.fileExes.routes.MindmapController.adminExerciseList()

  override def newExFormRoute: Call = controllers.exes.fileExes.routes.MindmapController.adminNewExerciseForm()

  override def importExesRoute: Call = controllers.exes.fileExes.routes.MindmapController.adminImportExercises()

  override def exportExesRoute: Call = controllers.exes.fileExes.routes.MindmapController.adminExportExercises()

  override def exportExesAsFileRoute: Call = controllers.exes.fileExes.routes.MindmapController.adminExportExercisesAsFile()

  override def changeExStateRoute(exercise: HasBaseValues): Call = controllers.exes.fileExes.routes.MindmapController.adminChangeExState(exercise.id)

  override def editExerciseFormRoute(exercise: HasBaseValues): Call = controllers.exes.fileExes.routes.MindmapController.adminEditExerciseForm(exercise.id)

  override def editExerciseRoute(exercise: HasBaseValues): Call = controllers.exes.fileExes.routes.MindmapController.adminEditExercise(exercise.id)

  override def deleteExerciseRoute(exercise: HasBaseValues): Call = controllers.exes.fileExes.routes.MindmapController.adminDeleteExercise(exercise.id)

}
