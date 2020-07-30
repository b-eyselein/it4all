package model.mongo

import model.JsonProtocols
import model.result.BasicExercisePartResult
import play.api.libs.json.{JsObject, Json, OFormat}
import play.modules.reactivemongo.ReactiveMongoComponents
import reactivemongo.play.json.collection.JSONCollection
import reactivemongo.play.json.compat._

import scala.concurrent.{ExecutionContext, Future}

trait MongoExercisePartResultQueries {
  self: ReactiveMongoComponents =>

  protected implicit val ec: ExecutionContext

  private def futureExerciseResultsCollection: Future[JSONCollection] =
    reactiveMongoApi.database.map(_.collection("exerciseResults"))

  private implicit val basicExerciseResultFormat: OFormat[BasicExercisePartResult] =
    JsonProtocols.basicExerciseResultFormat

  private def basicExercisePartResultKey[P](
    toolId: String,
    collectionId: Int,
    exerciseId: Int,
    partId: String
  ) =
    Json.obj(
      "toolId"       -> toolId,
      "collectionId" -> collectionId,
      "exerciseId"   -> exerciseId,
      "part"         -> partId
    )

  protected def futureExerciseResultById(
    toolId: String,
    collectionId: Int,
    exerciseId: Int,
    partId: String
  ): Future[Option[BasicExercisePartResult]] = {

    for {
      exerciseResultsCollection <- futureExerciseResultsCollection
      maybeExerciseResult <-
        exerciseResultsCollection
          .find(basicExercisePartResultKey(toolId, collectionId, exerciseId, partId), Option.empty[JsObject])
          .one[BasicExercisePartResult]
    } yield maybeExerciseResult
  }

  protected def futureUpsertExerciseResult(exerciseResult: BasicExercisePartResult): Future[Boolean] = {
    val key = basicExercisePartResultKey(
      exerciseResult.toolId,
      exerciseResult.collectionId,
      exerciseResult.exerciseId,
      exerciseResult.partId
    )

    for {
      exerciseResultsCollection <- futureExerciseResultsCollection
      insertResult              <- exerciseResultsCollection.update(true).one(key, exerciseResult, upsert = true)
    } yield insertResult.ok
  }

}
