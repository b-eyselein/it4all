package controllers.exes

import model.Enums.ToolState
import model.HasBaseValues
import model.core.tools.IdExToolObject
import play.api.mvc.Call

object BlanksToolObject extends IdExToolObject("blanks", "Lueckentext", ToolState.ALPHA) {

  override def indexCall: Call = controllers.exes.routes.BlanksController.index()

  override def exerciseRoute(exercise: HasBaseValues): Call = controllers.exes.routes.BlanksController.exercise(exercise.id)

  // onyl self ref
  override def exesListRoute(page: Int): Call = ??? // controllers.exes.routes.BlanksController.exercises(page)

  override def correctLiveRoute(exercise: HasBaseValues): Call = ??? // controllers.exes.routes.BlanksController.correctLive(exercise.id)

  override def correctRoute(exercise: HasBaseValues): Call = controllers.exes.routes.BlanksController.correct(exercise.id)


  override val restHeaders: List[String] = List.empty

  override def adminIndexRoute: Call = controllers.exes.routes.BlanksController.adminIndex()

  override def adminExesListRoute: Call = controllers.exes.routes.BlanksController.exercises()

  override def newExFormRoute: Call = controllers.exes.routes.BlanksController.newExerciseForm()

  override def importExesRoute: Call = controllers.exes.routes.BlanksController.importExercises()

  override def exportExesRoute: Call = controllers.exes.routes.BlanksController.exportExercises()

  override def exportExesAsFileRoute: Call = controllers.exes.routes.BlanksController.exportExercisesAsFile()

  override def changeExStateRoute(exercise: HasBaseValues): Call = controllers.exes.routes.BlanksController.changeExState(exercise.id)

  override def editExerciseFormRoute(exercise: HasBaseValues): Call = controllers.exes.routes.BlanksController.editExerciseForm(exercise.id)

  override def editExerciseRoute(exercise: HasBaseValues): Call = controllers.exes.routes.BlanksController.editExercise(exercise.id)

  override def deleteExerciseRoute(exercise: HasBaseValues): Call = controllers.exes.routes.BlanksController.deleteExercise(exercise.id)

}
