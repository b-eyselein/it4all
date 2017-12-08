package controllers.exCollections

import model.Enums.ToolState
import model.questions.{Question, QuestionConsts}
import model.{Consts, HasBaseValues}
import play.api.mvc.Call

object QuestionToolObject extends CollectionToolObject {

  override val collectionName: String = "Quiz"

  override type CompEx = Question

  override val hasTags  : Boolean   = true
  override val toolname             = "Auswahlfragen"
  override val exType               = "question"
  override val toolState: ToolState = ToolState.ALPHA
  override val consts   : Consts    = QuestionConsts

  override def indexCall: Call = routes.QuestionController.index()

  override def exerciseRoute(exercise: Question): Call = ???

  override def collectionRoute(id: Int, page: Int = 1): Call = routes.QuestionController.collection(id, page)

  override def filteredCollectionRoute(id: Int, filter: String, page: Int = 1): Call = routes.QuestionController.filteredCollection(id, filter, page)

  override def exerciseListRoute(page: Int): Call = routes.QuestionController.collectionList(page)

  override def correctLiveRoute(exercise: HasBaseValues): Call = ???

  override def correctRoute(exercise: HasBaseValues): Call = ???


  override val restHeaders: List[String] = List.empty

  override def adminIndexRoute: Call = routes.QuestionController.adminIndex()

  override def adminExesListRoute: Call = routes.QuestionController.adminExerciseList()

  override def newExFormRoute: Call = routes.QuestionController.adminNewExerciseForm()

  override def importExesRoute: Call = routes.QuestionController.adminImportExercises()

  override def exportExesRoute: Call = routes.QuestionController.adminExportExercises()

  override def exportExesAsFileRoute: Call = routes.QuestionController.adminExportExercisesAsFile()

  override def changeExStateRoute(exercise: HasBaseValues): Call = routes.QuestionController.adminChangeExState(exercise.id)

  override def editExerciseFormRoute(exercise: HasBaseValues): Call = routes.QuestionController.adminEditExerciseForm(exercise.id)

  override def editExerciseRoute(exercise: HasBaseValues): Call = routes.QuestionController.adminEditExercise(exercise.id)

  override def deleteExerciseRoute(exercise: HasBaseValues): Call = routes.QuestionController.adminDeleteExercise(exercise.id)
}
