package controllers.exCollections

import model.Enums.ToolState
import model.questions.{Question, QuestionConsts, Quiz}
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

  override def indexCall: Call = controllers.exCollections.routes.QuestionController.index()

  override def exerciseRoute(exercise: Question): Call = ???

  override def exerciseListRoute(page: Int): Call = controllers.exCollections.routes.QuestionController.exerciseList(page)

  override def correctLiveRoute(exercise: HasBaseValues): Call = ???

  override def correctRoute(exercise: HasBaseValues): Call = ???


  override val restHeaders: List[String] = List.empty

  override def adminIndexRoute: Call = controllers.exCollections.routes.QuestionController.adminIndex()

  override def adminExesListRoute: Call = controllers.exCollections.routes.QuestionController.adminExerciseList()

  override def newExFormRoute: Call = controllers.exCollections.routes.QuestionController.adminNewExerciseForm()

  override def importExesRoute: Call = controllers.exCollections.routes.QuestionController.adminImportExercises()

  override def exportExesRoute: Call = controllers.exCollections.routes.QuestionController.adminExportExercises()

  override def exportExesAsFileRoute: Call = controllers.exCollections.routes.QuestionController.adminExportExercisesAsFile()

  override def changeExStateRoute(exercise: HasBaseValues): Call = controllers.exCollections.routes.QuestionController.adminChangeExState(exercise.id)

  override def editExerciseFormRoute(exercise: HasBaseValues): Call = controllers.exCollections.routes.QuestionController.adminEditExerciseForm(exercise.id)

  override def editExerciseRoute(exercise: HasBaseValues): Call = controllers.exCollections.routes.QuestionController.adminEditExercise(exercise.id)

  override def deleteExerciseRoute(exercise: HasBaseValues): Call = controllers.exCollections.routes.QuestionController.adminDeleteExercise(exercise.id)
}
