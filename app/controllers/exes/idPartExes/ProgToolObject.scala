package controllers.exes.idPartExes

import model.Enums.ToolState
import model.programming.ProgEnums.ProgExPart
import model.programming.{ProgCompleteEx, ProgConsts}
import model.{Consts, HasBaseValues}
import play.api.mvc.Call

object ProgToolObject extends IdPartExToolObject {

  override def exParts: Map[String, String] = ProgExPart.values().map(part => part.name.toLowerCase -> part.partName).toMap

  override type CompEx = ProgCompleteEx

  override val hasTags  : Boolean   = false
  override val toolname : String    = "Programmierung"
  override val exType   : String    = "prog"
  override val consts   : Consts    = ProgConsts
  override val toolState: ToolState = ToolState.BETA

  override def indexCall: Call = routes.ProgController.index()

  override def exerciseRoute(exercise: HasBaseValues, part: String): Call = routes.ProgController.exercise(exercise.id, part)

  //  override def exerciseRoutes(exercise: ProgCompleteEx) = Map(
  //    routes.ProgController.exercise(exercise.ex.id, ProgExPart.TESTDATA_CREATION.name) -> "Testdaten erstellen",
  //    routes.ProgController.exercise(exercise.ex.id, ProgExPart.IMPLEMENTATION.name) -> "Aufgabe bearbeiten"
  //  )

  override def exerciseListRoute(page: Int): Call = routes.ProgController.exerciseList(page)

  override def correctLiveRoute(exercise: HasBaseValues, part: String): Call = routes.ProgController.correctLive(exercise.id, part)

  override def correctRoute(exercise: HasBaseValues, part: String): Call = routes.ProgController.correct(exercise.id, part)


  override def adminIndexRoute: Call = routes.ProgController.adminIndex()

  override def adminExesListRoute: Call = routes.ProgController.adminExerciseList()

  override def newExFormRoute: Call = routes.ProgController.adminNewExerciseForm()

  override def createNewExRoute: Call = routes.ProgController.adminCreateExercise()

  override def importExesRoute: Call = routes.ProgController.adminImportExercises()

  override def exportExesRoute: Call = routes.ProgController.adminExportExercises()

  override def exportExesAsFileRoute: Call = routes.ProgController.adminExportExercisesAsFile()

  override def changeExStateRoute(exercise: HasBaseValues): Call = routes.ProgController.adminChangeExState(exercise.id)

  override def editExerciseFormRoute(exercise: HasBaseValues): Call = routes.ProgController.adminEditExerciseForm(exercise.id)

  override def editExerciseRoute(exercise: HasBaseValues): Call = routes.ProgController.adminEditExercise(exercise.id)

  override def deleteExerciseRoute(exercise: HasBaseValues): Call = routes.ProgController.adminDeleteExercise(exercise.id)

}
