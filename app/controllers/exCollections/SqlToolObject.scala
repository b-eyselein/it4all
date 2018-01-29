package controllers.exCollections

import model.Consts
import model.core.tools.CollectionToolObject
import model.sql.{SqlCompleteEx, SqlConsts}
import play.api.mvc.Call


object SqlToolObject extends CollectionToolObject {

  override type CompEx = SqlCompleteEx

  override val hasExType             : Boolean = true
  override val hasTags               : Boolean = true
  override val toolname              : String  = "Sql"
  override val exType                : String  = "sql"
  override val collectionSingularName: String  = "Szenario"
  override val collectionPluralName  : String  = "Szenarien"
  override val consts                : Consts  = SqlConsts

  override def indexCall: Call = routes.SqlController.index()

  override def exerciseRoute(collectionId: Int, exerciseId: Int): Call = routes.SqlController.exercise(collectionId, exerciseId)

  override def collectionRoute(id: Int, page: Int = 1): Call = routes.SqlController.collection(id, page)

  override def filteredCollectionRoute(id: Int, filter: String, page: Int = 1): Call = routes.SqlController.filteredCollection(id, filter, page)

  override def exerciseListRoute(page: Int): Call = routes.SqlController.collectionList(page)

  override def correctLiveRoute(id: Int): Call = ???

  override def correctRoute(id: Int): Call = ???


  override def adminIndexRoute: Call = routes.SqlController.adminIndex()

  override def adminExesListRoute: Call = routes.SqlController.adminCollectionsList()

  override def newExFormRoute: Call = routes.SqlController.adminNewCollectionForm()

  override def createNewExRoute: Call = routes.SqlController.adminCreateCollection()

  override def importExesRoute: Call = routes.SqlController.adminImportCollections()

  override def exportExesRoute: Call = routes.SqlController.adminExportCollections()

  override def exportExesAsFileRoute: Call = routes.SqlController.adminExportCollectionsAsFile()

  override def changeExStateRoute(id: Int): Call = routes.SqlController.adminChangeCollectionState(id)

  override def editExerciseFormRoute(id: Int): Call = routes.SqlController.adminEditCollectionForm(id)

  override def editExerciseRoute(id: Int): Call = routes.SqlController.adminEditCollection(id)

  override def deleteExerciseRoute(id: Int): Call = routes.SqlController.adminDeleteCollection(id)

}
