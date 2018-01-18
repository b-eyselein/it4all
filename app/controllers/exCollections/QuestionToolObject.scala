package controllers.exCollections

import model.Enums.ToolState
import model.core.tools.CollectionToolObject
import model.questions.{CompleteQuestion, QuestionConsts}
import model.{Consts, HasBaseValues}
import play.api.mvc.Call

object QuestionToolObject extends CollectionToolObject {

  override type CompEx = CompleteQuestion

  override val hasTags               : Boolean   = true
  override val hasExType             : Boolean   = true
  override val toolname              : String    = "Auswahlfragen"
  override val collectionSingularName: String    = "Quiz"
  override val collectionPluralName  : String    = "Quizze"
  override val exType                : String    = "question"
  override val consts                : Consts    = QuestionConsts
  override val toolState             : ToolState = ToolState.ALPHA

  override def indexCall: Call = routes.QuestionController.index()

  override def exerciseRoute(collectionId: Int, exerciseId: Int): Call = routes.QuestionController.exercise(collectionId, exerciseId)

  override def collectionRoute(id: Int, page: Int = 1): Call = routes.QuestionController.collection(id, page)

  override def filteredCollectionRoute(id: Int, filter: String, page: Int = 1): Call = routes.QuestionController.filteredCollection(id, filter, page)

  override def exerciseListRoute(page: Int): Call = routes.QuestionController.collectionList(page)

  override def correctLiveRoute(exercise: HasBaseValues): Call = ???

  override def correctRoute(exercise: HasBaseValues): Call = ???


  override def adminIndexRoute: Call = routes.QuestionController.adminIndex()

  override def adminExesListRoute: Call = routes.QuestionController.adminCollectionsList()

  override def newExFormRoute: Call = routes.QuestionController.adminNewCollectionForm()

  override def createNewExRoute: Call = routes.QuestionController.adminCreateCollection()

  override def importExesRoute: Call = routes.QuestionController.adminImportCollections()

  override def exportExesRoute: Call = routes.QuestionController.adminExportCollections()

  override def exportExesAsFileRoute: Call = routes.QuestionController.adminExportCollectionsAsFile()

  override def changeExStateRoute(exercise: HasBaseValues): Call = routes.QuestionController.adminChangeCollectionState(exercise.id)

  override def editExerciseFormRoute(exercise: HasBaseValues): Call = routes.QuestionController.adminEditCollectionForm(exercise.id)

  override def editExerciseRoute(exercise: HasBaseValues): Call = routes.QuestionController.adminEditCollection(exercise.id)

  override def deleteExerciseRoute(exercise: HasBaseValues): Call = routes.QuestionController.adminDeleteCollection(exercise.id)
}
