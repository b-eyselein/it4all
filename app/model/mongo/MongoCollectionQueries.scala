package model.mongo

import model.{ExerciseCollection, JsonProtocols}
import play.api.libs.json.OFormat
import play.modules.reactivemongo.ReactiveMongoComponents
import reactivemongo.api.Cursor
import reactivemongo.api.bson.BSONDocument
import reactivemongo.api.bson.collection.BSONCollection
import reactivemongo.play.json.compat.json2bson._

import scala.concurrent.{ExecutionContext, Future}

trait MongoCollectionQueries {
  self: ReactiveMongoComponents =>

  protected implicit val ec: ExecutionContext

  private implicit val exerciseCollectionFormat: OFormat[ExerciseCollection] = JsonProtocols.exerciseCollectionFormat

  private def futureCollectionsCollection: Future[BSONCollection] =
    reactiveMongoApi.database.map(_.collection("exerciseCollections"))

  protected def futureCollectionCountForTool(toolId: String): Future[Long] =
    for {
      collectionCollection <- futureCollectionsCollection
      collectionCount      <- collectionCollection.count(Some(BSONDocument("toolId" -> toolId)))
    } yield collectionCount

  protected def futureCollectionsForTool(toolId: String): Future[Seq[ExerciseCollection]] =
    for {
      collectionsCollection <- futureCollectionsCollection
      collections <-
        collectionsCollection
          .find(BSONDocument("toolId" -> toolId), Option.empty[BSONDocument])
          .sort(BSONDocument("collectionId" -> 1))
          .cursor[ExerciseCollection]()
          .collect[Seq](-1, Cursor.FailOnError())
    } yield collections

  protected def futureCollectionById(toolId: String, collectionId: Int): Future[Option[ExerciseCollection]] =
    for {
      collectionCollection <- futureCollectionsCollection
      maybeCollection <-
        collectionCollection
          .find(BSONDocument("toolId" -> toolId, "collectionId" -> collectionId), Option.empty[BSONDocument])
          .one[ExerciseCollection]
    } yield maybeCollection

  protected def futureInsertCollection(exerciseCollection: ExerciseCollection): Future[Boolean] =
    for {
      collectionsCollection <- futureCollectionsCollection
      insertResult          <- collectionsCollection.insert(true).one(exerciseCollection)
    } yield insertResult.writeErrors.isEmpty

}
