package controllers.programming

import controllers.programming.routes.ProgController._
import controllers.programming.routes.ProgAdmin._
import controllers.core.RoutesObject

object ProgRoutesObject extends RoutesObject {

  // User

  override def correctLiveRoute(id: Int) = correctLive(id)

  override def correctRoute(id: Int) = correct(id)

  // Admin

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