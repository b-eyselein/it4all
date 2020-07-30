package model.mongo

import model.{Exercise, JsonProtocols}
import model.result.BasicExercisePartResult
import play.api.libs.json.OFormat
import play.modules.reactivemongo.ReactiveMongoComponents
import reactivemongo.api.bson.BSONDocument
import reactivemongo.api.bson.collection.BSONCollection
import reactivemongo.play.json.compat.json2bson._

import scala.concurrent.{ExecutionContext, Future}

trait MongoExercisePartResultQueries {
  self: ReactiveMongoComponents =>

  protected implicit val ec: ExecutionContext

  private def futureExerciseResultsCollection: Future[BSONCollection] =
    reactiveMongoApi.database.map(_.collection("exerciseResults"))

  private implicit val basicExerciseResultFormat: OFormat[BasicExercisePartResult] =
    JsonProtocols.basicExerciseResultFormat

  private def basicExercisePartResultKey(
    username: String,
    toolId: String,
    collectionId: Int,
    exerciseId: Int,
    partId: String
  ): BSONDocument =
    BSONDocument(
      "username"     -> username,
      "toolId"       -> toolId,
      "collectionId" -> collectionId,
      "exerciseId"   -> exerciseId,
      "partId"       -> partId
    )

  protected def futureExerciseResultById(
    username: String,
    exercise: Exercise[_],
    partId: String
  ): Future[Option[BasicExercisePartResult]] = {

    val key = basicExercisePartResultKey(username, exercise.toolId, exercise.collectionId, exercise.exerciseId, partId)

    for {
      exerciseResultsCollection <- futureExerciseResultsCollection
      maybeExerciseResult <-
        exerciseResultsCollection
          .find(key, Option.empty[BSONDocument])
          .one[BasicExercisePartResult]
    } yield maybeExerciseResult
  }

  protected def futureUpsertExerciseResult(exerciseResult: BasicExercisePartResult): Future[Boolean] = {
    val key = basicExercisePartResultKey(
      exerciseResult.username,
      exerciseResult.toolId,
      exerciseResult.collectionId,
      exerciseResult.exerciseId,
      exerciseResult.partId
    )

    for {
      exerciseResultsCollection <- futureExerciseResultsCollection
      insertResult              <- exerciseResultsCollection.update(true).one(key, exerciseResult, upsert = true)
    } yield insertResult.writeErrors.isEmpty
  }

}
