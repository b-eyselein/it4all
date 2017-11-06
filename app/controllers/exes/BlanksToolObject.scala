package controllers.exes

import model.Enums.ToolState
import model.core.HasBaseValues
import model.core.tools.IdExToolObject
import play.api.mvc.Call

object BlanksToolObject extends IdExToolObject("blanks", "Lueckentext", ToolState.ALPHA) {

  // User
  override def indexCall: Call = controllers.exes.routes.BlanksController.index()

  override def exerciseRoute(exercise: HasBaseValues): Call = ??? // controllers.exes.routes.BlanksController.exercise(exercise.id)

  override def exesListRoute(page: Int): Call = ??? //controllers.exes.routes.BlanksController.exercises(exercise.id)

  override def correctLiveRoute(exercise: HasBaseValues): Call = ??? // controllers.exes.routes.BlanksController.correctLive(exercise.id)

  override def correctRoute(exercise: HasBaseValues): Call = ??? //controllers.exes.routes.BlanksController.correct(exercise.id)

  // Admin

  val restHeaders: List[String] = List.empty

  override def adminIndexRoute: Call = controllers.exes.routes.BlanksController.adminIndex()

  override def adminExesListRoute: Call = ??? // controllers.exes.routes.BlanksController.exercises()

  override def newExFormRoute: Call = ??? //controllers.exes.routes.BlanksController.newExerciseForm()

  override def exportExesRoute: Call = ??? //controllers.exes.routes.BlanksController.exportExercises()

  override def importExesRoute: Call = ??? //controllers.exes.routes.BlanksController.importExercises()

  //  override def jsonSchemaRoute: Call = ??? //controllers.exes.routes.BlanksController.getJSONSchemaFile()

  //  override def uploadFileRoute: Call = ??? //controllers.exes.routes.BlanksController.uploadFile()

  override def changeExStateRoute(exercise: HasBaseValues): Call = ??? //controllers.exes.routes.BlanksController.changeExState(exercise.id)

  override def editExerciseFormRoute(exercise: HasBaseValues): Call = ??? //controllers.exes.routes.BlanksController.editExerciseForm(exercise.id)

  override def editExerciseRoute(exercise: HasBaseValues): Call = ??? //controllers.exes.routes.BlanksController.editExercise(exercise.id)

  override def deleteExerciseRoute(exercise: HasBaseValues): Call = ??? //controllers.exes.routes.BlanksController.deleteExercise(exercise.id)

}
