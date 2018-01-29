package controllers.exes.idPartExes

import model.Enums.ToolState
import model.core.tools.IdPartExToolObject
import model.programming.{ProgCompleteEx, ProgConsts, ProgExPart, ProgExParts}
import model.{Consts, HasBaseValues}
import play.api.mvc.Call

object ProgToolObject extends IdPartExToolObject[ProgExPart] {

  override type CompEx = ProgCompleteEx

  override def exParts: Seq[ProgExPart] = ProgExParts.values

  override val hasTags  : Boolean   = false
  override val toolname : String    = "Programmierung"
  override val exType   : String    = "prog"
  override val consts   : Consts    = ProgConsts
  override val toolState: ToolState = ToolState.BETA

  override def indexCall: Call = routes.ProgController.index()

  override def exerciseRoute(exercise: HasBaseValues, part: String): Call = routes.ProgController.exercise(exercise.id, part)

  override def exerciseListRoute(page: Int): Call = routes.ProgController.exerciseList(page)

  override def correctLiveRoute(exercise: HasBaseValues): Call = routes.ProgController.correctLive(exercise.id)

  override def correctRoute(exercise: HasBaseValues): Call = routes.ProgController.correct(exercise.id)


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
