package controllers.xml

import controllers.core.RoutesObject
import play.api.mvc.Call

import controllers.xml.routes.XmlController._
import controllers.xml.routes.XmlAdmin._

object XmlRoutesObject extends RoutesObject {

  // User

  override def correctLiveRoute(id: Int) = correctLive(id)

  override def correctRoute(id: Int) = correct(id)

  // Admin

  override val restHeaders = List("Typ", "Wurzelknoten")

  override def adminIndexRoute = adminIndex

  override def exercisesRoute = exercises

  override def newExFormRoute = newExerciseForm

  override def exportExesRoute = exportExercises

  override def importExesRoute = importExercises

  override def jsonSchemaRoute = getJSONSchemaFile

  override def uploadFileRoute = uploadFile

  override def editExerciseFormRoute(id: Int) = editExerciseForm(id)

  override def editExerciseRoute(id: Int) = editExercise(id)

  override def deleteExerciseRoute(id: Int) = deleteExercise(id)

}