package controllers.spread

import controllers.spread.routes.SpreadAdmin._
import controllers.core.RoutesObject

object SpreadRoutesObject extends RoutesObject {

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