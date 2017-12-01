package controllers.exes.idExes

import model.Enums.ToolState
import model.ebnf.{EbnfConsts, EbnfExercise}
import model.{Consts, HasBaseValues}
import play.api.mvc.Call

object EbnfToolObject extends IdExToolObject {

  override type CompEx = EbnfExercise

  override val hasTags  : Boolean   = false
  override val toolname : String    = "Ebnf"
  override val exType   : String    = "ebnf"
  override val toolState: ToolState = ToolState.ALPHA
  override val consts   : Consts    = EbnfConsts

  override def indexCall: Call = controllers.exes.idExes.routes.EbnfController.index()

  override def exerciseRoute(exercise: HasBaseValues): Call = controllers.exes.idExes.routes.EbnfController.exercise(exercise.id)

  override def exerciseListRoute(page: Int): Call = controllers.exes.idExes.routes.EbnfController.exerciseList(page)

  override def correctLiveRoute(exercise: HasBaseValues): Call = ???

  override def correctRoute(exercise: HasBaseValues): Call = ???


  override val restHeaders: List[String] = List("Terminalsymbole")

  override def adminIndexRoute: Call = controllers.exes.idExes.routes.EbnfController.adminIndex()

  override def adminExesListRoute: Call = controllers.exes.idExes.routes.EbnfController.adminExerciseList()

  override def newExFormRoute: Call = controllers.exes.idExes.routes.EbnfController.adminNewExerciseForm()

  override def importExesRoute: Call = controllers.exes.idExes.routes.EbnfController.adminImportExercises()

  override def exportExesRoute: Call = controllers.exes.idExes.routes.EbnfController.adminExportExercises()

  override def exportExesAsFileRoute: Call = controllers.exes.idExes.routes.EbnfController.adminExportExercisesAsFile()

  override def changeExStateRoute(exercise: HasBaseValues): Call = controllers.exes.idExes.routes.EbnfController.adminChangeExState(exercise.id)

  override def editExerciseFormRoute(exercise: HasBaseValues): Call = controllers.exes.idExes.routes.EbnfController.adminEditExerciseForm(exercise.id)

  override def editExerciseRoute(exercise: HasBaseValues): Call = controllers.exes.idExes.routes.EbnfController.adminEditExercise(exercise.id)

  override def deleteExerciseRoute(exercise: HasBaseValues): Call = controllers.exes.idExes.routes.EbnfController.adminDeleteExercise(exercise.id)

}
