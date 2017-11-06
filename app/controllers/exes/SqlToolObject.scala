package controllers.exes

import model.Enums.ToolState
import model.core.HasBaseValues
import model.core.tools.IdExToolObject
import play.api.mvc.Call

object SqlToolObject extends IdExToolObject("sql", "Sql", ToolState.LIVE, "Szenarien") {

  // User

  override def indexCall: Call = controllers.exes.routes.SqlController.index()

  override def exerciseRoute(exercise: HasBaseValues): Call = ???

  override def exesListRoute(page: Int): Call = ??? // controllers.exes.routes.SqlAdmin.exesListRoute(exercise.id)

  override def correctLiveRoute(exercise: HasBaseValues): Call = ???

  override def correctRoute(exercise: HasBaseValues): Call = ???

  // Admin

  val restHeaders: List[String] = List.empty

  override def adminIndexRoute: Call = controllers.exes.routes.SqlController.adminIndex()

  override def adminExesListRoute: Call = controllers.exes.routes.SqlController.exercises()

  override def newExFormRoute: Call = controllers.exes.routes.SqlController.newExerciseForm()

  //  override def exportExesRoute: Call = controllers.exes.routes.SqlController.exportExercises()

  //  override def importExesRoute: Call = controllers.exes.routes.SqlController.importExercises()

  //  override def jsonSchemaRoute: Call = controllers.exes.routes.SqlController.getJSONSchemaFile()

  //  override def uploadFileRoute: Call = controllers.exes.routes.SqlController.uploadFile()

  override def changeExStateRoute(exercise: HasBaseValues): Call = controllers.exes.routes.SqlController.changeExState(exercise.id)

  override def editExerciseFormRoute(exercise: HasBaseValues): Call = controllers.exes.routes.SqlController.editExerciseForm(exercise.id)

  override def editExerciseRoute(exercise: HasBaseValues): Call = controllers.exes.routes.SqlController.editExercise(exercise.id)

  override def deleteExerciseRoute(exercise: HasBaseValues): Call = controllers.exes.routes.SqlController.deleteExercise(exercise.id)

}
