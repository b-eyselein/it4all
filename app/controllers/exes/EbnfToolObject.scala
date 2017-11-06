package controllers.exes

import model.Enums.ToolState
import model.core.HasBaseValues
import model.core.tools.IdExToolObject
import play.api.mvc.Call

object EbnfToolObject extends IdExToolObject("ebnf", "Ebnf", ToolState.ALPHA) {

  // User

  override def indexCall: Call = controllers.exes.routes.EbnfController.index()

  override def exerciseRoute(exercise: HasBaseValues): Call = controllers.exes.routes.EbnfController.exercise(exercise.id)

  override def exesListRoute(page: Int): Call = ??? // controllers.exes.routes.EbnfController.exesListRoute(exercise.id)

  override def correctLiveRoute(exercise: HasBaseValues): Call = ???

  override def correctRoute(exercise: HasBaseValues): Call = ???

  // Admin

  val restHeaders: List[String] = List("Terminalsymbole")

  override def adminIndexRoute: Call = controllers.exes.routes.EbnfController.adminIndex()

  override def adminExesListRoute: Call = controllers.exes.routes.EbnfController.exercises()

  override def newExFormRoute: Call = controllers.exes.routes.EbnfController.newExerciseForm()

  override def exportExesRoute: Call = controllers.exes.routes.EbnfController.exportExercises()

  override def importExesRoute: Call = controllers.exes.routes.EbnfController.importExercises()

  //  override def jsonSchemaRoute: Call = controllers.exes.routes.EbnfController.getJSONSchemaFile()

  //  override def uploadFileRoute: Call = controllers.exes.routes.EbnfController.uploadFile()

  override def changeExStateRoute(exercise: HasBaseValues): Call = controllers.exes.routes.EbnfController.changeExState(exercise.id)

  override def editExerciseFormRoute(exercise: HasBaseValues): Call = controllers.exes.routes.EbnfController.editExerciseForm(exercise.id)

  override def editExerciseRoute(exercise: HasBaseValues): Call = controllers.exes.routes.EbnfController.editExercise(exercise.id)

  override def deleteExerciseRoute(exercise: HasBaseValues): Call = controllers.exes.routes.EbnfController.deleteExercise(exercise.id)

}
