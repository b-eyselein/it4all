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

  override def adminExesListRoute: Call = ??? // controllers.exes.routes.MindmapController.exercises()

  override def newExFormRoute: Call = ??? //controllers.exes.routes.MindmapController.newExerciseForm()

  override def importExesRoute: Call = controllers.exes.routes.MindmapController.importExercises()

  override def exportExesRoute: Call = controllers.exes.routes.MindmapController.exportExercises()

  override def exportExesAsFileRoute: Call = controllers.exes.routes.MindmapController.exportExercisesAsFile()

  override def changeExStateRoute(exercise: HasBaseValues): Call = ??? //controllers.exes.routes.MindmapController.changeExState(exercise.id)

  override def editExerciseFormRoute(exercise: HasBaseValues): Call = ??? //controllers.exes.routes.MindmapController.editExerciseForm(exercise.id)

  override def editExerciseRoute(exercise: HasBaseValues): Call = ??? //controllers.exes.routes.MindmapController.editExercise(exercise.id)

  override def deleteExerciseRoute(exercise: HasBaseValues): Call = ??? //controllers.exes.routes.MindmapController.deleteExercise(exercise.id)


}
