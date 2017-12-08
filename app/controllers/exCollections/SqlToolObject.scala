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

  override def indexCall: Call = routes.SqlController.index()

  override def exerciseRoute(exercise: SqlCompleteEx): Call = ???

  override def collectionRoute(id: Int, page: Int = 1): Call = routes.SqlController.collection(id, page)

  override def filteredCollectionRoute(id: Int, filter: String, page: Int = 1): Call = routes.SqlController.filteredCollection(id, filter, page)

  override def exerciseListRoute(page: Int): Call = routes.SqlController.collectionList(page)

  override def correctLiveRoute(exercise: HasBaseValues): Call = ???

  override def correctRoute(exercise: HasBaseValues): Call = ???


  override val restHeaders: List[String] = List.empty

  override def adminIndexRoute: Call = routes.SqlController.adminIndex()

  override def adminExesListRoute: Call = routes.SqlController.adminExerciseList()

  override def newExFormRoute: Call = routes.SqlController.adminNewExerciseForm()

  override def importExesRoute: Call = routes.SqlController.adminImportExercises()

  override def exportExesRoute: Call = routes.SqlController.adminExportExercises()

  override def exportExesAsFileRoute: Call = routes.SqlController.adminExportExercisesAsFile()

  override def changeExStateRoute(exercise: HasBaseValues): Call = routes.SqlController.adminChangeExState(exercise.id)

  override def editExerciseFormRoute(exercise: HasBaseValues): Call = routes.SqlController.adminEditExerciseForm(exercise.id)

  override def editExerciseRoute(exercise: HasBaseValues): Call = routes.SqlController.adminEditExercise(exercise.id)

  override def deleteExerciseRoute(exercise: HasBaseValues): Call = routes.SqlController.adminDeleteExercise(exercise.id)

}
