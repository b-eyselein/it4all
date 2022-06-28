package model.mongo

import model.ExerciseCollection
import play.api.libs.json.{Json, OFormat}
import reactivemongo.api.bson.BSONDocument
import reactivemongo.api.bson.collection.BSONCollection
import reactivemongo.play.json.compat.json2bson._

import scala.concurrent.Future

trait MongoCollectionQueries extends MongoRepo {

  private implicit val exerciseCollectionFormat: OFormat[ExerciseCollection] = Json.format

  private def futureCollectionsCollection: Future[BSONCollection] = futureCollection("exerciseCollections")

  def futureCollectionCountForTool(toolId: String): Future[Long] = for {
    collectionCollection <- futureCollectionsCollection
    collectionCount      <- collectionCollection.count(Some(BSONDocument("toolId" -> toolId)))
  } yield collectionCount

  def futureCollectionsForTool(toolId: String): Future[Seq[ExerciseCollection]] = for {
    collectionsCollection <- futureCollectionsCollection
    collections <-
      collectionsCollection
        .find(BSONDocument("toolId" -> toolId))
        .sort(BSONDocument("collectionId" -> 1))
        .cursor[ExerciseCollection]()
        .collect[Seq]()
  } yield collections

  def futureCollectionById(toolId: String, collectionId: Int): Future[Option[ExerciseCollection]] = for {
    collectionCollection <- futureCollectionsCollection
    maybeCollection      <- collectionCollection.find(BSONDocument("toolId" -> toolId, "collectionId" -> collectionId)).one[ExerciseCollection]
  } yield maybeCollection

  def futureInsertCollection(exerciseCollection: ExerciseCollection): Future[Boolean] = for {
    collectionsCollection <- futureCollectionsCollection
    insertResult          <- collectionsCollection.insert(true).one(exerciseCollection)
  } yield insertResult.writeErrors.isEmpty

}
