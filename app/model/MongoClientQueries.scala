package model

import model.json.JsonProtocols
import model.tools.{CollectionTool, Exercise, ExerciseCollection}
import play.api.libs.json.{Format, JsObject, Json}
import reactivemongo.api.{Cursor, DefaultDB, ReadConcern}
import reactivemongo.play.json._
import reactivemongo.play.json.collection.JSONCollection

import scala.concurrent.{ExecutionContext, Future}

object MongoClientQueries {

  private implicit val exerciseCollectionFormat: Format[ExerciseCollection] = JsonProtocols.collectionFormat

  private val exerciseCollectionCollectionName = "exercise_collections"
  private val exercisesCollectionName          = "exercises"

  private def toolFilter(toolId: String): JsObject = Json.obj(
    "toolId" -> toolId
  )

  private def collectionFilter(toolId: String, collectionId: Int): JsObject = Json.obj(
    "toolId"       -> toolId,
    "collectionId" -> collectionId
  )

  private def exerciseFilter(toolId: String, collectionId: Int, exerciseId: Int): JsObject = Json.obj(
    "toolId"       -> toolId,
    "collectionId" -> collectionId,
    "exerciseId"   -> exerciseId
  )

  // Collection queries

  def getExerciseCountForTool(
    defaultDB: Future[DefaultDB],
    toolId: String
  )(implicit ec: ExecutionContext): Future[Long] =
    defaultDB.flatMap(
      _.collection[JSONCollection](exercisesCollectionName)
        .count(
          selector = Some(toolFilter(toolId)),
          limit = None,
          skip = 0,
          hint = None,
          readConcern = ReadConcern.Local
        )
    )

  def getCollectionCount(
    defaultDB: Future[DefaultDB],
    toolId: String
  )(implicit ec: ExecutionContext): Future[Long] =
    defaultDB.flatMap(
      _.collection[JSONCollection](exerciseCollectionCollectionName)
        .count(
          selector = Some(toolFilter(toolId)),
          limit = None,
          skip = 0,
          hint = None,
          readConcern = ReadConcern.Local
        )
    )

  def getExerciseCollections(
    defaultDB: Future[DefaultDB],
    toolId: String
  )(implicit ec: ExecutionContext): Future[Seq[ExerciseCollection]] =
    defaultDB
      .map(
        _.collection[JSONCollection](exerciseCollectionCollectionName)
          .find(toolFilter(toolId))
          .cursor[ExerciseCollection]()
      )
      .flatMap(_.collect[Seq](-1, Cursor.FailOnError()))

  def getExerciseCollection(
    defaultDB: Future[DefaultDB],
    toolId: String,
    collectionId: Int
  )(implicit ec: ExecutionContext): Future[Option[ExerciseCollection]] =
    defaultDB.flatMap(
      _.collection[JSONCollection](exerciseCollectionCollectionName)
        .find(collectionFilter(toolId, collectionId))
        .one[ExerciseCollection]
    )

  // Exercise queries

  def getExerciseCountForCollection(
    defaultDB: Future[DefaultDB],
    toolId: String,
    collectionId: Int
  )(implicit ec: ExecutionContext): Future[Long] =
    defaultDB
      .flatMap(
        _.collection[JSONCollection](exercisesCollectionName)
          .count(
            selector = Some(collectionFilter(toolId, collectionId)),
            limit = None,
            skip = 0,
            hint = None,
            readConcern = ReadConcern.Local
          )
      )

  def getExercisesForCollection(
    defaultDB: Future[DefaultDB],
    tool: CollectionTool,
    collectionId: Int
  )(implicit ec: ExecutionContext): Future[Seq[Exercise[tool.SolType, tool.ExContentType]]] = {
    implicit val exerciseFormat: Format[Exercise[tool.SolType, tool.ExContentType]] =
      tool.toolJsonProtocol.exerciseFormat

    defaultDB
      .map(
        _.collection[JSONCollection](exercisesCollectionName)
          .find(collectionFilter(tool.id, collectionId))
          .cursor[Exercise[tool.SolType, tool.ExContentType]]()
      )
      .flatMap(_.collect[Seq](-1, Cursor.FailOnError()))
  }

}
