package controllers.sql

import model.Enums.ToolState
import model.core.tools.IdExToolObject
import play.api.mvc.Call

object SqlToolObject extends IdExToolObject("sql", "Sql", ToolState.LIVE, "Szenarien") {

  // User

  override def indexCall: Call = controllers.sql.routes.SqlController.index()

  override def exerciseRoute(id: Int): Call = ???

  override def exesListRoute(id: Int): Call = ??? // controllers.sql.routes.SqlAdmin.exesListRoute(id)

  override def correctLiveRoute(id: Int): Call = ???

  override def correctRoute(id: Int): Call = ???

  // Admin

  val restHeaders: List[String] = List.empty

  override def adminIndexRoute: Call = controllers.sql.routes.SqlAdmin.adminIndex()

  override def exercisesRoute: Call = controllers.sql.routes.SqlAdmin.exercises()

  override def newExFormRoute: Call = controllers.sql.routes.SqlAdmin.newExerciseForm()

  override def exportExesRoute: Call = controllers.sql.routes.SqlAdmin.exportExercises()

  override def importExesRoute: Call = controllers.sql.routes.SqlAdmin.importExercises()

  override def jsonSchemaRoute: Call = controllers.sql.routes.SqlAdmin.getJSONSchemaFile()

  override def uploadFileRoute: Call = controllers.sql.routes.SqlAdmin.uploadFile()

  override def changeExStateRoute(id: Int): Call = controllers.sql.routes.SqlAdmin.changeExState(id)

  override def editExerciseFormRoute(id: Int): Call = controllers.sql.routes.SqlAdmin.editExerciseForm(id)

  override def editExerciseRoute(id: Int): Call = controllers.sql.routes.SqlAdmin.editExercise(id)

  override def deleteExerciseRoute(id: Int): Call = controllers.sql.routes.SqlAdmin.deleteExercise(id)

}