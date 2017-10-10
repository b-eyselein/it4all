package controllers.questions

import model.tools.{IdExToolObject, ToolState}
import play.api.Configuration
import play.api.mvc.Call

class QuestionToolObject(c: Configuration) extends IdExToolObject(c, "question", "Auswahlfragen", ToolState.BETA) {

  // User

  override def indexCall: Call = controllers.questions.routes.QuestionController.index()

  def exerciseRoute(id: Int): Call = ???

  override def exesListRoute(id: Int): Call = ???

  override def correctLiveRoute(id: Int): Call = ???

  override def correctRoute(id: Int): Call = ???

  // Admin

  val restHeaders: List[String] = List.empty

  override def adminIndexRoute: Call = controllers.questions.routes.QuestionAdmin.adminIndex()

  override def exercisesRoute: Call = controllers.questions.routes.QuestionAdmin.exercises()

  override def newExFormRoute: Call = controllers.questions.routes.QuestionAdmin.newExerciseForm()

  override def exportExesRoute: Call = controllers.questions.routes.QuestionAdmin.exportExercises()

  override def importExesRoute: Call = controllers.questions.routes.QuestionAdmin.importExercises()

  override def jsonSchemaRoute: Call = controllers.questions.routes.QuestionAdmin.getJSONSchemaFile()

  override def uploadFileRoute: Call = controllers.questions.routes.QuestionAdmin.uploadFile()

  override def changeExStateRoute(id: Int): Call = controllers.questions.routes.QuestionAdmin.changeExState(id)

  override def editExerciseFormRoute(id: Int): Call = controllers.questions.routes.QuestionAdmin.editExerciseForm(id)

  override def editExerciseRoute(id: Int): Call = controllers.questions.routes.QuestionAdmin.editExercise(id)

  override def deleteExerciseRoute(id: Int): Call = controllers.questions.routes.QuestionAdmin.deleteExercise(id)

}