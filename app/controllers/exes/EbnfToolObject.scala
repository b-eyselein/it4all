package controllers.exes

import model.Enums.ToolState
import model.HasBaseValues
import model.core.tools.IdExToolObject
import play.api.mvc.Call

object EbnfToolObject extends IdExToolObject("ebnf", "Ebnf", ToolState.ALPHA) {

  override def indexCall: Call = controllers.exes.routes.EbnfController.index()

  override def exerciseRoute(exercise: HasBaseValues): Call = controllers.exes.routes.EbnfController.exercise(exercise.id)

  // only self reference
  override def exesListRoute(page: Int): Call = ??? // controllers.exes.routes.EbnfController.exesListRoute(exercise.id)

  override def correctLiveRoute(exercise: HasBaseValues): Call = ???

  override def correctRoute(exercise: HasBaseValues): Call = ???


  override val restHeaders: List[String] = List("Terminalsymbole")

  override def adminIndexRoute: Call = controllers.exes.routes.EbnfController.adminIndex()

  override def adminExesListRoute: Call = controllers.exes.routes.EbnfController.adminExerciseList()

  override def newExFormRoute: Call = controllers.exes.routes.EbnfController.adminNewExerciseForm()

  override def importExesRoute: Call = controllers.exes.routes.EbnfController.adminImportExercises()

  override def exportExesRoute: Call = controllers.exes.routes.EbnfController.adminExportExercises()

  override def exportExesAsFileRoute: Call = controllers.exes.routes.EbnfController.adminExportExercisesAsFile()

  override def changeExStateRoute(exercise: HasBaseValues): Call = controllers.exes.routes.EbnfController.adminChangeExState(exercise.id)

  override def editExerciseFormRoute(exercise: HasBaseValues): Call = controllers.exes.routes.EbnfController.adminEditExerciseForm(exercise.id)

  override def editExerciseRoute(exercise: HasBaseValues): Call = controllers.exes.routes.EbnfController.adminEditExercise(exercise.id)

  override def deleteExerciseRoute(exercise: HasBaseValues): Call = controllers.exes.routes.EbnfController.adminDeleteExercise(exercise.id)

}
