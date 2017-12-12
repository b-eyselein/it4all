package controllers.exCollections

import model.sql.{SqlCompleteEx, SqlConsts}
import model.{Consts, HasBaseValues}
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

  override def correctLiveRoute(collection: HasBaseValues): Call = ???

  override def correctRoute(collection: HasBaseValues): Call = ???


  override val restHeaders: List[String] = List.empty

  override def adminIndexRoute: Call = routes.SqlController.adminIndex()

  override def adminExesListRoute: Call = routes.SqlController.adminCollectionsList()

  override def newExFormRoute: Call = routes.SqlController.adminNewCollectionForm()

  override def createNewExRoute: Call = routes.SqlController.adminCreateCollection()

  override def importExesRoute: Call = routes.SqlController.adminImportCollections()

  override def exportExesRoute: Call = routes.SqlController.adminExportCollections()

  override def exportExesAsFileRoute: Call = routes.SqlController.adminExportCollectionsAsFile()

  override def changeExStateRoute(collection: HasBaseValues): Call = routes.SqlController.adminChangeCollectionState(collection.id)

  override def editExerciseFormRoute(collection: HasBaseValues): Call = routes.SqlController.adminEditCollectionForm(collection.id)

  override def editExerciseRoute(collection: HasBaseValues): Call = routes.SqlController.adminEditCollection(collection.id)

  override def deleteExerciseRoute(collection: HasBaseValues): Call = routes.SqlController.adminDeleteCollection(collection.id)

}
