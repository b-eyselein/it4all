package model.mongo

import model.tools.Tool
import model.{Exercise, ExerciseContent}
import play.api.libs.json.{Format, JsObject, Json, OFormat}
import play.modules.reactivemongo.ReactiveMongoComponents
import reactivemongo.api.{Cursor, ReadConcern}
import reactivemongo.play.json.collection.JSONCollection
import reactivemongo.play.json.compat._

import scala.concurrent.{ExecutionContext, Future}

trait MongoExerciseQueries {
  self: ReactiveMongoComponents =>

  protected implicit val ec: ExecutionContext

  private def futureExercisesCollection: Future[JSONCollection] =
    reactiveMongoApi.database.map(_.collection("exercises"))

  protected def futureExerciseCountForTool(toolId: String): Future[Long] =
    for {
      exercisesCollection <- futureExercisesCollection
      exerciseCount       <- exercisesCollection.count(Some(Json.obj("toolId" -> toolId)), None, 0, None, ReadConcern.Local)
    } yield exerciseCount

  protected def futureExercisesForTool(tool: Tool): Future[Seq[Exercise[tool.ExContentType]]] = {
    implicit val ef: Format[Exercise[tool.ExContentType]] = tool.jsonFormats.exerciseFormat

    for {
      exercisesCollection <- futureExercisesCollection
      exercises <-
        exercisesCollection
          .find(Json.obj("toolId" -> tool.id), Option.empty[JsObject])
          .cursor[Exercise[tool.ExContentType]]()
          .collect[Seq](-1, Cursor.FailOnError())
    } yield exercises
  }

  protected def futureExerciseCountForCollection(toolId: String, collectionId: Int): Future[Long] =
    for {
      exercisesCollection <- futureExercisesCollection
      exerciseCount <-
        exercisesCollection
          .count(Some(Json.obj("toolId" -> toolId, "collectionId" -> collectionId)), None, 0, None, ReadConcern.Local)
    } yield exerciseCount

  protected def futureExercisesForCollection(
    tool: Tool,
    collectionId: Int
  ): Future[Seq[Exercise[tool.ExContentType]]] = {
    implicit val ef: Format[Exercise[tool.ExContentType]] = tool.jsonFormats.exerciseFormat

    for {
      exercisesCollection <- futureExercisesCollection
      exercises <-
        exercisesCollection
          .find(Json.obj("toolId" -> tool.id, "collectionId" -> collectionId), Option.empty[JsObject])
          .sort(Json.obj("exerciseId" -> 1))
          .cursor[Exercise[tool.ExContentType]]()
          .collect[Seq](-1, Cursor.FailOnError())
    } yield exercises
  }

  protected def futureExerciseExists(toolId: String, collectionId: Int, exerciseId: Int): Future[Boolean] =
    for {
      exercisesCollection <- futureExercisesCollection
      maybeExercise <-
        exercisesCollection
          .find(
            Json.obj("toolId" -> toolId, "collectionId" -> collectionId, "exerciseId" -> exerciseId),
            Option.empty[JsObject]
          )
          .one[JsObject]
    } yield maybeExercise.isDefined

  protected def futureExerciseById(
    tool: Tool,
    collectionId: Int,
    exerciseId: Int
  ): Future[Option[Exercise[tool.ExContentType]]] = {
    implicit val ef: OFormat[Exercise[tool.ExContentType]] = tool.jsonFormats.exerciseFormat

    for {
      exercisesCollection <- futureExercisesCollection
      maybeExercise <-
        exercisesCollection
          .find(
            Json.obj("toolId" -> tool.id, "collectionId" -> collectionId, "exerciseId" -> exerciseId),
            Option.empty[JsObject]
          )
          .one[Exercise[tool.ExContentType]]
    } yield maybeExercise
  }

  protected def futureInsertExercise[EC <: ExerciseContent](
    exercise: Exercise[EC],
    exerciseFormat: OFormat[Exercise[EC]]
  ): Future[Boolean] = {
    implicit val ef: OFormat[Exercise[EC]] = exerciseFormat

    for {
      exercisesCollection <- futureExercisesCollection
      insertResult        <- exercisesCollection.insert(true).one(exercise)
    } yield insertResult.ok
  }

}
