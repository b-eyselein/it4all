package controllers.idExes

import model.Enums.ToolState
import model.programming.{ProgCompleteEx, ProgConsts}
import model.{Consts, HasBaseValues}
import play.api.mvc.Call

object ProgToolObject extends IdExToolObject {

  override type CompEx = ProgCompleteEx

  override val hasTags  : Boolean   = false
  override val toolname : String    = "Programmierung"
  override val exType   : String    = "prog"
  override val toolState: ToolState = ToolState.ALPHA
  override val consts   : Consts    = ProgConsts

  override def indexCall: Call = controllers.idExes.routes.ProgController.index()

  override def exerciseRoute(exercise: HasBaseValues): Call = controllers.idExes.routes.ProgController.exercise(exercise.id)

  override def exerciseRoutes(exercise: ProgCompleteEx) = List(
    (controllers.idExes.routes.ProgController.testData(exercise.ex.id), "Testdaten erstellen"),
    (controllers.idExes.routes.ProgController.exercise(exercise.ex.id), "Aufgabe bearbeiten"))

  override def exerciseListRoute(page: Int): Call = controllers.idExes.routes.ProgController.exerciseList(page)

  override def correctLiveRoute(exercise: HasBaseValues): Call = controllers.idExes.routes.ProgController.correctLive(exercise.id)

  override def correctRoute(exercise: HasBaseValues): Call = controllers.idExes.routes.ProgController.correct(exercise.id)


  override val restHeaders: List[String] = List("Funktionsname", "Anzahl Inputs")

  override def adminIndexRoute: Call = controllers.idExes.routes.ProgController.adminIndex()

  override def adminExesListRoute: Call = controllers.idExes.routes.ProgController.adminExerciseList()

  override def newExFormRoute: Call = controllers.idExes.routes.ProgController.adminNewExerciseForm()

  override def importExesRoute: Call = controllers.idExes.routes.ProgController.adminImportExercises()

  override def exportExesRoute: Call = controllers.idExes.routes.ProgController.adminExportExercises()

  override def exportExesAsFileRoute: Call = controllers.idExes.routes.ProgController.adminExportExercisesAsFile()

  override def changeExStateRoute(exercise: HasBaseValues): Call = controllers.idExes.routes.ProgController.adminChangeExState(exercise.id)

  override def editExerciseFormRoute(exercise: HasBaseValues): Call = controllers.idExes.routes.ProgController.adminEditExerciseForm(exercise.id)

  override def editExerciseRoute(exercise: HasBaseValues): Call = controllers.idExes.routes.ProgController.adminEditExercise(exercise.id)

  override def deleteExerciseRoute(exercise: HasBaseValues): Call = controllers.idExes.routes.ProgController.adminDeleteExercise(exercise.id)

}
