package model.mongo

import model.ExerciseCollection
import model.json.JsonProtocols
import play.api.libs.json.{JsObject, Json, OFormat}
import play.modules.reactivemongo.ReactiveMongoComponents
import reactivemongo.api.{Cursor, ReadConcern}
import reactivemongo.play.json._
import reactivemongo.play.json.collection.JSONCollection

import scala.concurrent.{ExecutionContext, Future}

trait MongoCollectionQueries {
  self: ReactiveMongoComponents =>

  protected implicit val ec: ExecutionContext

  private implicit val exerciseCollectionFormat: OFormat[ExerciseCollection] = JsonProtocols.exerciseCollectionFormat

  private def futureCollectionsCollection: Future[JSONCollection] =
    reactiveMongoApi.database.map(_.collection("exerciseCollections"))

  protected def futureCollectionCountForTool(toolId: String): Future[Long] =
    for {
      collectionCollection <- futureCollectionsCollection
      collectionCount <-
        collectionCollection.count(Some(Json.obj("toolId" -> toolId)), None, 0, None, ReadConcern.Local)
    } yield collectionCount

  protected def futureCollectionsForTool(toolId: String): Future[Seq[ExerciseCollection]] =
    for {
      collectionsCollection <- futureCollectionsCollection
      collections <-
        collectionsCollection
          .find(Json.obj("toolId" -> toolId), Option.empty[JsObject])
          .sort(Json.obj("collectionId" -> 1))
          .cursor[ExerciseCollection]()
          .collect[Seq](-1, Cursor.FailOnError())
    } yield collections

  protected def futureCollectionById(toolId: String, collectionId: Int): Future[Option[ExerciseCollection]] =
    for {
      collectionCollection <- futureCollectionsCollection
      maybeCollection <-
        collectionCollection
          .find(Json.obj("toolId" -> toolId, "collectionId" -> collectionId), Option.empty[JsObject])
          .one[ExerciseCollection]
    } yield maybeCollection

  protected def futureInsertCollection(exerciseCollection: ExerciseCollection): Future[Boolean] =
    for {
      collectionsCollection <- futureCollectionsCollection
      insertResult          <- collectionsCollection.insert(true).one(exerciseCollection)
    } yield insertResult.ok

}
