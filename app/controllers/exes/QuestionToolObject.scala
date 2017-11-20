package controllers.exes

import model.Enums.ToolState
import model.HasBaseValues
import model.core.tools.IdExToolObject
import play.api.mvc.Call

object QuestionToolObject extends IdExToolObject("question", "Auswahlfragen", ToolState.BETA) {

  override def indexCall: Call = controllers.exes.routes.QuestionController.index()

  override def exerciseRoute(exercise: HasBaseValues): Call = ???

  // only self reference...
  override def exesListRoute(page: Int): Call = ???

  override def correctLiveRoute(exercise: HasBaseValues): Call = ???

  override def correctRoute(exercise: HasBaseValues): Call = ???


  override val restHeaders: List[String] = List.empty

  override def adminIndexRoute: Call = controllers.exes.routes.QuestionController.adminIndex()

  override def adminExesListRoute: Call = controllers.exes.routes.QuestionController.adminExerciseList()

  override def newExFormRoute: Call = controllers.exes.routes.QuestionController.adminNewExerciseForm()

  override def importExesRoute: Call = controllers.exes.routes.QuestionController.adminImportExercises()

  override def exportExesRoute: Call = controllers.exes.routes.QuestionController.adminExportExercises()

  override def exportExesAsFileRoute: Call = controllers.exes.routes.QuestionController.adminExportExercisesAsFile()

  override def changeExStateRoute(exercise: HasBaseValues): Call = controllers.exes.routes.QuestionController.adminChangeExState(exercise.id)

  override def editExerciseFormRoute(exercise: HasBaseValues): Call = controllers.exes.routes.QuestionController.adminEditExerciseForm(exercise.id)

  override def editExerciseRoute(exercise: HasBaseValues): Call = controllers.exes.routes.QuestionController.adminEditExercise(exercise.id)

  override def deleteExerciseRoute(exercise: HasBaseValues): Call = controllers.exes.routes.QuestionController.adminDeleteExercise(exercise.id)

}
