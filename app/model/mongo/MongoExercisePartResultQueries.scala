package model.mongo

import model.points.Points
import model.result.BasicExercisePartResult
import play.api.libs.json.{Format, Json, OFormat}
import reactivemongo.api.bson.BSONDocument
import reactivemongo.api.bson.collection.BSONCollection
import reactivemongo.play.json.compat.json2bson._

import scala.concurrent.Future

trait MongoExercisePartResultQueries extends MongoRepo {

  private def futureExerciseResultsCollection: Future[BSONCollection] = futureCollection("exerciseResults")

  private implicit val basicExerciseResultFormat: OFormat[BasicExercisePartResult] = {
    implicit val pf: Format[Points] = Json.format

    Json.format
  }

  private def basicExercisePartResultKey(
    username: String,
    toolId: String,
    collectionId: Int,
    exerciseId: Int,
    partId: String
  ): BSONDocument = BSONDocument(
    "username"     -> username,
    "toolId"       -> toolId,
    "collectionId" -> collectionId,
    "exerciseId"   -> exerciseId,
    "partId"       -> partId
  )

  def futureExerciseResultById(
    username: String,
    toolId: String,
    collectionId: Int,
    exerciseId: Int,
    partId: String
  ): Future[Option[BasicExercisePartResult]] = {

    val key = basicExercisePartResultKey(username, toolId, collectionId, exerciseId, partId)

    for {
      exerciseResultsCollection <- futureExerciseResultsCollection
      maybeExerciseResult       <- exerciseResultsCollection.find(key).one[BasicExercisePartResult]
    } yield maybeExerciseResult
  }

  def futureUserHasCorrectExerciseResult(
    username: String,
    toolId: String,
    collectionId: Int,
    exerciseId: Int,
    partId: String
  ): Future[Boolean] = futureExerciseResultById(username, toolId, collectionId, exerciseId, partId).map {
    case None         => false
    case Some(result) => result.isCorrect
  }

  def futureUpsertExerciseResult(exerciseResult: BasicExercisePartResult): Future[Boolean] = {
    val key = basicExercisePartResultKey(
      exerciseResult.username,
      exerciseResult.toolId,
      exerciseResult.collectionId,
      exerciseResult.exerciseId,
      exerciseResult.partId
    )

    for {
      exerciseResultsCollection <- futureExerciseResultsCollection
      insertResult              <- exerciseResultsCollection.update.one(key, exerciseResult, upsert = true)
    } yield insertResult.writeErrors.isEmpty
  }

}
