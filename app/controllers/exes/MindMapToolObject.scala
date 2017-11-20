package controllers.exes

import model.Enums.ToolState
import model.HasBaseValues
import model.core.tools.IdExToolObject
import play.api.mvc.Call

object MindMapToolObject extends IdExToolObject("mindmap", "Mindmap", ToolState.LIVE) {

  // User
  override def indexCall: Call = controllers.exes.routes.MindmapController.index()

  override def exerciseRoute(exercise: HasBaseValues): Call = ??? // controllers.exes.routes.MindmapController.exercise(exercise.id)

  override def exesListRoute(page: Int): Call = ??? //controllers.exes.routes.MindmapController.exercises(exercise.id)

  override def correctLiveRoute(exercise: HasBaseValues): Call = ??? // controllers.exes.routes.MindmapController.correctLive(exercise.id)

  override def correctRoute(exercise: HasBaseValues): Call = ??? //controllers.exes.routes.MindmapController.correct(exercise.id)

  // Admin

  override val restHeaders: List[String] = List.empty

  override def adminIndexRoute: Call = controllers.exes.routes.MindmapController.adminIndex()

  override def adminExesListRoute: Call = ??? // controllers.exes.routes.MindmapController.adminExerciseList()

  override def newExFormRoute: Call = ??? //controllers.exes.routes.MindmapController.adminNewExerciseForm()

  override def importExesRoute: Call = controllers.exes.routes.MindmapController.adminImportExercises()

  override def exportExesRoute: Call = controllers.exes.routes.MindmapController.adminExportExercises()

  override def exportExesAsFileRoute: Call = controllers.exes.routes.MindmapController.adminExportExercisesAsFile()

  override def changeExStateRoute(exercise: HasBaseValues): Call = ??? //controllers.exes.routes.MindmapController.adminChangeExState(exercise.id)

  override def editExerciseFormRoute(exercise: HasBaseValues): Call = ??? //controllers.exes.routes.MindmapController.adminEditExerciseForm(exercise.id)

  override def editExerciseRoute(exercise: HasBaseValues): Call = ??? //controllers.exes.routes.MindmapController.adminEditExercise(exercise.id)

  override def deleteExerciseRoute(exercise: HasBaseValues): Call = ??? //controllers.exes.routes.MindmapController.adminDeleteExercise(exercise.id)


}
