package controllers.ebnf

import controllers.ebnf.routes.EBNFAdmin._
import controllers.ebnf.routes.EBNFController._
import model.tools.IdedExToolObject
import model.tools.ToolState

object EBNFToolObject extends IdedExToolObject("EBNF", ToolState.ALPHA) {

  // User
  
  override def indexCall = index()

  override def exerciseRoute(id: Int) = exercise(id)
  
  
  
  override def exesListRoute(id: Int) = exesListRoute(id)

  override def correctLiveRoute(id: Int) = null

  override def correctRoute(id: Int) = null

  // Admin

  val restHeaders = List("Terminalsymbole")
  
  override def adminIndexRoute = adminIndex

  override def exercisesRoute = exercises

  override def newExFormRoute = newExerciseForm

  override def exportExesRoute = exportExercises

  override def importExesRoute = importExercises

  override def jsonSchemaRoute = getJSONSchemaFile

  override def uploadFileRoute = uploadFile

  override def changeExStateRoute(id: Int) = changeExState(id)

  override def editExerciseFormRoute(id: Int) = editExerciseForm(id)

  override def editExerciseRoute(id: Int) = editExercise(id)

  override def deleteExerciseRoute(id: Int) = deleteExercise(id)

}