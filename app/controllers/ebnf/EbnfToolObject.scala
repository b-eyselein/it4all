package controllers.ebnf

import model.Enums.ToolState
import model.core.tools.IdExToolObject
import play.api.mvc.Call

object EbnfToolObject extends IdExToolObject("ebnf", "Ebnf", ToolState.ALPHA) {

  // User

  override def indexCall: Call = controllers.ebnf.routes.EbnfController.index()

  override def exerciseRoute(id: Int): Call = controllers.ebnf.routes.EbnfController.exercise(id)

  override def exesListRoute(id: Int): Call = ??? // controllers.ebnf.routes.EbnfController.exesListRoute(id)

  override def correctLiveRoute(id: Int): Call = ???

  override def correctRoute(id: Int): Call = ???

  // Admin

  val restHeaders: List[String] = List("Terminalsymbole")

  override def adminIndexRoute: Call = controllers.ebnf.routes.EbnfAdmin.adminIndex()

  override def exercisesRoute: Call = controllers.ebnf.routes.EbnfAdmin.exercises()

  override def newExFormRoute: Call = controllers.ebnf.routes.EbnfAdmin.newExerciseForm()

  override def exportExesRoute: Call = controllers.ebnf.routes.EbnfAdmin.exportExercises()

  override def importExesRoute: Call = controllers.ebnf.routes.EbnfAdmin.importExercises()

  override def jsonSchemaRoute: Call = controllers.ebnf.routes.EbnfAdmin.getJSONSchemaFile()

  override def uploadFileRoute: Call = controllers.ebnf.routes.EbnfAdmin.uploadFile()

  override def changeExStateRoute(id: Int): Call = controllers.ebnf.routes.EbnfAdmin.changeExState(id)

  override def editExerciseFormRoute(id: Int): Call = controllers.ebnf.routes.EbnfAdmin.editExerciseForm(id)

  override def editExerciseRoute(id: Int): Call = controllers.ebnf.routes.EbnfAdmin.editExercise(id)

  override def deleteExerciseRoute(id: Int): Call = controllers.ebnf.routes.EbnfAdmin.deleteExercise(id)

}