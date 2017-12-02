package controllers.exCollections

import model.Enums.ToolState
import model.sql.{SqlCompleteEx, SqlConsts}
import model.{Consts, HasBaseValues}
import play.api.mvc.Call

object SqlToolObject extends CollectionToolObject {

  override val collectionName: String = "Szenario"

  override type CompEx = SqlCompleteEx

  override val hasTags   : Boolean   = true
  override val toolname  : String    = "Sql"
  override val exType    : String    = "sql"
  override val pluralName: String    = "Szenarien"
  override val toolState : ToolState = ToolState.ALPHA
  override val consts    : Consts    = SqlConsts

  override def indexCall: Call = controllers.exCollections.routes.SqlController.index()

  override def exerciseRoute(exercise: SqlCompleteEx): Call = ???

  override def exerciseListRoute(page: Int): Call = controllers.exCollections.routes.SqlController.exerciseList(page)

  override def correctLiveRoute(exercise: HasBaseValues): Call = ???

  override def correctRoute(exercise: HasBaseValues): Call = ???


  override val restHeaders: List[String] = List.empty

  override def adminIndexRoute: Call = controllers.exCollections.routes.SqlController.adminIndex()

  override def adminExesListRoute: Call = controllers.exCollections.routes.SqlController.adminExerciseList()

  override def newExFormRoute: Call = controllers.exCollections.routes.SqlController.adminNewExerciseForm()

  override def importExesRoute: Call = controllers.exCollections.routes.SqlController.adminImportExercises()

  override def exportExesRoute: Call = controllers.exCollections.routes.SqlController.adminExportExercises()

  override def exportExesAsFileRoute: Call = controllers.exCollections.routes.SqlController.adminExportExercisesAsFile()

  override def changeExStateRoute(exercise: HasBaseValues): Call = controllers.exCollections.routes.SqlController.adminChangeExState(exercise.id)

  override def editExerciseFormRoute(exercise: HasBaseValues): Call = controllers.exCollections.routes.SqlController.adminEditExerciseForm(exercise.id)

  override def editExerciseRoute(exercise: HasBaseValues): Call = controllers.exCollections.routes.SqlController.adminEditExercise(exercise.id)

  override def deleteExerciseRoute(exercise: HasBaseValues): Call = controllers.exCollections.routes.SqlController.adminDeleteExercise(exercise.id)

}
