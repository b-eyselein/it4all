package controllers.web

import controllers.web.routes.WebAdmin._
import controllers.core.RoutesObject

object WebRoutesObject extends RoutesObject {

  val adminIndexRoute = adminIndex

  val exercisesRoutes = exercises

  val newExFormRoute = newExerciseForm

  val exportExesRoute = exportExercises

  val importExesRoute = importExercises

  val jsonSchemaRoute = getJSONSchemaFile

  val uploadFileRoute = uploadFile

  override def editExerciseFormRoute(id: Int) = editExerciseForm(id)

  override def editExerciseRoute(id: Int) = editExercise(id)

  override def deleteExerciseRoute(id: Int) = deleteExercise(id)

}
