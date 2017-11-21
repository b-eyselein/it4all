package controllers.fileExes

import model.Enums.ToolState
import model.mindmap.{MindmapConsts, MindmapExercise}
import model.{Consts, HasBaseValues}
import play.api.mvc.Call

object MindMapToolObject extends FileExToolObject {

  override type CompEx = MindmapExercise

  override val fileTypes = List(FileType("mmap", "MindJet MindManager"))

  override val hasTags: Boolean = false
  override val toolname         = "Mindmap"
  override val exType           = "mindmap"
  override val toolState        = ToolState.ALPHA
  override val consts : Consts  = MindmapConsts


  override def indexCall: Call = controllers.fileExes.routes.MindmapController.index()

  override def exerciseRoute(exercise: HasBaseValues, fileType: String): Call = controllers.fileExes.routes.MindmapController.downloadTemplate(exercise.id, fileType)

  override def exerciseListRoute(page: Int): Call = controllers.fileExes.routes.MindmapController.exerciseList(page)

  override def uploadSolutionRoute(exercise: HasBaseValues): Call = controllers.fileExes.routes.MindmapController.uploadSolution(exercise.id)

  override def downloadCorrectedRoute(exercise: HasBaseValues, fileType: String): Call = controllers.fileExes.routes.MindmapController.downloadCorrected(exercise.id, fileType)


  override val restHeaders: List[String] = List.empty

  override def adminIndexRoute: Call = controllers.fileExes.routes.MindmapController.adminIndex()

  override def adminExesListRoute: Call = controllers.fileExes.routes.MindmapController.adminExerciseList()

  override def newExFormRoute: Call = controllers.fileExes.routes.MindmapController.adminNewExerciseForm()

  override def importExesRoute: Call = controllers.fileExes.routes.MindmapController.adminImportExercises()

  override def exportExesRoute: Call = controllers.fileExes.routes.MindmapController.adminExportExercises()

  override def exportExesAsFileRoute: Call = controllers.fileExes.routes.MindmapController.adminExportExercisesAsFile()

  override def changeExStateRoute(exercise: HasBaseValues): Call = controllers.fileExes.routes.MindmapController.adminChangeExState(exercise.id)

  override def editExerciseFormRoute(exercise: HasBaseValues): Call = controllers.fileExes.routes.MindmapController.adminEditExerciseForm(exercise.id)

  override def editExerciseRoute(exercise: HasBaseValues): Call = controllers.fileExes.routes.MindmapController.adminEditExercise(exercise.id)

  override def deleteExerciseRoute(exercise: HasBaseValues): Call = controllers.fileExes.routes.MindmapController.adminDeleteExercise(exercise.id)

}
