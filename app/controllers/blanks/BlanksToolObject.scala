package controllers.blanks

import model.core.tools.IdExToolObject

object BlanksToolObject extends IdExToolObject("blanks", "Lueckentext") {

  override def exerciseRoute(id: Int) = ???

  override def correctLiveRoute(id: Int) = ???

  override def correctRoute(id: Int) = ???

  override def exesListRoute(page: Int) = ???

  override def restHeaders = ???

  override def adminIndexRoute = ???

  override def exercisesRoute = ???

  override def newExFormRoute = ???

  override def exportExesRoute = ???

  override def importExesRoute = ???

  override def jsonSchemaRoute = ???

  override def uploadFileRoute = ???

  override def changeExStateRoute(id: Int) = ???

  override def editExerciseFormRoute(id: Int) = ???

  override def editExerciseRoute(id: Int) = ???

  override def deleteExerciseRoute(id: Int) = ???

  override def indexCall = ???

}
