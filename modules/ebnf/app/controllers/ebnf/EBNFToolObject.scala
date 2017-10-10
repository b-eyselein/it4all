package controllers.ebnf

import model.tools.{IdExToolObject, ToolState}
import play.api.Configuration
import play.mvc.Call

class EBNFToolObject(c: Configuration) extends IdExToolObject(c, "ebnf", "Ebnf", ToolState.ALPHA) {

  // User

  override def indexCall: Call = controllers.ebnf.routes.EBNFController.index()

  override def exerciseRoute(id: Int): Call = controllers.ebnf.routes.EBNFController.exercise(id)

  override def exesListRoute(id: Int): Call = ??? // controllers.ebnf.routes.EBNFController.exesListRoute(id)

  override def correctLiveRoute(id: Int): Call = ???

  override def correctRoute(id: Int): Call = ???

  // Admin

  val restHeaders: List[String] = List("Terminalsymbole")

  override def adminIndexRoute: Call = controllers.ebnf.routes.EBNFAdmin.adminIndex()

  override def exercisesRoute: Call = controllers.ebnf.routes.EBNFAdmin.exercises()

  override def newExFormRoute: Call = controllers.ebnf.routes.EBNFAdmin.newExerciseForm()

  override def exportExesRoute: Call = controllers.ebnf.routes.EBNFAdmin.exportExercises()

  override def importExesRoute: Call = controllers.ebnf.routes.EBNFAdmin.importExercises()

  override def jsonSchemaRoute: Call = controllers.ebnf.routes.EBNFAdmin.getJSONSchemaFile()

  override def uploadFileRoute: Call = controllers.ebnf.routes.EBNFAdmin.uploadFile()

  override def changeExStateRoute(id: Int): Call = controllers.ebnf.routes.EBNFAdmin.changeExState(id)

  override def editExerciseFormRoute(id: Int): Call = controllers.ebnf.routes.EBNFAdmin.editExerciseForm(id)

  override def editExerciseRoute(id: Int): Call = controllers.ebnf.routes.EBNFAdmin.editExercise(id)

  override def deleteExerciseRoute(id: Int): Call = controllers.ebnf.routes.EBNFAdmin.deleteExercise(id)

}